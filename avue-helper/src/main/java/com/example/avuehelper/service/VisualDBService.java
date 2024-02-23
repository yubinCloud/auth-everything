package com.example.avuehelper.service;

import com.example.avuehelper.entity.VisualDB;
import com.example.avuehelper.mapper.VisualDBMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VisualDBService {

    private final VisualDBMapper visualDBMapper;

    public VisualDB queryById(long id) {
        return visualDBMapper.selectById(id);
    }

}
