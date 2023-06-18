package com.example.ssoauth.mapper;

import com.example.ssoauth.dao.param.*;
import com.example.ssoauth.dao.result.UserDao;
import com.example.ssoauth.dao.result.UserWithRoleDao;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
* @author yubin
* @description 针对表【user(用户信息表)】的数据库操作Mapper
* @createDate 2023-05-26 19:30:23
* @Entity com.example.ssoauth.dao.User
*/
@Mapper
public interface UserMapper {

    int deleteByUsername(String username);

    int insert(NewUserDao userDao);

    List<UserWithRoleDao> selectByCond(UserSelectCond cond);

    UserWithRoleDao selectByUsername(String username);

    void updateUserInfo(UpdateUserParam param);

    void appendPermission(PermissionInsertParam param);

    void deletePermission(DeleteUserPermissionParam param);
}
