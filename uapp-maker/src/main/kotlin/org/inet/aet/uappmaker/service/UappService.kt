package org.inet.aet.uappmaker.service

import org.inet.aet.uappmaker.dto.request.CreateUappRequest
import org.inet.aet.uappmaker.dto.request.UpdateUappMetadataRequest
import org.inet.aet.uappmaker.dto.response.PageInfo
import org.inet.aet.uappmaker.entity.Uapp
import org.inet.aet.uappmaker.entity.UappMetadata
import org.inet.aet.uappmaker.exception.UappPermissionException
import org.inet.aet.uappmaker.exception.UappTypeMismatchException
import org.inet.aet.uappmaker.repository.mongo.UappRepository
import org.inet.aet.uappmaker.service.uapp.UappFactory
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class UappService (val uappRepository: UappRepository) {

    companion object {
        private val log = LoggerFactory.getLogger(this::class.java)
    }

    fun createUapp(body: CreateUappRequest): Uapp {
        if (body.tenantId == null){
            body.tenantId = 1
        }
        val uapp = UappFactory.createUapp(body)
        val doc = uappRepository.saveUapp(uapp)
        log.info("Create uapp success, id: ${doc.uappId}, type: ${doc.appType}")
        return doc
    }

    /**
     * 校验 userId 的用户是否有权限编辑 uappId 的 UAPP
     * 如果没有权限，则直接抛出异常
     */
    fun checkEditPermission(uappId: String, user: String, onlyMeta: Boolean = false): Uapp {
        val uapp = when (onlyMeta) {
            false -> uappRepository.findById(uappId)
            true -> uappRepository.findMetadataById(uappId)
        }
        checkEditPermission(uapp, user)
        return uapp!!
    }

    private fun checkEditPermission(uapp: Uapp?, user: String) {
        if (uapp == null || (uapp.owner != user && !uapp.coEdit)) {
            throw UappPermissionException("Uapp 不存在或者没有编辑权限")
        }
    }

    /**
     * 校验 userId 的内部用户是否有权限查看 uappId 的 UAPP
     */
    fun checkInternalViewPermission(uappId: String, userId: String, onlyMeta: Boolean = false): Uapp {
        val uapp = when (onlyMeta) {
            false -> uappRepository.findById(uappId)
            true -> uappRepository.findMetadataById(uappId)
        }
        checkInternalViewPermission(uapp, userId)
        return uapp!!
    }

    private fun checkInternalViewPermission(uapp: Uapp?, userId: String) {
        if (uapp == null || (uapp.owner != userId && !uapp.iShare)) {
            throw UappPermissionException("Uapp 不存在或者没有查看权限")
        }
    }

    fun checkUappType(actual: Int, expected: Int) {
        if (actual != expected) {
            throw UappTypeMismatchException("Uapp type not match, actual: $actual, expected: $expected")
        }
    }

    fun convertMetadata(uapp: Uapp): UappMetadata {
        val metadata = UappMetadata(
            uappId = uapp.uappId!!,
            appType = uapp.appType,
            groupId = uapp.groupId,
            owner = uapp.owner,
            name = uapp.name,
            createTime = uapp.createTime,
            icon = uapp.icon,
            banner = uapp.banner,
            topic = uapp.topic,
            description = uapp.description,
            updateTime = uapp.updateTime,
            iShare = uapp.iShare,
            coEdit = uapp.coEdit,
            eShare = uapp.eShare,
            tenantId = uapp.tenantId,
            usableAvues = uapp.usableAvues,
        )
        return metadata
    }

    fun deleteUapp(uappId: String) {
        uappRepository.deleteById(uappId)
        log.info("Delete uapp success, id: $uappId")
    }

    fun updateUappMetadata(uappId: String, updateReq: UpdateUappMetadataRequest): Boolean {
        return uappRepository.updateUappMetadata(uappId, updateReq)
    }

    fun listUapp(groupId: String, user: String, tenantId: Int): PageInfo<UappMetadata> {
        val query = uappRepository.oprOfQueryByGroupId(groupId, user, tenantId)
        val uappCnt = uappRepository.executeCount(query)
        val uappList = uappRepository.executeQuery(query)
        val metadataList = uappList.map { convertMetadata(it) }
        return PageInfo(
            total = uappCnt,
            list = metadataList
        )
    }
}