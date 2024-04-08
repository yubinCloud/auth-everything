package org.inet.aet.uappmaker.service.uapp.types

import io.prometheus.client.Collector
import org.inet.aet.uappmaker.constant.UappType
import org.inet.aet.uappmaker.dto.request.uapptypes.AddUiBasicTabRequest
import org.inet.aet.uappmaker.dto.request.uapptypes.CreateNavInfo
import org.inet.aet.uappmaker.entity.Uapp
import org.inet.aet.uappmaker.entity.uielement.UiBasicNav
import org.inet.aet.uappmaker.entity.uielement.UiBasicNavMetadata
import org.inet.aet.uappmaker.entity.uielement.UiBasicTab
import org.inet.aet.uappmaker.entity.uielement.UiBasicTabMetadata
import org.inet.aet.uappmaker.exception.BaseBuzException
import org.inet.aet.uappmaker.exception.UappOprException
import org.inet.aet.uappmaker.exception.UappTypeMismatchException
import org.inet.aet.uappmaker.repository.mongo.UappBasicTypeRepository
import org.inet.aet.uappmaker.repository.mongo.UappRepository
import org.inet.aet.uappmaker.service.UappService
import org.slf4j.LoggerFactory
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Service
import java.util.*
import java.util.stream.Collectors
import kotlin.collections.ArrayList


/**
 * 在 UAPP 中定位出 nav 的位置信息
 */
data class NavLocateInfo (
    val targetNav: UiBasicNav,
    val navLevel: Int,
    val parentTabId: String,
    val navPath: List<UiBasicNav>,
)

@Service
class BasicUappService (private val uappService: UappService,
                        private val uappRepository: UappRepository,
                        private val uappBasicTypeRepository: UappBasicTypeRepository) {

    companion object {
        private val log = LoggerFactory.getLogger(this::class.java)

        // 删除 nav 时的可能返回结果
        const val DELETE_NAV_RET_OK             = 0
        const val DELETE_NAV_RET_EXIST_CHILD    = 1
        const val DELETE_NAV_RET_NOT_FOUND      = 2
    }

    /**
     * 给 UAPP 添加一个 tab
     */
    fun addTab(addTabReq: AddUiBasicTabRequest): Boolean {
        val tab = UiBasicTab(
            id = UUID.randomUUID().toString().replace("-", ""),
            name = addTabReq.name,
            icon = addTabReq.icon,
            color = addTabReq.color,
            navs = ArrayList()
        )
        val ok = uappBasicTypeRepository.addTab(addTabReq.uappId, tab)
        if (!ok) {
            throw UappOprException("添加 tab 失败")
        }
        return true
    }

    /**
     * 将一个 nav 加入到 tab 中
     */
    fun addNavToTab(navInfo: CreateNavInfo, uapp: Uapp, tabId: String): Boolean {
        val tabs = parseContent(uapp)
        val tab = tabs.stream().filter { tab -> tab.id == tabId }.findFirst()
        if (tab.isEmpty) {
            log.info("Tab $tabId not found in uapp ${uapp.uappId}")
            throw BaseBuzException("无法在 APP ${uapp.uappId} 中找到 tab $tabId")
        }
        val nav = UiBasicNav(
            id = UUID.randomUUID().toString().replace("-", ""),
            name = navInfo.name,
            icon = navInfo.icon,
            color = navInfo.color,
            path = navInfo.path,
            avid = null,
            children = ArrayList()
        )
        val ok = uappBasicTypeRepository.addNavToTab(uapp.uappId!!, tabId, nav)
        if (!ok) {
            throw UappOprException("添加 nav 失败")
        }
        return true
    }

    /**
     * 为 nav 添加 child nav
     */
    fun addNavChild(navInfo: CreateNavInfo, uapp: Uapp, parentId: String): Boolean {
        val tabs = parseContent(uapp)
        val childNav = UiBasicNav(
            id = UUID.randomUUID().toString().replace("-", ""),
            name = navInfo.name,
            icon = navInfo.icon,
            color = navInfo.color,
            path = navInfo.path,
            avid = null,
            children = ArrayList()
        )
        // 从 tabs 中找到 parentId 的 nav，并将 childNav 加到其 children 中
        var targetNav: UiBasicNav? = null
        findNav@ for (i in 0 until tabs.size) {
            val navs = tabs[i].navs
            for (nav in navs) {
                targetNav = walkNav(nav, parentId)
                if (targetNav != null) {
                    break@findNav
                }
            }
        }
        // 如果能够找到 parentId，则保存 uapp
        if (targetNav != null) {
            targetNav.children!!.addLast(childNav)
            uapp.content = tabs
            uappRepository.saveUapp(uapp)
            return true
        }
        return false
    }

    fun moveNavBetweenTabs(uappId: String, userId: String, navId: String, toTabId: String): Boolean {
        // 先检查权限
        val uapp = uappService.checkEditPermission(uappId, userId)
        uappService.checkUappType(uapp.appType, UappType.BASIC)
        // 定位一下 nav 的级别
        val tabs = parseContent(uapp)
        val locateInfo = locateNavLevel(tabs, navId)
        // 执行移动操作
        val deleteOpr = createNavDeleteOpr(locateInfo)
        val addOpr = uappBasicTypeRepository.oprOfAddNavToTab(uappId, toTabId, locateInfo.targetNav)
        val ok = uappBasicTypeRepository.moveNavBetweenTabs(uappId, deleteOpr, addOpr)
        if (!ok) {
            throw UappOprException("移动 nav 失败")
        }
        return true
    }

    fun moveNavToNavChild(uappId: String, userId: String, srcNavId: String, dstNavId: String): Boolean {
        // 先检查权限
        val uapp = uappService.checkEditPermission(uappId, userId)
        uappService.checkUappType(uapp.appType, UappType.BASIC)
        // 定位一下 src nav 的级别，并执行删除操作
        val tabs = parseContent(uapp)
        val srcLocateInfo = locateNavLevel(tabs, srcNavId)
        val deleteOpr = createNavDeleteOpr(srcLocateInfo)
        // 再定位 dst nav 的级别，并执行添加操作
        val dstLocateInfo = locateNavLevel(tabs, dstNavId)
        val addOpr = uappBasicTypeRepository.oprOfAddNavChild(dstLocateInfo.parentTabId, dstNavId, srcLocateInfo.targetNav)
        val ok = uappBasicTypeRepository.moveNavToNavChild(uappId, deleteOpr, addOpr)
        if (!ok) {
            throw UappOprException("移动 nav 失败")
        }
        return true
    }

    fun deleteTab(uappId: String, tabId: String): Boolean {
        val ok = uappBasicTypeRepository.deleteTab(uappId, tabId)
        if (!ok) {
            throw UappOprException("删除 tab 失败")
        }
        return true
    }



    // TODO: 扩展到无限层级
    /**
     * 在 uapp 中删除一个 nav
     * 返回值含义：
     * - 0：删除成功
     * - 1：因仍存在 children 而删除失败（isForce 置为 true 则不会产生该错误）
     * - 2：未找到该 nav
     */
    fun deleteNav(uappId: String, userId: String, navId: String, isForce: Boolean): Int {
        val uapp = uappService.checkEditPermission(uappId, userId)
        uappService.checkUappType(uapp.appType, UappType.BASIC)
        val tabs = parseContent(uapp)
        var retCode = -1
        // 在 tabs 中寻找 nav，并将其在内存的数据结构中删除掉
        findNav@ for (tab in tabs) {
            for (nav in tab.navs) {
                if (nav.id == navId) {
                    if (!isForce && nav.children != null && nav.children!!.isNotEmpty()) {
                        retCode = DELETE_NAV_RET_EXIST_CHILD
                        break@findNav
                    }
                    tab.navs = tab.navs.stream().filter{ nav.id != navId }.collect(Collectors.toList())
                    retCode = DELETE_NAV_RET_OK
                    break@findNav
                } else {
                    val parentNav = findNavParent(nav, navId)
                    if (parentNav != null) {
                        if (!isForce) {
                            val targetNav = parentNav.children!!.stream().filter {it.id == navId}.findFirst()
                            if (targetNav.isPresent && targetNav.get().children != null && targetNav.get().children!!.isNotEmpty()) {
                                retCode = DELETE_NAV_RET_EXIST_CHILD
                                break@findNav
                            }
                        }
                        parentNav.children = parentNav.children!!.stream().filter { nav.id != navId }.collect(Collectors.toList())
                        retCode = DELETE_NAV_RET_OK
                        break@findNav
                    }
                }
            }
        }
        // 根据 retCode 做出相应的 return 处理
        if (retCode < 0) {
            return DELETE_NAV_RET_NOT_FOUND
        } else if (retCode > 0) {
            return retCode
        }
        // 如果成功找到并完成删除，则保存到数据库中
        uapp.content = tabs
        uappRepository.saveUapp(uapp)
        return DELETE_NAV_RET_OK
    }

    fun updateTabMetadata(uappId: String, tabMetadata: UiBasicTabMetadata): Boolean {
        val ok = uappBasicTypeRepository.updateTabMetadata(uappId, tabMetadata)
        if (!ok) {
            throw UappOprException("更新 tab 失败")
        }
        return true
    }

    // TODO：扩展到无限层级
    fun updateNavMetadata(uapp: Uapp, navMetadata: UiBasicNavMetadata): Boolean {
        val navLocateInfo = locateNavLevel(parseContent(uapp), navMetadata.id)
        val ok = uappBasicTypeRepository.updateNavMetadata(uapp, navLocateInfo, navMetadata)
        if (!ok) {
            throw UappOprException("更新 nav 失败")
        }
        return true
    }

    fun getTabList(uapp: Uapp): List<UiBasicTab> {
        return parseContent(uapp)
    }

    fun getTabDetail(uapp: Uapp, tabId: String): UiBasicTab? {
        val tabs = parseContent(uapp)
        val tab = tabs.stream().filter { tab -> tab.id == tabId }.findFirst()
        return tab.orElse(null)
    }

    /**
     * 解析 Basic 类型的 UAPP 的 content 字段，将其转化为 tabs 类型
     */
    private fun parseContent(uapp: Uapp): ArrayList<UiBasicTab> {
        val optionalTabs = uapp.content as? List<*>
        if (optionalTabs == null) {
            log.error("Uapp ${uapp.uappId} content is not a list of UiBasicTab")
            throw UappTypeMismatchException("Uapp ${uapp.uappId} 内容无法正常解析")
        }
        val tabs = ArrayList<UiBasicTab>()
        optionalTabs.stream().forEach { tab ->
            if (tab is UiBasicTab) {
                tabs.add(tab)
            }
        }
        return tabs
    }

    /**
     * 定位出 nav 在第几级上
     */
    private fun locateNavLevel(uappTabs: List<UiBasicTab>, navId: String): NavLocateInfo {
        for (tab in uappTabs) {
            for (nav1 in tab.navs) {
                if (nav1.id == navId) {
                    return NavLocateInfo(nav1, 1, tab.id, listOf())
                }
                if (nav1.children != null) {
                    for (nav2 in nav1.children!!) {
                        if (nav2.id == navId) {
                            return NavLocateInfo(nav2, 2, tab.id, listOf(nav1))
                        }
                    }
                }
            }
        }
        throw UappOprException("未能找到 nav: $navId")
    }

    /**
     * 根据 nav 的定位信息创建出删除操作
     */
    private fun createNavDeleteOpr(locateInfo: NavLocateInfo): Update {
        return when (locateInfo.navLevel) {
            1 -> uappBasicTypeRepository.oprOfDeleteL1Nav(locateInfo.targetNav.id, locateInfo.parentTabId)
            2 -> uappBasicTypeRepository.oprOfDeleteL2Nav(locateInfo.targetNav.id, locateInfo.parentTabId, locateInfo.navPath[0].id)
            else -> {
                log.error("Unexpected nav-level at ${locateInfo.navLevel}")
                throw BaseBuzException("Unexpected error.")
            }
        }
    }

    /**
     * 在 nav（本身或 children）中找到 targetId 对应的 nav
     */
    private fun walkNav(nav: UiBasicNav, targetId: String): UiBasicNav? {
        if (nav.children == null) {
            nav.children = ArrayList()
        }
        // 判断是否命中
        if (nav.id == targetId) {
            return nav
        }
        // 递归遍历其 children
        for (child in nav.children!!) {
            val found = walkNav(child, targetId)
            if (found != null) {
                return found
            }
        }
        return null
    }

    /**
     * 找到一个 nav 的 parent nav
     */
    private fun findNavParent(nav: UiBasicNav, targetId: String): UiBasicNav? {
        if (nav.children == null) {
            nav.children = ArrayList()
        }
        val isFound = nav.children!!.stream().anyMatch { child -> child.id == targetId }
        if (isFound) {
            return nav
        }
        for (childNav in nav.children!!) {
            val result = findNavParent(childNav, targetId)
            if (result != null) {
                return result
            }
        }
        return null
    }
}