package org.inet.aet.uappmaker.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "uapp")
data class Uapp(

    @Id
    var uappId: String?,  // uapp id

    var tenantId: Int?,   // app 的机构类型

    var appType: Int,    // app 类型
    var groupId: String, // 所在的 group 的 id
    var owner: String,   // app 所有者
    var name: String,    // app 名称
    var createTime: Long,    // app 创建时间
    var icon: String?,    // app 图标
    var banner: String,   // app banner
    var topic: String?,   // app 主题
    var description: String,    // app 描述
    var updateTime: Long,    // app 更新时间
    var usableAvues: List<String>?, // app 可使用的大屏 id 集合
    // 权限相关信息
    var iShare: Boolean,  // 是否内部可共享
    var coEdit: Boolean,  // 是否可以共同编辑
    var eShare: Boolean,  // 是否对外共享

    var content: Any?,    // app 内容，比如有哪些 tabs 等等
)

/**
 * Uapp 的元数据，不包含具体内容
 */
data class UappMetadata(
    var uappId: String,  // uapp id
    var tenantId: Int?,   // app 的机构类型
    var appType: Int,    // app 类型
    var groupId: String, // 所在的 group 的 id
    var owner: String,   // app 所有者
    var name: String,    // app 名称
    var createTime: Long,    // app 创建时间
    var icon: String?,    // app 图标
    var banner: String?,  // app banner
    var topic: String?,   // app 主题
    var description: String,    // app 描述
    var updateTime: Long,    // app 更新时间
    var usableAvues: List<String>?, // app 可使用的大屏 id 集合
    // 权限相关信息
    var iShare: Boolean,    // 是否内部共享
    var coEdit: Boolean,    // 是否其他人可编辑
    var eShare: Boolean    // 是否可对外访问
)
