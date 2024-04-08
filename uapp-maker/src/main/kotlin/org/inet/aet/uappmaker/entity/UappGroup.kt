package org.inet.aet.uappmaker.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document


const val DEFAULT_UAPP_GROUP = "DEFAULT-GROUP"

@Document(collection = "uapp_group")
data class UappGroup(
    @Id
    var id: String?,

    var name: String,
    var icon: String,
    var description: String?,
)
