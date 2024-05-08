package com.example.avuehelper.controller;

import com.example.avuehelper.dto.request.NewVisualDBDto;
import com.example.avuehelper.dto.response.R;
import com.example.avuehelper.entity.VisualDB;
import com.example.avuehelper.service.VisualDBService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/db")
@RequiredArgsConstructor
@Validated
@Slf4j
@Tag(name = "数据源管理")
public class VisualDBController {

    private final VisualDBService visualDBService;

    @GetMapping("/query/{id}")
    public R<VisualDB> queryVisualDb(@PathVariable long id) {
        return R.ok(visualDBService.queryById(id));
    }

    @PostMapping("/add")
    public R saveDB(@RequestBody NewVisualDBDto db,
                    @RequestHeader("X-TenantId") Integer tenantId) {
        db.setTenantId(tenantId);
        visualDBService.insertOne(db);
        return R.ok();
    }
}
