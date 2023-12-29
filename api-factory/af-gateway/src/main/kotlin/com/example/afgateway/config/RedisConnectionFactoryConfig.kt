package com.example.afgateway.config


import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory

@Configuration
class RedisConnectionFactoryConfig(val afRedisProperties: AfRedisProperties) {

    @Bean(name = ["afRedisConnectionFactory"])
    fun afRedisConnectionFactory(): ReactiveRedisConnectionFactory {
        val conf = RedisStandaloneConfiguration()
        conf.hostName = afRedisProperties.host
        conf.port = afRedisProperties.port
        conf.database = afRedisProperties.database
        return LettuceConnectionFactory(conf)
    }
}