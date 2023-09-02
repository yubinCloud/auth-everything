package com.example.ssoauth.mapper;

import com.example.ssoauth.dao.result.RoleDao;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
* @author yubin
* @description 针对表【role(角色表)】的数据库操作Mapper
* @createDate 2023-05-26 20:25:00
* @Entity com.example.ssoauth.dao.Role
*/
@Mapper
public interface RoleMapper {

    int deleteByPrimaryKey(Long id);

    int insert(RoleDao record);

    int insertSelective(RoleDao record);

    List<RoleDao> selectAll();

    RoleDao selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(RoleDao record);

    int updateByPrimaryKey(RoleDao record);

}
