package org.inet.aet.uappmaker.dto.request.uapptypes

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import org.inet.aet.uappmaker.entity.uielement.UiBasicNavMetadata
import org.inet.aet.uappmaker.entity.uielement.UiBasicTabMetadata


data class AddUiBasicTabRequest (

    @NotBlank
    var uappId: String,

    var tabType: Int?,

    @NotBlank
    var name: String,

    var icon: String?,

    var color: String?,

    var body: String?,
)

data class CreateNavInfo (

    @Schema(description = "1 代表 avue 大屏 nav（默认），2 代表外链 nav")
    var navType: Int?,

    var name: String,

    var icon: String?,

    var color: String?,

    var path: String,
)


data class AddUiBasicNavToTabRequest (

    @NotBlank
    var uappId: String,

    @NotBlank
    var tabId: String,

    var navInfo: CreateNavInfo,
)

data class AddUiBasicNavChildRequest (

    @NotBlank
    var uappId: String,

    @NotBlank
    var parentId: String,

    var navInfo: CreateNavInfo,
)

data class MoveNavToTabRequest(
    @NotBlank
    var uappId: String,
    @NotBlank
    var tabId: String,
    @NotBlank
    var navId: String,
)

data class MoveNavToNavChildRequest (
    @NotBlank
    var uappId: String,

    @NotBlank
    var srcNavId: String,

    @NotBlank
    var dstNavId: String,
)

data class DeleteTabRequest (
    @NotBlank
    var uappId: String,

    @NotBlank
    var tabId: String,
)

@Schema(description = "删除 nav 请求")
data class DeleteNavRequest (
    @NotBlank
    var uappId: String,

    @NotBlank
    var navId: String,

    @Schema(description = "当该 nav 存在子 nav 时，是否强制删除")
    var force: Boolean?
)

data class UpdateTabMetadataRequest (
    @NotBlank
    var uappId: String,

    var tabMetadata: UiBasicTabMetadata
)

data class UpdateNavMetadataRequest (
    @NotBlank
    var uappId: String,

    var navMetadata: UiBasicNavMetadata
)