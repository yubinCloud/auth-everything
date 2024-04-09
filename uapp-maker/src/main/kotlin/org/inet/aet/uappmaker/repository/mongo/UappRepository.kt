package org.inet.aet.uappmaker.repository.mongo

import com.mongodb.bulk.BulkWriteResult
import org.inet.aet.uappmaker.constant.UappType
import org.inet.aet.uappmaker.dto.request.UpdateUappMetadataRequest
import org.inet.aet.uappmaker.entity.Uapp
import org.inet.aet.uappmaker.entity.UappMetadata
import org.inet.aet.uappmaker.entity.uielement.UiBasicNav
import org.inet.aet.uappmaker.entity.uielement.UiBasicNavMetadata
import org.inet.aet.uappmaker.entity.uielement.UiBasicTab
import org.inet.aet.uappmaker.entity.uielement.UiBasicTabMetadata
import org.inet.aet.uappmaker.service.uapp.types.NavLocateInfo
import org.springframework.data.mongodb.core.BulkOperations
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.count
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Query.query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface UappCrudRepository: MongoRepository<Uapp, String>

@Repository
class UappRepository(private val uappCrudRepository: UappCrudRepository, private val mongoTemplate: MongoTemplate) {

    fun executeQuery(query: Query): List<Uapp> {
        return mongoTemplate.find(query, Uapp::class.java)
    }

    fun executeCount(query: Query): Long {
        return mongoTemplate.count(query, Uapp::class.java)
    }

    fun saveUapp(uapp: Uapp): Uapp {
        return uappCrudRepository.save(uapp)
    }

    fun findById(uappId: String): Uapp? {
        return uappCrudRepository.findById(uappId).orElse(null)
    }

    fun findMetadataById(uappId: String): Uapp? {
        val q = oprOfQueryById(uappId)
        q.fields().exclude("content")
        return mongoTemplate.findOne(q, Uapp::class.java)
    }

    fun deleteById(uappId: String) {
        uappCrudRepository.deleteById(uappId)
    }

    fun oprOfQueryById(uappId: String): Query {
        return Query(Criteria.where("_id").`is`(uappId))
    }

    fun multiUpdates(uappId: String, updates: List<Update>): BulkWriteResult {
        val q = oprOfQueryById(uappId)
        val bulkOps = mongoTemplate.bulkOps(BulkOperations.BulkMode.ORDERED, Uapp::class.java)
        updates.forEach { bulkOps.updateOne(q, it) }
        return bulkOps.execute()
    }

    fun updateUappMetadata(uappId: String, updateReq: UpdateUappMetadataRequest): Boolean {
        val q = oprOfQueryById(uappId)
        val u = Update()
        if (updateReq.groupId != null) {
            u.set("groupId", updateReq.groupId)
        }
        if (updateReq.name != null) {
            u.set("name", updateReq.name)
        }
        if (updateReq.icon != null) {
            u.set("icon", updateReq.icon)
        }
        if (updateReq.banner != null) {
            u.set("banner", updateReq.banner)
        }
        if (updateReq.topic != null) {
            u.set("topic", updateReq.topic)
        }
        if (updateReq.description != null) {
            u.set("description", updateReq.description)
        }
        if (updateReq.iShare != null) {
            u.set("iShare", updateReq.iShare)
        }
        if (updateReq.coEdit != null) {
            u.set("coEdit", updateReq.coEdit)
        }
        if (updateReq.eShare != null) {
            u.set("eShare", updateReq.eShare)
        }
        return mongoTemplate.updateFirst(q, u, Uapp::class.java).modifiedCount > 0
    }

    /**
     * 计算某个 group 下 uapp 的数量
     */
    fun countByGroupId(groupId: String): Long {
        val q = Query(Criteria.where("groupId").`is`(groupId))
        return mongoTemplate.count(q, Uapp::class.java)
    }

    /**
     * 根据 groupId 和 userId 创建出查询操作
     */
    fun oprOfQueryByGroupId(groupId: String, userId: String): Query {
        // 过滤出 groupId 为指定值的记录，且 owner 为 userId 或 eShare 为 true
        val criteria = Criteria().andOperator(
            Criteria.where("groupId").`is`(groupId),
            Criteria().orOperator(
                Criteria.where("owner").`is`(userId),
                Criteria.where("eShare").`is`(true)
            )
        )
        val q = Query(criteria)
        q.fields().exclude("content")  // 查询列表时，只需要获取其中的元数据，因此可以省略掉 content 字段
        return q
    }
}

@Repository
class UappBasicTypeRepository(private val uappRepository: UappRepository, private val mongoTemplate: MongoTemplate) {

    fun addTab(uappId: String, tab: UiBasicTab): Boolean {
        return mongoTemplate.updateFirst(
            query(Criteria.where("_id").`is`(uappId).and("appType").`is`(UappType.BASIC)),
            Update().push("content", tab),
            Uapp::class.java
        ).modifiedCount > 0
    }

    fun addNavToTab(uappId: String, tabId: String, nav: UiBasicNav): Boolean {
        val q = uappRepository.oprOfQueryById(uappId)
        val update = oprOfAddNavToTab(uappId, tabId, nav)
        return mongoTemplate.updateFirst(q, update, Uapp::class.java).modifiedCount > 0
    }

    fun moveNavBetweenTabs(uappId: String, deleteOpr: Update, addOpr: Update): Boolean {
        return uappRepository.multiUpdates(uappId, listOf(deleteOpr, addOpr)).modifiedCount > 0
    }

    fun moveNavToNavChild(uappId: String, deleteOpr: Update, addOpr: Update): Boolean {
        return uappRepository.multiUpdates(uappId, listOf(deleteOpr, addOpr)).modifiedCount > 0
    }

    fun deleteTab(uappId: String, tabId: String): Boolean {
        val q = uappRepository.oprOfQueryById(uappId)
        val update = Update().pull("content", Query(Criteria.where("_id").`is`(tabId)))
        return mongoTemplate.updateFirst(q, update, Uapp::class.java).modifiedCount > 0
    }

    fun deleteNav(uappId: String, deleteOpr: Update): Boolean {
        val q = uappRepository.oprOfQueryById(uappId)
        return mongoTemplate.updateFirst(q, deleteOpr, Uapp::class.java).modifiedCount > 0
    }

    fun updateTabMetadata(uappId: String, metadata: UiBasicTabMetadata): Boolean {
        val q = uappRepository.oprOfQueryById(uappId)
        val update = Update()
        if (metadata.icon != null) {
            update.set("content.$[tab].icon", metadata.icon)
        }
        if (metadata.name != null) {
            update.set("content.$[tab].name", metadata.name)
        }
        if (metadata.color != null) {
            update.set("content.$[tab].color", metadata.color)
        }
        if (metadata.body != null) {
            update.set("content.$[tab].body", metadata.body)
        }
        update.filterArray(Criteria.where("tab._id").`is`(metadata.id))
        return mongoTemplate.updateFirst(q, update, Uapp::class.java).modifiedCount > 0
    }

    fun updateNavMetadata(uapp: Uapp, locateInfo: NavLocateInfo, metadata: UiBasicNavMetadata): Boolean {
        val q = uappRepository.oprOfQueryById(uapp.uappId!!)
        val update = Update()
        var attrPrefix = "content.$[tab].navs.$[nav]."
        if (locateInfo.navLevel == 2) {
            attrPrefix = "content.$[tab].navs.$[parentNav].children.$[nav]."
        }
        if (metadata.name != null) {
            update.set(attrPrefix + "name", metadata.name)
        }
        if (metadata.icon != null) {
            update.set(attrPrefix + "icon", metadata.icon)
        }
        if (metadata.color != null) {
            update.set(attrPrefix + "color", metadata.color)
        }
        if (metadata.path != null) {
            update.set(attrPrefix + "path", metadata.path)
        }
        if (metadata.avid != null) {
            update.set(attrPrefix + "avid", metadata.avid)
        }
        update.filterArray(Criteria.where("tab._id").`is`(locateInfo.parentTabId))
        if (locateInfo.navLevel == 2) {
            update.filterArray(Criteria.where("parentNav._id").`is`(locateInfo.navPath[0].id))
        }
        update.filterArray(Criteria.where("nav._id").`is`(metadata.id))
        return mongoTemplate.updateFirst(q, update, Uapp::class.java).modifiedCount > 0
    }

    /**
     * 在一个 tab 中添加 nav 的 opr
     */
    fun oprOfAddNavToTab(uappId: String, tabId: String, nav: UiBasicNav): Update {
        return Update().push("content.$[tabItem].navs", nav).filterArray(Criteria.where("tabItem._id").`is`(tabId))
    }

    /**
     * 删除一级 nav 的 opr
     */
    fun oprOfDeleteL1Nav(navId: String, tabId: String): Update {
        return Update().pull("content.$[tabItem].navs", Query(Criteria.where("_id").`is`(navId)))
            .filterArray(Criteria.where("tabItem._id").`is`(tabId))
    }

    /**
     * 删除二级 nav 的 opr
     */
    fun oprOfDeleteL2Nav(navId: String, tabId: String, parentId: String): Update {
        return Update().pull("content.$[tabItem].navs.$[navElem].children", Query(Criteria.where("_id").`is`(navId)))
            .filterArray(Criteria.where("tabItem._id").`is`(tabId))
            .filterArray(Criteria.where("navElem._id").`is`(parentId))
    }

    /**
     * 在一级 nav 中添加子 nav 的 opr
     */
    fun oprOfAddNavChild(tabId: String, parentId: String, child: UiBasicNav): Update {
        return Update().push("content.$[tabItem].navs.$[navElem].children", child)
            .filterArray(Criteria.where("tabItem._id").`is`(tabId))
            .filterArray(Criteria.where("navElem._id").`is`(parentId))
    }
}