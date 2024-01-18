package com.example.avuehelper.service;

import cn.hutool.json.JSONUtil;
import com.example.avuehelper.dao.VisualDao;
import com.example.avuehelper.dto.response.VisualName;
import com.example.avuehelper.entity.VisualVariable;
import com.example.avuehelper.mapper.VisualMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VisualService {

    private final VisualMapper visualMapper;

    public VisualVariable getVariable(long visualId) {
        VisualDao visualDao = visualMapper.selectVariableByVisualId(visualId);
        if (Objects.isNull(visualDao)) {
            return null;
        }
        VisualVariable variable = new VisualVariable();
        variable.setId(visualDao.getId());
        variable.setVariable(visualDao.getVariable());
        return variable;
    }

    public int updateVariable(VisualVariable variable) {
        return visualMapper.updateVariable(variable);
    }

    public VisualName queryVisualName(long visualId) {
        return visualMapper.queryVisualName(visualId);
    }

    public Map<Long, VisualName> batchQueryVisualName(List<Long> visualIds) {
        return visualMapper.queryBatchVisualName(visualIds).entrySet()
                .stream()
                .filter(entry -> Objects.nonNull(entry.getValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> {
                    var map = entry.getValue();
                    return new VisualName(map.get("visualName"), map.get("categoryName"));
                }));
    }
}
