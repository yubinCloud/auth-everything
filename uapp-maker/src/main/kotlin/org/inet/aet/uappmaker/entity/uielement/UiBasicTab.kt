package org.inet.aet.uappmaker.entity.uielement


enum class UiBasicTabType(val no: Int) {
    NAVS(1),            // 默认类型，带有多个 navs
    SINGLE_PAGE(2),     // 只有一个大屏页面的 tab
}


data class UiBasicTab(
    var id: String,
    var tabType: Int?,  // 区分不同 BasicTab 的类型
    var name: String,
    var icon: String?,
    var color: String?,
    var navs: MutableList<UiBasicNav>,
    var body: String?,
): UiElement("basic-tab")


data class UiBasicTabMetadata (
    var id: String,
    var name: String?,
    var icon: String?,
    var color: String?,
    var body: String?,
)