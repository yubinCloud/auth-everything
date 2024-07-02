package org.inet.aet.chatanalysis.exchange.response

data class DsWorkerRespJSON<T> (
    var code: Int,
    var msg: String?,
    var data: T?,
)