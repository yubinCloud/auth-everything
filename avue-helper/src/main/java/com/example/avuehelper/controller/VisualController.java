package com.example.avuehelper.controller;

import com.example.avuehelper.dto.response.R;
import com.example.avuehelper.dto.response.VisualComponentSimple;
import com.example.avuehelper.dto.response.VisualName;
import com.example.avuehelper.entity.VisualVariable;
import com.example.avuehelper.service.VisualConfigService;
import com.example.avuehelper.service.VisualService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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

    private final VisualService visualService;

    @GetMapping("/components/{visualId}")
    @Operation(summary = "查看大屏的所有组件")
    R<List<VisualComponentSimple>> getVisualComponents(@PathVariable @Valid @Min(0) long visualId) {
        var components = visualConfigService.getComponentFromVisual(visualId);
        return R.ok(components);
    }

    @GetMapping("/variable/{visualId}")
    @Operation(summary = "获取大屏的变量")
    R<VisualVariable> getVariable(@PathVariable @Valid @Min(0) long visualId) {
        VisualVariable variable = visualService.getVariable(visualId);
        return R.ok(variable);
    }

    @PostMapping("/variable/update")
    @Operation(summary = "更新大屏的变量")
    R<String> updateVariable(@RequestBody @Valid VisualVariable variable) {
        int result = visualService.updateVariable(variable);
        return result > 0? R.ok("success"): R.error("未查找到该大屏 ID", null);
    }

    @GetMapping("/visual-name/{visualId}")
    @Operation(summary = "根据大屏 ID 获取大屏的名字")
    R<VisualName> queryVisualName(@PathVariable @Valid @NotBlank long visualId) {
        var visualName = visualService.queryVisualName(visualId);
        return R.ok(visualName);
    }
}
