package org.inet.aet.uappmaker.service

import org.inet.aet.uappmaker.dto.request.CreateUappGroupRequest
import org.inet.aet.uappmaker.dto.request.UpdateUappGroupRequest
import org.inet.aet.uappmaker.entity.Uapp
import org.inet.aet.uappmaker.entity.UappGroup
import org.inet.aet.uappmaker.exception.UappOprException
import org.inet.aet.uappmaker.repository.mongo.UappGroupRepository
import org.inet.aet.uappmaker.repository.mongo.UappRepository
import org.springframework.stereotype.Service

@Service
class UappGroupService (private val uappGroupRepository: UappGroupRepository, private val uappRepository: UappRepository) {

    fun createUappGroup(createReq: CreateUappGroupRequest): UappGroup {
        val uappGroup = UappGroup(
            id = null,
            name = createReq.name,
            icon = createReq.icon,
            description = createReq.description,
        )
        return uappGroupRepository.saveUappGroup(uappGroup)
    }

    fun deleteUappGroup(groupId: String): Boolean {
        val refCnt = uappRepository.countByGroupId(groupId)
        if (refCnt > 0) {
            throw UappOprException("删除失败，原因：组中仍存在应用，无法删除")
        }
        return uappGroupRepository.deleteUappGroup(groupId)
    }

    fun updateUappGroup(groupId: String, updateReq: UpdateUappGroupRequest): Boolean {
        return uappGroupRepository.updateUappGroup(groupId, updateReq)
    }

    fun allUappGroups(): List<UappGroup> {
        return uappGroupRepository.listUappGroups()
    }

    fun count(): Long {
        return uappGroupRepository.count()
    }

}