package org.inet.aet.uappmaker.dto.request

data class CreateUappGroupRequest (
    val name: String,
    val icon: String,
    val description: String?,
    var tenantId: Int?,
)

data class MoveUappRequest (
    val uappId: String,
)

data class UpdateUappGroupRequest (
    val name: String?,
    val icon: String?,
    val description: String?,
)