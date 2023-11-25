package com.example.avuehelper.controller;

import com.example.avuehelper.config.MinioProperties;
import com.example.avuehelper.dto.response.R;
import com.example.avuehelper.dto.response.VideoUploadResp;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

@RestController
@RequestMapping("/video")
@RequiredArgsConstructor
@Validated
@Slf4j
@Tag(
        name = "大屏信息"
)
public class VideoController {

    private final MinioClient minioClient;

    private final MinioProperties minioProperties;

    @PostMapping("/upload")
    @ResponseBody
    public R<VideoUploadResp> videoUpload(@RequestParam("uploadFile") MultipartFile uploadFile) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        if (Objects.isNull(uploadFile) || uploadFile.getSize() == 0) {
            return R.error("上传文件不能为空", null);
        }
        // 获取文件名
        String originalFilename = uploadFile.getOriginalFilename();
        String fileName = "video/" + originalFilename;
        minioClient.putObject(PutObjectArgs.builder().bucket(minioProperties.getBucket()).object(fileName).stream(uploadFile.getInputStream(), uploadFile.getSize(), -1).contentType(uploadFile.getContentType()).build());
        VideoUploadResp resp = new VideoUploadResp();
        resp.setExternalUrl("/s3/" + minioProperties.getBucket() + "/" + fileName);
        resp.setInternalUrl(minioProperties.getUrl() + "/" + minioProperties.getBucket() + "/" + fileName);
        return R.ok(resp);
    }
}
