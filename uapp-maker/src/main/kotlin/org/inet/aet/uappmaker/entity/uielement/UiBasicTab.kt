package org.inet.aet.uappmaker.entity.uielement

data class UiBasicTab(
    var id: String,
    var name: String,
    var icon: String?,
    var color: String?,
    var navs: MutableList<UiBasicNav>,
): UiElement("basic-tab")


data class UiBasicTabMetadata (
    var id: String,
    var name: String?,
    var icon: String?,
    var color: String?,
)