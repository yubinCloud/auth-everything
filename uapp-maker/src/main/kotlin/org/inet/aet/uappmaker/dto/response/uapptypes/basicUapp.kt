package org.inet.aet.uappmaker.dto.response.uapptypes

import org.inet.aet.uappmaker.entity.uielement.UiBasicTab

/**
 * tab 的简要信息
 */
data class BasicUiTabBrief (
    var id: String,
    var name: String
)

data class GetBasicUiTabListResponse (
    var tabs: List<BasicUiTabBrief>,
    var firstTab: UiBasicTab?,
)