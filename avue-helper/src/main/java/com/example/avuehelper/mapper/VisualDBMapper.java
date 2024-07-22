package com.example.avuehelper.mapper;

import com.example.avuehelper.dto.request.NewVisualDBDto;
import com.example.avuehelper.entity.VisualDB;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface VisualDBMapper {

    VisualDB selectById(long id);

    int insertOne(NewVisualDBDto db);

    List<VisualDB> selectPage(int offset, int limit, int tenantId);

    long countAll(int tenantId);
}
