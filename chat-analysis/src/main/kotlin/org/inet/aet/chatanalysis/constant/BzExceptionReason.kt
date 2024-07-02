package org.inet.aet.chatanalysis.constant

class BzExceptionReason {

    companion object {
        /**
         * SQL 执行后的 result set 为空的错误
         */
        const val EMPTY_RESULT_SET = "数据库执行结果为空"

        /**
         * SQL 执行后 Result Set 缺少某个字段的错误
         */
        @JvmStatic fun RESULT_SET_ABSENT_FIELD(fieldName: String): String {
            return "结果集不符合格式，缺少 $fieldName"
        }

        /**
         * SQL 执行后 Result Set 某个字段的类型错误
         */
        @JvmStatic fun RESULT_SET_KLASS_ERROR(fieldName: String, klass: String): String {
            return "结果集不符合格式，$fieldName 字段值不为 $klass 类型"
        }

        @JvmStatic fun CHART_TYPE_NOT_SUPPORT(chartType: String): String {
            return "图表类型 $chartType 目前不支持"
        }
    }


}