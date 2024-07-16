package org.inet.aet.uappmaker.entity.uielement


enum class UiBasicNavType(val no: Int) {
    AUE_NAV(1),
    EXTERNAL_LINK_NAV(2),
    DE_PANEL(3),
}

data class UiBasicNav(
    var id: String,
    var navType: Int?,   // 区分 Basic Nav 的不同类型
    var name: String,
    var icon: String?,
    var color: String?,
    var path: String,       // `Avue Visual ID`  or 外链地址 or `DataEase-panel id`
    var avid: String?,      // 暂时没有用到
    var children: MutableList<UiBasicNav>?
): UiElement("basic-nav")


data class UiBasicNavMetadata (
    var id: String,
    var navType: Int?,
    var name: String?,
    var icon: String?,
    var color: String?,
    var path: String?,  // Avue Visual ID or 外链地址 or DataEase-panel id
    var avid: String?,
)
