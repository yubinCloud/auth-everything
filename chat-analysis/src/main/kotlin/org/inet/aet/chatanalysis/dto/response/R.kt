package org.inet.aet.chatanalysis.dto.response

import java.io.Serial
import java.io.Serializable

data class R<T>(
    var code: Int,
    var msg: String?,
    var data: T?,
) : Serializable {
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

fun <T> R_ERROR(data: T, msg: String = "fail"): R<T> {

    return R(R.CODE_ERROR, msg, data)
}


fun <T> R_FORBIIDEN(msg: String, data: T): R<T> {
    return R(R.FORBIDDEN, msg, data)
}

fun <T> R_CODE(code: Int, msg: String?, data: T): R<T> {
    return R(code, msg, data)
}