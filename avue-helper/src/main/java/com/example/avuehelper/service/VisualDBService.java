package com.example.avuehelper.service;

import com.example.avuehelper.dto.request.NewVisualDBDto;
import com.example.avuehelper.dto.response.PageResult;
import com.example.avuehelper.entity.VisualDB;
import com.example.avuehelper.mapper.VisualDBMapper;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VisualDBService {

    private final VisualDBMapper visualDBMapper;

    public VisualDB queryById(long id) {
        return visualDBMapper.selectById(id);
    }

    public int insertOne(NewVisualDBDto db) {
        // TODO 这里的 ID 生成方式有点问题
        db.setId(Math.round(new Date().getTime()+Math.random()*10000));
        return visualDBMapper.insertOne(db);
    }

    @Transactional
    public PageResult<VisualDB> queryPage(int pageNum, int pageSize, int tenantId) {
        int offset = (pageNum - 1) * pageSize;
        var records = visualDBMapper.selectPage(offset, pageSize, tenantId);
        var count = visualDBMapper.countAll(tenantId);
        PageResult<VisualDB> result = new PageResult<>();
        result.setList(records);
        result.setTotal(count);
        return result;
    }
}
