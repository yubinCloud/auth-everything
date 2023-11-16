package com.example.avuehelper.service;

import cn.hutool.core.lang.Dict;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.example.avuehelper.dto.response.VisualComponentSimple;
import com.example.avuehelper.entity.VisualConfig;
import com.example.avuehelper.mapper.VisualConfigMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VisualConfigService {

    private final VisualConfigMapper visualConfigMapper;

    public List<VisualComponentSimple> getComponentFromVisual(long visualId) {
        var vc = visualConfigMapper.selectComponentByVisualId(visualId);
        if (vc == null) {
            return Collections.emptyList();
        }
        JSONArray components = JSONUtil.parseArray(vc.getComponent());
        return JSONUtil.toList(components, VisualComponentSimple.class);
    }
}
