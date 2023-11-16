package com.example.avuehelper.mapper;

import com.example.avuehelper.dao.VisualDao;
import com.example.avuehelper.dto.response.VisualName;
import com.example.avuehelper.entity.VisualVariable;
import org.apache.ibatis.annotations.Mapper;

/**
* @author yubin
* @description 针对表【blade_visual(可视化表)】的数据库操作Mapper
* @createDate 2023-10-31 10:16:46
* @Entity generator.domain.BladeVisual
*/
@Mapper
public interface VisualMapper {

    VisualDao selectVariableByVisualId(long visualId);

    int updateVariable(VisualVariable param);

    VisualName queryVisualName(long visualId);

}
