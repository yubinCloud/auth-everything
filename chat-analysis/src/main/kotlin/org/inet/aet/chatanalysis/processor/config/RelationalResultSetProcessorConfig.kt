package org.inet.aet.chatanalysis.processor.config

import org.inet.aet.chatanalysis.constant.ChartTypesEnum
import org.inet.aet.chatanalysis.processor.RelationalDBResultSetProcessor
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.function.Function
import java.util.stream.Collectors


typealias RelationalResultSetProcessorMap = Map<ChartTypesEnum, RelationalDBResultSetProcessor>

@Configuration
class RelationalResultSetProcessorConfig (private val processors: List<RelationalDBResultSetProcessor>) {

    /**
     * 将 relational result set 的 processors 转为 map
     *
     * key 是所用于处理的 chart type，value 是 processor 实例
     */
    @Bean
    fun relationalResultSetProcessorMap(): RelationalResultSetProcessorMap {
        return processors.stream().collect(Collectors.toMap(RelationalDBResultSetProcessor::forChartType, Function.identity()))
    }
}