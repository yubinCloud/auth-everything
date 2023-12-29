package com.example.afgateway.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.RedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer

/**
 * 配置 Redis 序列化器
 */
@Configuration
class RedisSerializationContextConfig {

    @Bean
    fun redisSerializationContext(): RedisSerializationContext<String, Any> {
        val builder = RedisSerializationContext.newSerializationContext<String, Any>();
        builder.key(StringRedisSerializer.UTF_8);
        builder.value(RedisSerializer.json());
        builder.hashKey(StringRedisSerializer.UTF_8);
        builder.hashValue(StringRedisSerializer.UTF_8);
        return builder.build();
    }

}