package com.example.ssoauth.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "登录的响应结果")
public class LoginResp {

    private String username;
    private String screenName;
    private List<String> roleList;
    private List<String> permissionList;
    private String token;
    private String note;

    @Schema(description = "用户创建时间的 Unix 时间戳，是从1970年1月1日开始的秒数，JS 转换代码：`new Date(parseInt(d) * 1000)`")
    private Long createTime;
}
