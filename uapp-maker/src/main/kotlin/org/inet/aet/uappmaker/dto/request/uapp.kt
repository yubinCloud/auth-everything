package org.inet.aet.uappmaker.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

@Schema(description = "创建 Uapp 的请求")
data class CreateUappRequest (

    @Min(0)
    var appType: Int,

    var groupId: String?,

    @NotBlank
    var name: String,

    var icon: String?,

    var banner: String?,

    var topic: String?,

    @Schema(description = "app 的描述，不允许为 null")
    var description: String?,

    @Schema(description = "是否内部共享，不共享的话，就只能自己访问")
    var iShare: Boolean,

    @Schema(description = "是否与共享者共同编辑，不可编辑的话，只有自己可以编辑")
    var coEdit: Boolean,

    @Schema(description = "前端不需要传递这个参数")
    var owner: String?,
)

@Schema(description = "更新 Uapp 的元数据")
data class UpdateUappMetadataRequest (
    var groupId: String?,
    var name: String?,
    var icon: String?,
    var banner: String?,
    var topic: String?,
    var description: String?,

    var iShare: Boolean?,
    var coEdit: Boolean?,
    var eShare: Boolean?,
)