package org.inet.aet.uappmaker.dto.response

data class PageInfo<T> (
    val total: Long,
    val list: List<T>
)