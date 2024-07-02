package org.inet.aet.chatanalysis.util

import org.inet.aet.chatanalysis.entity.DBSchema


/**
 * 用于将 DB schema 序列化为 string
 */
interface DBSchemaSerializer {

    fun serializeDBSchema(schema: DBSchema): String

}


class CommonDBSchemaSerializer: DBSchemaSerializer {

    private fun nullableText(nullable: Int): String {
        return when (nullable) {
            0 -> "Not Nullable"
            1 -> "Nullable"
            else -> "Unknown Nullable"
        }
    }

    override fun serializeDBSchema(schema: DBSchema): String {
        val sb = StringBuilder()
        sb.append("Database：${schema.dbProductName}:${schema.dbProductVersion}  Database name：${schema.dbName}\n")
        sb.append("Tables:\n")
        for (table in schema.tables) {
            sb.append("- table name: ${table.name}, table remark: ${table.remark}\n")
            sb.append("- columns:\n")
            for (col in table.columns) {
                sb.append("  - ${col.name}: ${col.typ}, ${nullableText(col.nullable)}, ${col.remark}\n")
            }
        }
        return sb.toString()
    }

}