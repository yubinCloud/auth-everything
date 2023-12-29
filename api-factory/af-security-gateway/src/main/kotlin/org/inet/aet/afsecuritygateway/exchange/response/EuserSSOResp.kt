package org.inet.aet.afsecuritygateway.exchange.response

data class EuserSSOResp<T>(
    var code: Int,
    var msg: String,
    var data: T,
)