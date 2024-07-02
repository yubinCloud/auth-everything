package org.inet.aet.chatanalysis.entity

data class Chat2SQLResult (
    var success: Boolean,
    var errorMsg: String,
    var sql: String,
)