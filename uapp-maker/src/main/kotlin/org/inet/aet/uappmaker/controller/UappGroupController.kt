package org.inet.aet.uappmaker.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.inet.aet.uappmaker.dto.request.CreateUappGroupRequest
import org.inet.aet.uappmaker.dto.request.UpdateUappGroupRequest
import org.inet.aet.uappmaker.dto.response.PageInfo
import org.inet.aet.uappmaker.dto.response.R
import org.inet.aet.uappmaker.dto.response.R_ERROR
import org.inet.aet.uappmaker.dto.response.R_SUCCESS
import org.inet.aet.uappmaker.entity.UappGroup
import org.inet.aet.uappmaker.service.UappGroupService
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/uapp-group")
@Validated
@Tag(name = "Uapp Group 操作")
class UappGroupController (private val uappGroupService: UappGroupService) {

    @PostMapping("/create")
    @Operation(summary = "创建一个 Uapp Group")
    fun createUappGroup(@RequestBody @Valid createUappGroupRequest: CreateUappGroupRequest): R<UappGroup> {
        val uappGroup = uappGroupService.createUappGroup(createUappGroupRequest)
        return R_SUCCESS(uappGroup)
    }

    @DeleteMapping("/delete/{uappGroupId}")
    @Operation(summary = "删除 Uapp Group")
    fun deleteUappGroup(@PathVariable uappGroupId: String): R<String> {
        val ok = uappGroupService.deleteUappGroup(uappGroupId)
        return when (ok) {
            true -> R_SUCCESS("删除成功")
            false -> R_ERROR("删除失败，原因：组不存在或者组中仍存在应用")
        }
    }

    @PostMapping("/update/{uappGroupId}")
    @Operation(summary = "更新 Uapp Group 的元信息")
    fun updateUappGroup(@PathVariable uappGroupId: String, @RequestBody @Valid updateUappGroupRequest: UpdateUappGroupRequest): R<String> {
        val ok = uappGroupService.updateUappGroup(uappGroupId, updateUappGroupRequest)
        return when (ok) {
            true -> R_SUCCESS("更新成功")
            false -> R_ERROR("更新失败，原因：组不存在或未更新信息")
        }
    }

    @GetMapping("/all")
    @Operation(summary = "获取全部 Uapp Group")
    fun allUappGroups(): R<List<UappGroup>> {
        val result = uappGroupService.allUappGroups()
        return R_SUCCESS(result)
    }
}