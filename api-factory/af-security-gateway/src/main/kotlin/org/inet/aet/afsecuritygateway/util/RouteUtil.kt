package org.inet.aet.afsecuritygateway.util

import java.util.StringJoiner

/**
 * 将 `/dynamic/local-pg` -> `/local-pg`
 */
fun parseAfRoutePath(routePath: String): String {
    val requestPathSplits = routePath.split("/")
    val joiner = StringJoiner("/", "/", "")
    for (i in 2..< requestPathSplits.size) {
        joiner.add(requestPathSplits[i])
    }
    return joiner.toString()
}