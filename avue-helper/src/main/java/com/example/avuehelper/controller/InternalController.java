package com.example.avuehelper.controller;

import com.example.avuehelper.dto.request.BatchQueryVisualNameRequest;
import com.example.avuehelper.dto.response.R;
import com.example.avuehelper.dto.response.VisualName;
import com.example.avuehelper.service.VisualService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/internal")
@RequiredArgsConstructor
@Validated
@Slf4j
@Tag(
        name = "【内部接口】微服务内部调用接口"
)
public class InternalController {


    private final VisualService visualService;

    @PostMapping("/visual-name/batch")
    @Operation(summary = "批量根据大屏 id 获取大屏名称")
    public R<Map<String, VisualName>> batchQueryVisualName(@RequestBody @Valid BatchQueryVisualNameRequest body) {
        var results = visualService.batchQueryVisualName(body.getIds()).entrySet()
                .stream()
                .collect(Collectors.toMap(entry -> String.valueOf(entry.getKey()), Map.Entry::getValue));
        return R.ok(results);
    }
}
