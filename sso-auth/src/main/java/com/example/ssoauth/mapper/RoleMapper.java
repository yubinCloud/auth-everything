package com.example.ssoauth.mapper;


import com.example.ssoauth.dao.result.RoleDao;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
* @author yubin
* @description 针对表【role(角色表)】的数据库操作Mapper
* @createDate 2023-09-03 10:33:20
* @Entity generator.domain.Role
*/
@Mapper
public interface RoleMapper {

    List<RoleDao> selectAll();

    RoleDao selectByPrimaryKey(Integer id);

    int deleteByPrimaryKey(Integer id);

    int updateByPrimaryKey(RoleDao record);

}
