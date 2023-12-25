package com.example.eusersso.controller;

import com.example.eusersso.dto.response.QueryPublicAPIPermissionResponse;
import com.example.eusersso.dto.response.R;
import com.example.eusersso.service.EuserForPublicApiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Objects;

@RestController
@RequestMapping("/internal")
@RequiredArgsConstructor
@Validated
@Slf4j
@Tag(
        name = "内部 RPC 接口（前端无法访问）"
)
public class InternalMicroserviceController {

    private final EuserForPublicApiService euserService;

    @GetMapping("/perms/public-api")
    @Operation(summary = "获取一个用户的 public-api 权限")
    public R<QueryPublicAPIPermissionResponse> queryPublicAPIPerms(
            @RequestParam("un") @NotBlank @Parameter(required = true) String username
    ) {
        var euser = euserService.queryCheckedByUsername(username);
        QueryPublicAPIPermissionResponse response = new QueryPublicAPIPermissionResponse();
        if (Objects.nonNull(euser)) {
            response.setRoutes(euser.getPublicApiIds());
        } else {
            response.setRoutes(Collections.emptyList());
        }
        return R.ok(response);
    }

}
