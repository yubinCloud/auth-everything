package com.example.avuehelper.mapper;

import com.example.avuehelper.dao.VisualConfigDao;
import org.apache.ibatis.annotations.Mapper;

/**
* @author yubin
* @description 针对表【blade_visual_config(可视化配置表)】的数据库操作Mapper
* @createDate 2023-10-23 10:17:55
* @Entity com.example.avuehelper.dao.BladeVisualConfig
*/
@Mapper
public interface VisualConfigMapper {

    VisualConfigDao selectComponentByVisualId(long visualId);
}




