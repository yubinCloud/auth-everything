package com.example.eusersso.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

@Configuration
@RequiredArgsConstructor
public class RedisConnectionFactoryConfig {

    private final RedisProperties redisProperties;

    private final AfRedisProperties afRedisProperties;

    /**
     * Sa-Token 库使用的 Redis 连接信息
     * @return
     */
    @Bean
    @Primary
    public RedisConnectionFactory redisConnectionFactory() {
        var configuration = new RedisStandaloneConfiguration();
        configuration.setDatabase(redisProperties.getDatabase());
        configuration.setHostName(redisProperties.getHost());
        configuration.setPort(afRedisProperties.getPort());
        return new LettuceConnectionFactory(configuration);
    }

    /**
     * 存储 public-api 信息的 Redis 连接信息，用于与 af-gateway 进行交互
     * @return
     */
    @Bean(name = "afRedisConnectionFactory")
    public RedisConnectionFactory afRedisConnectionFactory() {
        var configuration = new RedisStandaloneConfiguration();
        configuration.setHostName(afRedisProperties.getHost());
        configuration.setPort(afRedisProperties.getPort());
        configuration.setDatabase(afRedisProperties.getDatabase());
        return new LettuceConnectionFactory(configuration);
    }

}
