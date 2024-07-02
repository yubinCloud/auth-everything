package org.inet.aet.chatanalysis.service.impl.ai_backend

import dev.langchain4j.model.chat.ChatLanguageModel
import dev.langchain4j.model.input.PromptTemplate
import dev.langchain4j.model.openai.OpenAiChatModel
import dev.langchain4j.service.AiServices
import org.inet.aet.chatanalysis.config.AIServProperty
import org.inet.aet.chatanalysis.constant.AIServBackendFuncEnum
import org.inet.aet.chatanalysis.constant.ChartTypesEnum
import org.inet.aet.chatanalysis.dto.request.Chat2ChartRequest
import org.inet.aet.chatanalysis.entity.ChartConf
import org.inet.aet.chatanalysis.entity.Chat2SQLResult
import org.inet.aet.chatanalysis.entity.ChatAnalysisResult
import org.inet.aet.chatanalysis.entity.ChatAnalysisResultFactory
import org.inet.aet.chatanalysis.service.impl.DatasourceService
import org.inet.aet.chatanalysis.service.itfce.AIBackendService
import org.inet.aet.chatanalysis.util.CommonDBSchemaSerializer
import org.inet.aet.chatanalysis.util.DBSchemaSerializer
import org.slf4j.LoggerFactory
import java.util.regex.Pattern


private interface OpenaiAssistant {
    fun chat(userMessage: String): String
}

/**
 * NL2SQL 的 prompt
 */
private const val NL2SQL_PROMPT_TEMPLATE_STRING = """你需要完成 NL2SQL 任务，根据一个自然语言问句（NL）来生成对应的 SQL 查询。按照下面的示例格式来回答问题，只需要输出 SQL 即可，不需要输出其他任何额外内容。可以参考数据库的元信息。

### 如下是一些 NL2SQL 的示例：

[NL]: 找出2024年入学的学生中年龄最大的人？
[SQL]: SELECT name FROM student ORDER BY year LIMIT 1;

[NL]: 2023年的学生中男生有多少人？
[SQL]: SELECT COUNT(*) FROM student WHERE sex='man';

### 下面是数据库的元信息，请依据此模式信息完成 SQL 编写：

{{db-schema}}

### 请参考前面示例的形式，生成下面自然语言问句的对应 SQL 查询

[NL]: {{question}}
[SQL]: """

/**
 * Chart Type Classify 的 prompt
 */
private const val CHART_TYPE_CLASSIFY_TEMPLATE_STRING = """你需要完成下面这个任务：给定一个自然语言问题（Q）以及对应的 SQL 查询，选出合适的 echarts 图表类型来展示这个 SQL 的查询结果，能够将其用于回答这个 question。

echarts 图表类型包括：bar（柱状图）、line（折线图）、pie（饼状图）、text（文字回复）、raw（以关系表格回复）

### 如下是一个任务示例，请你根据示例来完成任务，只需要输出 type 而不需要输出其他任何文本：

[Q]: 找出2024年入学的学生中年龄最大的人？
[SQL]: SELECT COUNT(*) FROM student WHERE sex='man';
[Type]: raw

[Q]：统计每年新生数量的变化趋势
[SQL]：SELECT year, COUNT(*) AS num FROM student GROUP BY year ORDER BY year;
[Type]: line

[Q]：展示当前学校中学生的男女比例
[SQL]: 
SELECT
    ROUND((SUM(CASE WHEN gender = 'male' THEN 1 ELSE 0 END) / COUNT(*)) * 100, 2) AS male_percentage,
    ROUND((SUM(CASE WHEN gender = 'female' THEN 1 ELSE 0 END) / COUNT(*)) * 100, 2) AS female_percentage
FROM
    students;
[Type]: pie

[Q]: {{question}}
[SQL]:
{{SQL}}
[Type]: """

/**
 * 以 OpenAI 作为 backend
 *
 * 需要在配置信息中填写 openai 相关字段
 */
class OpenAIBasicBackendService (aiServProperty: AIServProperty, private val datasourceService: DatasourceService): AIBackendService {

    private var assistant: OpenaiAssistant  // 基于 OpenAI LM 的对话助手

    private var dbSchemaSerializer: DBSchemaSerializer = CommonDBSchemaSerializer()   // 用于序列化 db schema

    companion object {
        // 用于 NL2SQL 的 prompt template
        private val NL2SQL_PROMPT_TEMPLATE: PromptTemplate = PromptTemplate.from(NL2SQL_PROMPT_TEMPLATE_STRING)

        // 从 markdown 文件中提取 SQL block 的 regex pattern
        private val MARKDOWN_SQL_EXTRACT_PATTERN: Pattern = Pattern.compile("```(?i)sql\\s*\\n([\\s\\S]*?)```")

        private val logger = LoggerFactory.getLogger(this::class.java)
    }

    init {
        /**
         * 初始化 assistant
         */
        val prop = aiServProperty.openai
        val openaiBuilder = OpenAiChatModel.builder()
        if (prop.modelName != null) {
            println(prop.modelName)
            openaiBuilder.modelName(prop.modelName)
        }
        if (prop.apiKey != null) {
            openaiBuilder.apiKey(prop.apiKey)
        } else {
            openaiBuilder.apiKey("demo")
        }
        if (prop.baseUrl != null) {
            println(prop.baseUrl)
            openaiBuilder.baseUrl(prop.baseUrl)
        }
        val chatLM: ChatLanguageModel = openaiBuilder.build()
        assistant = AiServices.create(OpenaiAssistant::class.java, chatLM)
    }

    override fun supportFunctions(): Set<AIServBackendFuncEnum> {
        return setOf(AIServBackendFuncEnum.NL2CHART)
    }

    override fun nl2chart(chatReq: Chat2ChartRequest): ChatAnalysisResult {
        // 首先进行 NL2SQL
        val dbSchema = datasourceService.getDBSchema(chatReq.dataSourceConf)
        val dbSchemaText = dbSchemaSerializer.serializeDBSchema(dbSchema)
        val prompt = NL2SQL_PROMPT_TEMPLATE.apply(mapOf(
            "question" to chatReq.input,
            "db-schema" to dbSchemaText
        ))
        val answer = assistant.chat(prompt.toUserMessage().toString())
        val sql = extractSQLFromLLMAnswer(answer)
        logger.info(mapOf("Q" to chatReq.input, "LLM ans" to answer, "SQL" to sql).toString())
        if (sql == null) {
            return ChatAnalysisResultFactory.fail("SQL 抽取失败")
        }
        // TODO: 然后进行 chart type classify
        return ChatAnalysisResultFactory.success(
            ChartConf(ChartTypesEnum.RAW_TABLE),
            sql,
            null,
            listOf()
        )
    }

    override fun nl2sql(chatReq: Chat2ChartRequest): Chat2SQLResult {
        TODO("Not yet implemented")
    }

    /**
     * 从 LLM 的 answer 中提取出 SQL
     *
     * answer example:
     *
     * ```sql
     * SELECT * FROM user;
     * ```
     *
     * 需要提取出 ``` 中间的 SQL 语句，并去掉左右两边的空格
     */
    private fun extractSQLFromLLMAnswer(answer: String): String? {
        if (answer.startsWith("SELECT")) {
            return answer
        }
        val matcher = MARKDOWN_SQL_EXTRACT_PATTERN.matcher(answer)
        if (!matcher.find()) {
            return null
        }
        val sqlBlock = matcher.group(1)
        return sqlBlock.trim()
    }
}