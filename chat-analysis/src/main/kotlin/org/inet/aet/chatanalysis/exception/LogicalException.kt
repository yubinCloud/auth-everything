package org.inet.aet.chatanalysis.exception

enum class Err (val errCode: Int, val description: String) {
    AI_BACKEND_CALL_ERROR(1, "AI 后端调用出错"),
    LLM_RESP_PARSE_ERROR(2, "LLM 的响应解析出错"),
    SQL_EXEC_ERROR(3, "SQL 执行出错"),
    RESULT_SET_NOT_MATCH(4, "SQL 查询结果无法展示为图表"),
    EMPTY_RESULT_SET(5, "SQL 查询结果为空"),
}


/**
 * 这类异常必须被 controller 捕捉，并返回错误信息，不能溢出到前端
 */
class LogicalException(val errType: Err, message: String): Exception(message) {

    companion object Factory {
        fun create(errType: Err, errorMsg: String? = null): LogicalException =
            LogicalException(errType, "【${errType.description}】" + (errorMsg ?: ""))
    }
}