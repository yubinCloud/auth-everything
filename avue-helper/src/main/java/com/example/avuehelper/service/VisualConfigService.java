package com.example.avuehelper.service;

import cn.hutool.core.lang.Dict;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
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

    public List<String> getComponentFromVisual(long visualId) {
        var vc = visualConfigMapper.selectComponentByVisualId(visualId);
        if (vc == null) {
            return Collections.emptyList();
        }
        JSONArray components = JSONUtil.parseArray(vc.getComponent());
        return components.toList(Dict.class).stream().map(d -> d.get("index").toString()).toList();
    }
}
