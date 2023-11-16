package com.example.eusersso.config;

import lombok.RequiredArgsConstructor;
import net.spy.memcached.ConnectionFactory;
import net.spy.memcached.ConnectionFactoryBuilder;
import net.spy.memcached.FailureMode;
import net.spy.memcached.MemcachedClient;
import net.spy.memcached.auth.AuthDescriptor;
import net.spy.memcached.auth.PlainCallbackHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class MemcachedClientConfig {

    private final MemcachedProperties memcachedProperties;

    @Bean
    public MemcachedClient memcachedClient() throws IOException {
        System.setProperty("net.spy.log.LoggerImpl", "net.spy.memcached.compat.log.SunLogger");
        PlainCallbackHandler pc = new PlainCallbackHandler(memcachedProperties.getUsername(), memcachedProperties.getPassword());
        AuthDescriptor authDescriptor = new AuthDescriptor(new String[]{"PLAIN"}, pc);
        ConnectionFactory factory = new ConnectionFactoryBuilder()
                .setProtocol(ConnectionFactoryBuilder.Protocol.BINARY)
                .setFailureMode(FailureMode.Retry)
                .setAuthDescriptor(authDescriptor)
                .build();
        var address = new InetSocketAddress(memcachedProperties.getHost(), memcachedProperties.getPort());
        return new MemcachedClient(factory, List.of(address));
    }
}
