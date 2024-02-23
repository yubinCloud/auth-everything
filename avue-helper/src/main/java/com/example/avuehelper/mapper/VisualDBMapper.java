package com.example.avuehelper.mapper;

import com.example.avuehelper.entity.VisualDB;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface VisualDBMapper {

    VisualDB selectById(long id);

}
