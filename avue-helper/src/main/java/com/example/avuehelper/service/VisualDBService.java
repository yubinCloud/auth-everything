package com.example.avuehelper.service;

import com.example.avuehelper.dto.request.NewVisualDBDto;
import com.example.avuehelper.entity.VisualDB;
import com.example.avuehelper.mapper.VisualDBMapper;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VisualDBService {

    private final VisualDBMapper visualDBMapper;

    public VisualDB queryById(long id) {
        return visualDBMapper.selectById(id);
    }

    public void insertOne(NewVisualDBDto db) {
        db.setId(Math.round(new Date().getTime()+Math.random()*10000));
        visualDBMapper.insertOne(db);
    }
}
