package org.inet.aet.uappmaker.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import jakarta.validation.constraints.Min
import org.inet.aet.uappmaker.constant.XReqHeader
import org.inet.aet.uappmaker.dto.request.CreateUappRequest
import org.inet.aet.uappmaker.dto.request.UpdateUappMetadataRequest
import org.inet.aet.uappmaker.dto.response.PageInfo
import org.inet.aet.uappmaker.dto.response.R
import org.inet.aet.uappmaker.dto.response.R_ERROR
import org.inet.aet.uappmaker.dto.response.R_SUCCESS
import org.inet.aet.uappmaker.entity.Uapp
import org.inet.aet.uappmaker.entity.UappMetadata
import org.inet.aet.uappmaker.service.UappService
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/uapp")
@Validated
@Tag(name = "Uapp 操作")
class UappController (private val uappService: UappService) {

    @PostMapping("/create")
    @Operation(summary = "创建一个 Uapp")
    fun createUapp(@RequestBody @Valid body: CreateUappRequest, @RequestHeader(XReqHeader.IUSER_WHOAMI) userId: String): R<Uapp> {
        body.owner = userId
        val uapp = uappService.createUapp(body)
        return R_SUCCESS(uapp)
    }

    @GetMapping("/metadata/{uappId}")
    @Operation(summary = "查看 UAPP 的元信息", description = "注意权限要求，且返回数据不包含 content")
    fun getUappMetadata(@PathVariable uappId: String, @RequestHeader(XReqHeader.IUSER_WHOAMI) userId: String): R<UappMetadata> {
        val uapp = uappService.checkInternalViewPermission(uappId, userId)
        val metadata = uappService.convertMetadata(uapp)
        return R_SUCCESS(metadata)
    }

    @DeleteMapping("/delete/{uappId}")
    @Operation(summary = "删除一个 Uapp")
    fun deleteUapp(@PathVariable uappId: String, @RequestHeader(XReqHeader.IUSER_WHOAMI) userId: String): R<String> {
        uappService.deleteUapp(uappId)
        return R_SUCCESS("删除成功")
    }

    @PostMapping("/update-metadata/{uappId}")
    @Operation(summary = "更新 Uapp 的元数据")
    fun updateUappMetadata(@RequestBody @Valid body: UpdateUappMetadataRequest, @PathVariable uappId: String, @RequestHeader(XReqHeader.IUSER_WHOAMI) userId: String): R<String> {
        uappService.checkEditPermission(uappId, userId)
        val ok = uappService.updateUappMetadata(uappId, body)
        return when (ok) {
            false -> R_ERROR("更新失败，原因：未找到 uapp 或者没有元数据被更新")
            else -> R_SUCCESS("更新成功")
        }
    }

    @GetMapping("/list/{groupId}")
    @Operation(summary = "列出某一个 group 的 Uapp")
    fun listUapp(@PathVariable groupId: String,
                  @Min(1) @Parameter(required = false, example = "1") @RequestParam(required = false, defaultValue = "1") pageNum: Int,
                  @Min(1) @Parameter(required = false, example = "10") @RequestParam(required = false, defaultValue = "10") pageSize: Int,
                  @RequestHeader(XReqHeader.IUSER_WHOAMI) userId: String): R<PageInfo<UappMetadata>> {
        val page = uappService.listUapp(groupId, userId)
        return R_SUCCESS(page)
    }
}