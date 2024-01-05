package org.inet.aet.afsecuritygateway.service

import cn.hutool.crypto.SecureUtil
import cn.hutool.json.JSONUtil
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.benmanes.caffeine.cache.Cache
import org.apache.curator.framework.CuratorFramework
import org.apache.curator.framework.recipes.cache.CuratorCache
import org.apache.curator.framework.recipes.cache.CuratorCacheListener
import org.inet.aet.afsecuritygateway.entity.RouteLocal
import org.springframework.stereotype.Service
import java.util.*
import kotlin.collections.HashMap

@Service
class EncryptService(val objectMapper: ObjectMapper, val curatorFramework: CuratorFramework, val apiEncryptCache: Cache<String, RouteLocal>) {

    companion object {
        const val ENCRYPTION_MAGIC_NUMBER = "0521"
    }

    fun findRouteLocal(routePath: String): RouteLocal {
        val key = routePath
        var routeLocal = apiEncryptCache.getIfPresent(key)
        if (routeLocal == null) {
            val zkPath = key.replace("/", ".")
            val encryptFlagPath = "/route/$zkPath/encrypt"
            val needEncrypt = curatorFramework.checkExists().forPath(encryptFlagPath) != null
            val secretKey = if (needEncrypt) curatorFramework.data.forPath(encryptFlagPath) else null
            val curatorCache = CuratorCache.build(curatorFramework, encryptFlagPath, CuratorCache.Options.SINGLE_NODE_CACHE)
            val listener = CuratorCacheListener.builder()
                .forCreates { node -> run {
                    val item = apiEncryptCache.getIfPresent(key)
                    if (item != null) {
                        item.encrypt = true
                        item.secretKey = node.data
                    }
                } }
                .forChanges { _, node -> run {
                    val item = apiEncryptCache.getIfPresent(key)
                    if (item != null) {
                        item.secretKey = node.data
                    }
                } }
                .forDeletes { _ -> run {
                    val item = apiEncryptCache.getIfPresent(key)
                    if (item != null) {
                        item.encrypt = false
                        item.secretKey = null
                    }
                }}
                .build()
            curatorCache.start()
            curatorCache.listenable().addListener(listener)
            routeLocal = RouteLocal(curatorCache, needEncrypt, secretKey)
            apiEncryptCache.put(key, routeLocal)
        }
        return routeLocal
    }

    fun decrypt(body: String?, routeLocal: RouteLocal): String {
        val requestBody: String
        if (body == null) {
            return ""
        } else {
            if (!routeLocal.encrypt) {
                requestBody = body
            } else {
                val aesAlg = SecureUtil.aes(routeLocal.secretKey)
                requestBody = aesAlg.decryptStr(Base64.getDecoder().decode(body))
            }
        }
        val bodyJSON = JSONUtil.parseObj(requestBody)
        return objectMapper.writeValueAsString(bodyJSON)
    }

    fun encrypt(body: String?, routeLocal: RouteLocal): String {
        if (body == null) {
            return ""
        }
        if (!routeLocal.encrypt) {
            return body
        }
        val aesAlg = SecureUtil.aes(routeLocal.secretKey)
        val encryptedString = aesAlg.encryptBase64(body)
        val magic = aesAlg.encryptBase64(ENCRYPTION_MAGIC_NUMBER)
        val respJSON = HashMap<String, Any>()
        respJSON["data"] = encryptedString
        respJSON["magic"] = magic
        return objectMapper.writeValueAsString(respJSON)
    }


}

//fun main(args: Array<String>) {
//    val secretKey = "XbcpEtrXe+U9x/aNbw3wxQ=="
//    val aes = SecureUtil.aes(Base64.getDecoder().decode(secretKey))
//    val reqJSON = HashMap<String, Any>()
//    reqJSON["username"] = "inet"
//    var body = "{\"username\":\"inet\"}"
//    body = Base64.getEncoder().encodeToString(aes.encrypt(body))
//    println(body)
//}