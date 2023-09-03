package com.example.ssoauth.service;

import cn.dev33.satoken.dao.SaTokenDaoRedisJackson;
import cn.hutool.json.JSONUtil;
import com.example.ssoauth.dao.param.DeleteUserPermissionParam;
import com.example.ssoauth.dao.param.NewUserDao;
import com.example.ssoauth.dao.param.PermissionInsertParam;
import com.example.ssoauth.dao.param.UserSelectCond;
import com.example.ssoauth.dao.result.UserDao;
import com.example.ssoauth.dto.request.NewUserDto;
import com.example.ssoauth.dto.request.UpdateUserReq;
import com.example.ssoauth.entity.User;
import com.example.ssoauth.exception.BaseBusinessException;
import com.example.ssoauth.exception.LoginException;
import com.example.ssoauth.exception.UserAddException;
import com.example.ssoauth.exchange.JupyterExchange;
import com.example.ssoauth.exchange.request.JupyterUserUpdateRequest;
import com.example.ssoauth.exchange.request.JupyterUsrCreateRequest;
import com.example.ssoauth.exchange.response.JR;
import com.example.ssoauth.mapper.UserMapper;
import com.example.ssoauth.mapstruct.UserConverter;
import com.example.ssoauth.mapstructutil.UserConverterUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
public class UserService {
    private final JupyterService jupyterService;

    private final UserMapper userMapper;

    private final UserConverter userConverter;

    private final UserConverterUtil userConverterUtil;

    private final JupyterExchange jupyterExchange;

    private final SaTokenDaoRedisJackson redisJackson;

    static private final String KEY_PREFIX_PERM = "aet:auth-perm:";

    @Transactional
    public void addUser(NewUserDto userDto, String whoAmI) {
        String jupyterToken = findJupyterToken(whoAmI);
        var jupyterReq = new JupyterUsrCreateRequest();
        jupyterReq.setAdmin(userDto.getJupyterhubAdmin());
        var jupyterResp = jupyterExchange.createUser(userDto.getUsername(), jupyterReq, jupyterToken);
        if (jupyterResp.getCode() != JR.SUCCESS) {
            throw new UserAddException("Exception in jupyter-service: " + jupyterResp.getData());
        }
        NewUserDao userDao = userConverter.toNewUserDao(userDto);
        int effect = userMapper.insert(userDao);
        if (effect == 0) {
            throw new UserAddException("Exception when insert database.");
        }
    }

    public User findByUsername(String username) {
        UserDao userDao = userMapper.selectByUsername(username);
        return userConverterUtil.toUser(userDao);
    }

    @Transactional
    public void deleteByUsername(String username, String whoAmI) {
        String jupyterToken = findJupyterToken(whoAmI);
        var jupyterResp = jupyterExchange.deleteUser(username, jupyterToken);
        if (jupyterResp.getCode() != JR.SUCCESS) {
            throw new BaseBusinessException("Exception in jupyter-service: " + jupyterResp.getData());
        }
        userMapper.deleteByUsername(username);
    }

    public PageInfo<User> selectByPage(UserSelectCond cond, int pageNum, int pageSize) {
        return PageHelper.startPage(pageNum, pageSize).doSelectPageInfo(
                () -> userMapper.selectByCond(cond).stream().map(userConverterUtil::toUser).toList()
        );
    }

    @Transactional
    public void updateUserInfo(UpdateUserReq updateUserReq, String whoAmI) {
        if (updateUserReq.getJupyterhubAdmin() != null) {
            String jupyterToken = findJupyterToken(whoAmI);
            var body = new JupyterUserUpdateRequest();
            body.setName(updateUserReq.getUsername());
            body.setAdmin(updateUserReq.getJupyterhubAdmin());
            var jupyterResp = jupyterExchange.updateUser(updateUserReq.getUsername(), body, jupyterToken);
            if (jupyterResp.getCode() != JR.SUCCESS) {
                throw new BaseBusinessException("Exception in update jupyterhub-admin.");
            }
        }
        var param = userConverter.toUpdateUserParam(updateUserReq);
        userMapper.updateUserInfo(param);
    }

    @Transactional
    public void addPermission(String username, List<String> permList) {
        String jsonStr = JSONUtil.parseArray(permList).toString();
        var insertParam = new PermissionInsertParam(username, jsonStr);
        userMapper.appendPermission(insertParam);
        redisJackson.delete(KEY_PREFIX_PERM + username);
    }

    @Transactional
    public void deletePermission(String username, String permission) {
        var param = new DeleteUserPermissionParam(username, permission);
        userMapper.deletePermission(param);
        redisJackson.delete(KEY_PREFIX_PERM + username);
    }

    private String findJupyterToken(String username) {
        var ctx = jupyterService.findCtx(username);
        if (ctx == null) {
            throw new LoginException("用户的 jupyter-token 获取失败，请重新登陆");
        }
        return "token " + ctx;
    }
}
