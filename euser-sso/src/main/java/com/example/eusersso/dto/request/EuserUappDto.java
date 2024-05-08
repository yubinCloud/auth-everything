package com.example.eusersso.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "新增或修改uapp用户的uapp的相关信息")
public class EuserUappDto {


    @Schema(description = "uapp ID")
    private String uappId;

    @NotBlank
    @Schema(description = "uapp用户的 username")
    private String username;

}
