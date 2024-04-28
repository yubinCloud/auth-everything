package com.example.eusersso.controller;

import com.example.eusersso.dto.request.NewUserDto;
import com.example.eusersso.dto.response.R;
import com.example.eusersso.service.EuserForUappService;
import com.example.eusersso.util.ConstantUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/uapp")
@RequiredArgsConstructor
@Validated
@Slf4j
@Tag(
        name = "【uapp】用户信息的管理"
)
public class AdminForUappController {

    private final EuserForUappService euserService;

    @PostMapping("/user/create")
    @Operation(summary = "创建用户")
    public R<String> createUser(
            @RequestBody @Valid NewUserDto userDto,
            @RequestHeader(ConstantUtil.IUSER_WHOAMI_HEADER) String whoAmI,
            @RequestHeader(ConstantUtil.TENANT_ID_HEADER) Integer tenantId
    ) {
        int result = euserService.createEuser(userDto, whoAmI, tenantId);
        if (result >= 1) {
            return R.ok("success");
        }
        return new R<>(R.CODE_ERROR, "插入失败，请稍后尝试", "fail");
    }
}
