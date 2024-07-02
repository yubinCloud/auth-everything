package org.inet.aet.chatanalysis.entity


data class DBTable (
    var name: String,
    var remark: String,
    val columns: List<DBColumn>,
    val primaryKeys: List<String>
)

data class DBColumn (
    var name: String,
    var typ: String,
    var nullable: Int,
    var remark: String,
)

data class DBSchema (
    var dbName: String,
    var dbProductName: String,
    var dbProductVersion: String,
    var dbDriverName: String,
    val tables: List<DBTable>,
)