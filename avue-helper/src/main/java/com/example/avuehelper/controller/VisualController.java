package com.example.avuehelper.controller;

import com.example.avuehelper.dto.response.R;
import com.example.avuehelper.service.VisualConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/visual")
@RequiredArgsConstructor
@Validated
@Slf4j
@Tag(
        name = "大屏信息"
)
public class VisualController {

    private final VisualConfigService visualConfigService;

    @GetMapping("/components/{visualId}")
    @Operation(summary = "查看大屏的所有组件")
    R<List<String>> getVisualComponents(@PathVariable @Valid @Min(0) long visualId) {
        List<String> components = visualConfigService.getComponentFromVisual(visualId);
        return R.ok(components);
    }
}
