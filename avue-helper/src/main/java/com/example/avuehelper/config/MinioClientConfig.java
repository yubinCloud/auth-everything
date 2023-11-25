package com.example.avuehelper.config;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.SetBucketPolicyArgs;
import io.minio.errors.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class MinioClientConfig {

    private final MinioProperties minioProperties;

    private final ResourceLoader resourceLoader;

    @Bean
    public MinioClient minioClient() throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        var client = MinioClient.builder()
                .endpoint(minioProperties.getUrl())
                .credentials(minioProperties.getAccessKey(), minioProperties.getSecretKey())
                .build();
        // 检查 bucket 是否存在
        boolean found = client.bucketExists(BucketExistsArgs.builder().bucket(minioProperties.getBucket()).build());
        if (!found) {
            log.info("MinIO bucket `" + minioProperties.getBucket() + "` not found, prepare to create it.");
            client.makeBucket(MakeBucketArgs.builder().bucket(minioProperties.getBucket()).build());
        } else {
            log.info("MinIO bucket `" + minioProperties.getBucket() + "` already exists.");
        }
        // 将 bucket 的策略设置为 public
        Resource bucketPolicyFile = resourceLoader.getResource("classpath:minio-bucket-policy.json");
        byte[] buf = new byte[1024];
        int len = bucketPolicyFile.getInputStream().read(buf);
        String policy = new String(buf, 0, len, StandardCharsets.UTF_8).replaceAll("\\$\\{bucket-name}", minioProperties.getBucket());
        client.setBucketPolicy(
                SetBucketPolicyArgs.builder().bucket(minioProperties.getBucket()).config(policy).build()
        );
        return client;
    }
}
