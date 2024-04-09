package org.inet.aet.uappmaker.entity.uielement


enum class UiBasicNavType(val no: Int) {
    AUE_NAV(1),
    EXTERNAL_LINK_NAV(2),
}

data class UiBasicNav(
    var id: String,
    var navType: Int?,   // 区分 Basic Nav 的不同类型
    var name: String,
    var icon: String?,
    var color: String?,
    var path: String,
    var avid: String?,  // `Avue Visual ID` 或者外链地址
    var children: MutableList<UiBasicNav>?
): UiElement("basic-nav")


data class UiBasicNavMetadata (
    var id: String,
    var navType: Int?,
    var name: String?,
    var icon: String?,
    var color: String?,
    var path: String?,
    var avid: String?,  // Avue Visual ID 或者外链地址
)
