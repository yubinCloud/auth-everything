package com.example.eusersso.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "登录的响应结果")
public class LoginResp {

    private String username;

    private String screenName;

    private String mobile;

    private String token;
}
