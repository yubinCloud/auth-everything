package com.example.afgateway.exchange.response

data class R<T>(
    val code: Int,
    val msg: String,
    val data: T,
)