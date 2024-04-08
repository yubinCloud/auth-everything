package org.inet.aet.uappmaker.entity.uielement

data class UiBasicNav(
    var id: String,
    var name: String,
    var icon: String?,
    var color: String?,
    var path: String,
    var avid: String?,  // Avue Visual ID
    var children: MutableList<UiBasicNav>?
): UiElement("basic-nav")


data class UiBasicNavMetadata (
    var id: String,
    var name: String?,
    var icon: String?,
    var color: String?,
    var path: String?,
    var avid: String?,  // Avue Visual ID
)
