package com.example.eusersso.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "更新用户信息")
public class UpdateEuserDto {

    @Schema(description = "所要更新用户的 username")
    private String username;

    @Schema(description = "更新的密码")
    private String password;

    @Schema(description = "更新的手机号")
    private String mobile;

    @Schema(description = "更新的 checked")
    private Boolean checked;

    @Schema(description = "更新的 screen name")
    private String screenName;

    @Schema(description = "更新的 note")
    private String note;

    @Schema(description = "新的 avue roles")
    private List<Integer> avueRoles;

    @Schema(description = "新的 public api 的 ID 列表")
    private List<Integer> publicApiIds;

}
