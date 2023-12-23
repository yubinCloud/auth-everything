package com.example.eusersso.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

@Configuration
@RequiredArgsConstructor
public class AfRedisConfig {

    private final AfRedisProperties afRedisProperties;


    @Bean
    public RedisConnectionFactory afRedisConnectionFactory() {
        var configuration = new RedisStandaloneConfiguration();
        configuration.setHostName(afRedisProperties.getHost());
        configuration.setPort(afRedisProperties.getPort());
        configuration.setDatabase(afRedisProperties.getDatabase());
        return new LettuceConnectionFactory(configuration);
    }

}
