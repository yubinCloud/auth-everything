package com.example.ssoauth.service;

import cn.hutool.json.JSONUtil;
import com.example.ssoauth.dao.param.DeleteUserPermissionParam;
import com.example.ssoauth.dao.param.NewUserDao;
import com.example.ssoauth.dao.param.PermissionInsertParam;
import com.example.ssoauth.dao.param.UserSelectCond;
import com.example.ssoauth.dao.result.UserDetailDao;
import com.example.ssoauth.dao.result.UserWithRoleDao;
import com.example.ssoauth.dto.request.NewUserDto;
import com.example.ssoauth.dto.request.UpdateUserReq;
import com.example.ssoauth.mapper.UserMapper;
import com.example.ssoauth.mapstruct.UserConverter;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;

    private final UserConverter userConverter;

    @Transactional
    public void addUser(NewUserDto userDto) {
        NewUserDao userDao = userConverter.toNewUserDao(userDto);
        userMapper.insert(userDao);
    }

    public UserDetailDao findByUsername(String username) {
        UserWithRoleDao userDao = userMapper.selectByUsername(username);
        System.out.println(userDao.getPermissionList());
        return userConverter.toUserDetail(userDao);
    }

    public void deleteByUsername(String username) {
        userMapper.deleteByUsername(username);
    }

    public PageInfo<UserWithRoleDao> selectByPage(UserSelectCond cond, int pageNum, int pageSize) {
        return PageHelper.startPage(pageNum, pageSize).doSelectPageInfo(
                () -> userMapper.selectByCond(cond)
        );
    }

    @Transactional
    public void updateUserInfo(UpdateUserReq updateUserReq) {
        var param = userConverter.toUpdateUserParam(updateUserReq);
        userMapper.updateUserInfo(param);
    }

    @Transactional
    public void addPermission(String username, List<String> permList) {
        String jsonStr = JSONUtil.parseArray(permList).toString();
        var insertParam = new PermissionInsertParam(username, jsonStr);
        userMapper.appendPermission(insertParam);
    }

    @Transactional
    public void deletePermission(String username, String permission) {
        var param = new DeleteUserPermissionParam(username, permission);
        userMapper.deletePermission(param);
    }
}
