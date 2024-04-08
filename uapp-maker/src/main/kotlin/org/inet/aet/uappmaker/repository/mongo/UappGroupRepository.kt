package org.inet.aet.uappmaker.repository.mongo

import org.inet.aet.uappmaker.dto.request.UpdateUappGroupRequest
import org.inet.aet.uappmaker.entity.UappGroup
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Repository

@Repository
interface UappGroupCrudRepository: MongoRepository<UappGroup, String>

@Repository
class UappGroupRepository (private val uappGroupCrudRepository: UappGroupCrudRepository, private val mongoTemplate: MongoTemplate) {

    fun saveUappGroup(uappGroup: UappGroup): UappGroup {
        return uappGroupCrudRepository.save(uappGroup)
    }

    fun deleteUappGroup(groupId: String): Boolean {
        val q = Query(Criteria.where("_id").`is`(groupId))
        return mongoTemplate.remove(q, UappGroup::class.java).deletedCount > 0
    }

    fun updateUappGroup(groupId: String, updateReq: UpdateUappGroupRequest): Boolean {
        val q = Query(Criteria.where("_id").`is`(groupId))
        val u = Update()
        if (updateReq.name != null) {
            u.set("name", updateReq.name)
        }
        if (updateReq.icon != null) {
            u.set("icon", updateReq.icon)
        }
        if (updateReq.description != null) {
            u.set("description", updateReq.description)
        }
        return mongoTemplate.updateFirst(q, u, UappGroup::class.java).modifiedCount > 0
    }

    fun listUappGroups(): List<UappGroup> {
        return uappGroupCrudRepository.findAll()
    }

    fun count(): Long {
        return uappGroupCrudRepository.count()
    }
}