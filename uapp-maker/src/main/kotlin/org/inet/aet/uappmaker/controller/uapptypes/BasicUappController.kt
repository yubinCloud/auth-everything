package org.inet.aet.uappmaker.controller.uapptypes

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import lombok.extern.slf4j.Slf4j
import org.inet.aet.uappmaker.constant.UappType
import org.inet.aet.uappmaker.constant.XReqHeader
import org.inet.aet.uappmaker.dto.request.uapptypes.*
import org.inet.aet.uappmaker.dto.response.R
import org.inet.aet.uappmaker.dto.response.R_CODE
import org.inet.aet.uappmaker.dto.response.R_ERROR
import org.inet.aet.uappmaker.dto.response.R_SUCCESS
import org.inet.aet.uappmaker.dto.response.uapptypes.BasicUiTabBrief
import org.inet.aet.uappmaker.dto.response.uapptypes.GetBasicUiTabListResponse
import org.inet.aet.uappmaker.entity.uielement.UiBasicTab
import org.inet.aet.uappmaker.service.UappService
import org.inet.aet.uappmaker.service.uapp.types.BasicUappService
import org.slf4j.LoggerFactory
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/uapp/types/basic")
@Validated
@Slf4j
@Tag(name = "Uapp 操作（basic 类型）")
class BasicUappController(private val uappService: UappService, private val uappBasicService: BasicUappService) {

    companion object {
        private val log = LoggerFactory.getLogger(this::class.java)
    }


    @PostMapping("/add-tab")
    @Operation(summary = "添加一个 tab")
    fun addTab(@RequestBody @Valid body: AddUiBasicTabRequest, @RequestHeader(XReqHeader.IUSER_WHOAMI) userId: String): R<String> {
        uappBasicService.addTab(body)
        return R_SUCCESS("添加成功")
    }

    @PostMapping("/add-nav-to-tab")
    @Operation(summary = "在 tab 中添加一个 nav")
    fun addNavToTab(@RequestBody @Valid body: AddUiBasicNavToTabRequest, @RequestHeader(XReqHeader.IUSER_WHOAMI) userId: String): R<String> {
        val uapp = uappService.checkEditPermission(body.uappId, userId)
        uappService.checkUappType(uapp.appType, UappType.BASIC)
        uappBasicService.addNavToTab(body.navInfo, uapp, body.tabId)
        return R_SUCCESS("添加成功")
    }

    @PostMapping("/add-nav-child")
    @Operation(summary = "为一个 nav 添加子 nav", description = "支持无限层级的 nav")
    fun addNavChildren(@RequestBody @Valid body: AddUiBasicNavChildRequest, @RequestHeader(XReqHeader.IUSER_WHOAMI) userId: String): R<String> {
        val uapp = uappService.checkEditPermission(body.uappId, userId)
        uappService.checkUappType(uapp.appType, UappType.BASIC)
        uappBasicService.addNavChild(body.navInfo, uapp, body.parentId)
        return R_SUCCESS("添加成功")
    }

    @PostMapping("/move-nav-between-tabs")
    @Operation(summary = "将一个 nav 移动到另一个 tab", description = "所要移动的 nav 最多支持二级 nav")
    fun moveNavBetweenTabs(@RequestBody @Valid body: MoveNavToTabRequest, @RequestHeader(XReqHeader.IUSER_WHOAMI) userId: String): R<String> {
        uappBasicService.moveNavBetweenTabs(body.uappId, userId, body.navId, body.tabId)
        return R_SUCCESS("移动成功")
    }

    @PostMapping("/move-nav-to-navchild")
    @Operation(summary = "将一个 nav 移动到另一个 nav 的子 nav 中", description = "所要移动的 nav 最多支持移动二级 nav")
    fun moveNavToNavChild(@RequestBody @Valid body: MoveNavToNavChildRequest, @RequestHeader(XReqHeader.IUSER_WHOAMI) userId: String): R<String> {
        uappBasicService.moveNavToNavChild(body.uappId, userId, body.srcNavId, body.dstNavId)
        return R_SUCCESS("移动成功")
    }

    @DeleteMapping("/delete-tab")
    @Operation(summary = "删除一个 tab")
    fun deleteTab(@RequestBody @Valid body: DeleteTabRequest, @RequestHeader(XReqHeader.IUSER_WHOAMI) userId: String): R<String> {
        val uapp = uappService.checkEditPermission(body.uappId, userId)
        uappService.checkUappType(uapp.appType, UappType.BASIC)
        uappBasicService.deleteTab(body.uappId, body.tabId)
        return R_SUCCESS("删除成功")
    }

    @DeleteMapping("/delete-nav")
    @Operation(summary = "删除一个 nav")
    fun deleteNav(@RequestBody @Valid body: DeleteNavRequest, @RequestHeader(XReqHeader.IUSER_WHOAMI) userId: String): R<String> {
        if (body.force == null) {
            body.force = false
        }
        val retCode = uappBasicService.deleteNav(body.uappId, userId, body.navId, body.force!!)
        return when (retCode) {
            BasicUappService.DELETE_NAV_RET_OK          -> R_SUCCESS("删除成功")
            BasicUappService.DELETE_NAV_RET_NOT_FOUND   -> R_CODE(1, "未找到该导航栏", "删除失败")
            BasicUappService.DELETE_NAV_RET_EXIST_CHILD -> R_CODE(2, "该导航栏仍存在子导航栏", "删除失败")
            else                                        -> R_ERROR("发生未知错误")
        }
    }

    @PostMapping("/update-tab-metadata")
    @Operation(summary = "更新 tab 的信息")
    fun updateTabMetadata(@RequestBody @Valid body: UpdateTabMetadataRequest, @RequestHeader(XReqHeader.IUSER_WHOAMI) userId: String): R<String> {
        val uapp = uappService.checkEditPermission(body.uappId, userId)
        uappService.checkUappType(uapp.appType, UappType.BASIC)
        uappBasicService.updateTabMetadata(body.uappId, body.tabMetadata)
        return R_SUCCESS("更新失败")
    }

    @PostMapping("/update-nav-metadata")
    @Operation(summary = "更新 nav 的信息")
    fun updateNavMetadata(@RequestBody @Valid body: UpdateNavMetadataRequest, @RequestHeader(XReqHeader.IUSER_WHOAMI) userId: String): R<String> {
        val uapp = uappService.checkEditPermission(body.uappId, userId)
        uappService.checkUappType(uapp.appType, UappType.BASIC)
        uappBasicService.updateNavMetadata(uapp, body.navMetadata)
        return R_SUCCESS("更新成功")
    }

    @GetMapping("/tab-list/{uappId}")
    @Operation(summary = "获取 tab 列表")
    fun getTabList(@PathVariable uappId: String, @RequestHeader(XReqHeader.IUSER_WHOAMI) userId: String): R<GetBasicUiTabListResponse> {
        val uapp = uappService.checkInternalViewPermission(uappId, userId)
        uappService.checkUappType(uapp.appType, UappType.BASIC)
        val tabs = uappBasicService.getTabList(uapp)
        val resp = GetBasicUiTabListResponse(
            tabs = tabs.map { BasicUiTabBrief(it.id, it.name) },
            firstTab = if (tabs.isEmpty()) null else tabs[0]
        )
        return R_SUCCESS(resp)
    }

    @GetMapping("/tab-detail/{uappId}/{tabId}")
    @Operation(summary = "获取某个 tab 的详细信息")
    fun getTabDetail(@PathVariable uappId: String, @PathVariable tabId: String, @RequestHeader(XReqHeader.IUSER_WHOAMI) userId: String): R<UiBasicTab?> {
        val uapp = uappService.checkInternalViewPermission(uappId, userId)
        uappService.checkUappType(uapp.appType, UappType.BASIC)
        val tab = uappBasicService.getTabDetail(uapp, tabId)
        return R_SUCCESS(tab)
    }
}