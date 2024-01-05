package com.example.afleader.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "调试解密的请求")
public class DebugDecryptionRequest {

    @Schema(description = "加密接口返回的结果")
    @NotNull(message = "数据不允许为空")
    private String response;

}
