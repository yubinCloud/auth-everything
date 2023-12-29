package org.inet.aet.afsecuritygateway.exchange.response

data class QueryPublicAPIPermissionResponse(
    var routes: List<String>?,
) {
    constructor(): this(null)
}