package com.example.eusersso.dto.request.shandongtong.sso;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "通过山东通来登录的请求参数")
public class SdtLoginParam {

    @Schema(description = "山东通的授权码")
    @NotBlank
    private String code;

}
