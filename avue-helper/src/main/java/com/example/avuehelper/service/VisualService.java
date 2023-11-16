package com.example.avuehelper.service;

import com.example.avuehelper.dao.VisualDao;
import com.example.avuehelper.dao.VisualVariableDao;
import com.example.avuehelper.dto.response.VisualName;
import com.example.avuehelper.entity.VisualVariable;
import com.example.avuehelper.mapper.VisualMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

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
}
