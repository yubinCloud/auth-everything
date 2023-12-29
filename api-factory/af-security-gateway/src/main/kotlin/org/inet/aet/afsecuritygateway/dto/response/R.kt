package org.inet.aet.afsecuritygateway.dto.response

import lombok.*
import lombok.experimental.Accessors
import java.io.Serial
import java.io.Serializable

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
class R<T>(
    var code: Int,
    var msg: String?,
    var data: T?,
) : Serializable {

    private fun transValue(value: Any): String {
        return if (value is String) "\"" + value + "\"" else value.toString()
    }

    companion object {
        @Serial
        private val serialVersionUID = 1L

        const val CODE_SUCCESS: Int = 0
        const val CODE_ERROR: Int = -500
        const val BAD_REQUEST: Int = -400
        const val FORBIDDEN: Int = -403
        const val SERVICE_UNAVAILABLE: Int = -503
    }
}

fun <T> R_SUCCESS(data: T): R<T> {
    return R(R.CODE_SUCCESS, "success", data)
}


fun R_FORBIIDEN(msg: String): R<Any> {
    return R(R.FORBIDDEN, "fail", null)
}