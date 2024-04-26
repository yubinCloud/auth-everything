package org.inet.aet.uappmaker.service.uapp

import org.inet.aet.uappmaker.dto.request.CreateUappRequest
import org.inet.aet.uappmaker.entity.Uapp
import org.inet.aet.uappmaker.entity.uielement.UiBasicTab
import org.inet.aet.uappmaker.constant.UappType
import org.inet.aet.uappmaker.entity.DEFAULT_TENANT_ID
import org.inet.aet.uappmaker.entity.DEFAULT_UAPP_GROUP

object UappFactory {

    fun createUapp(createReq: CreateUappRequest): Uapp {
       return when(createReq.appType) {
            UappType.BASIC -> createBasicUapp(createReq)
            else -> throw IllegalArgumentException("Unknown uapp type: ${createReq.appType}")
        }
    }

    private const val DEFAULT_PERM_ISHARE = false

    private const val DEFAULT_PERM_COEDIT = false

    private const val DEFAULT_PERM_ESHARE = false

    /**
     * 创建 basic 类型的 uapp
     */
    private fun createBasicUapp(createReq: CreateUappRequest): Uapp {
        val t = System.currentTimeMillis()
        return Uapp(
            uappId = null,
            tenantId = if (createReq.tenantId != null)createReq.tenantId!! else DEFAULT_TENANT_ID,
            appType = createReq.appType,
            groupId = if (createReq.groupId != null) createReq.groupId!! else DEFAULT_UAPP_GROUP,
            owner = createReq.owner!!,
            name = createReq.name,
            createTime = t,
            icon = createReq.icon,
            banner = if (createReq.banner != null) createReq.banner!! else "",
            topic = createReq.topic,
            description = if (createReq.description != null) createReq.description!! else "",
            updateTime = t,
            iShare = DEFAULT_PERM_ISHARE,
            coEdit = DEFAULT_PERM_COEDIT,
            eShare = DEFAULT_PERM_ESHARE,
            content = ArrayList<UiBasicTab>()
        )
    }

}