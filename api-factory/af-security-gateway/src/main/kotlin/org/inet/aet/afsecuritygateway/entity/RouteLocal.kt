package org.inet.aet.afsecuritygateway.entity

import org.apache.curator.framework.recipes.cache.CuratorCache

data class RouteLocal(
    var curatorCache: CuratorCache,  // route node 本身的 CuratorCache
    var encrypt: Boolean,
    var secretKey: ByteArray?
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RouteLocal

        if (curatorCache != other.curatorCache) return false
        if (encrypt != other.encrypt) return false
        if (!secretKey.contentEquals(other.secretKey)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = curatorCache.hashCode()
        result = 31 * result + encrypt.hashCode()
        result = 31 * result + secretKey.contentHashCode()
        return result
    }
}
