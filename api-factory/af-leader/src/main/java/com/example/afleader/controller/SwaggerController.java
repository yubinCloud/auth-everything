package com.example.afleader.controller;

import com.example.afleader.config.ConstantConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Objects;

@Controller
@RequestMapping("/swagger")
@RequiredArgsConstructor
@Validated
@Slf4j
@Tag(name = "Swagger UI 生成")
public class SwaggerController {

    @GetMapping("/ui")
    @Operation(summary = "打开 Swagger 调试界面")
    public String getSwaggerUI(
            Model model,
            @RequestParam @NotBlank @Parameter(description = "路由路径", required = true) String routePath,
            @RequestHeader(value = "x-forwarded-proto") String proto,
            @RequestHeader(value = "x-forwarded-host") String host,
            @RequestHeader(value = ConstantConfig.IUSER_TOKEN_HEADER) String token
    ) {
        String openapiURL = String.format("%s://%s/af-leader/dynamic-route/openapi?path=%s", proto, host, routePath);
        if (Objects.nonNull(token)) {
            model.addAttribute("token", token);
        }
        model.addAttribute("openapiURL", openapiURL);
        return "swagger-ui/index";
    }
}
