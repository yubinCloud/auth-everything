package com.example.ssoauth.service;

import cn.dev33.satoken.dao.SaTokenDaoRedisJackson;
import cn.hutool.json.JSONUtil;
import com.example.ssoauth.dao.param.DeleteUserPermissionParam;
import com.example.ssoauth.dao.param.NewUserDao;
import com.example.ssoauth.dao.param.PermissionInsertParam;
import com.example.ssoauth.dao.param.UserSelectCond;
import com.example.ssoauth.dao.result.UserDao;
import com.example.ssoauth.dto.request.DeleteUserReq;
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
import com.example.ssoauth.util.LoginIdUtil;
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

    private final LoginIdUtil loginIdUtil;

    static private final String KEY_PREFIX_PERM = "aet:auth-perm:";
    static private final Integer DEFAULT_TENANT_ID = 1;

    @Transactional
    public void addUser(NewUserDto userDto, String whoAmI, Integer tenantId) {
        //生成loginId
        String loginId = loginIdUtil.appendLoginId(tenantId, whoAmI);
        //查询当前操作人的 jupyter token
        String jupyterToken = findJupyterToken(loginId);
        var jupyterReq = new JupyterUsrCreateRequest();
        jupyterReq.setAdmin(userDto.getJupyterhubAdmin());
        String username = userDto.getUsername();
        var jupyterResp = jupyterExchange.createUser(username, jupyterReq, jupyterToken);
        if (jupyterResp.getCode() != JR.SUCCESS) {
            throw new UserAddException("Exception in jupyter-service: " + jupyterResp.getData());
        }
        NewUserDao userDao = userConverter.toNewUserDao(userDto);
        //添加默认租户id
        if (userDao.getTenantId() == null) {
            userDao.setTenantId(DEFAULT_TENANT_ID);
        }
        //新增user
        int effect = userMapper.insert(userDao);
        if (effect == 0) {
            throw new UserAddException("Exception when insert database.");
        }
    }

    public User findByUsername(String username) {
        UserDao userDao = userMapper.selectByUsername(username);
        return userConverterUtil.toUser(userDao);
    }

    public User findByUsernameAndTenantId(String username, Integer tenantId) {
        UserDao userDao = userMapper.selectByUsernameAndTenantId(username, tenantId);
        return userConverterUtil.toUser(userDao);
    }

    @Transactional
    public void deleteByUsernameAndTenantId(DeleteUserReq req, String whoAmI, Integer tenantId) {
        //redis中jupyter的token,key为loginId,需要先拼接
        String loginId = loginIdUtil.appendLoginId(tenantId, whoAmI);
        String jupyterToken = findJupyterToken(loginId);

        String username = req.getUsername();

        var jupyterResp = jupyterExchange.deleteUser(username, jupyterToken);
        if (jupyterResp.getCode() != JR.SUCCESS) {
            throw new BaseBusinessException("Exception in jupyter-service: " + jupyterResp.getData());
        }

        //删除user
        userMapper.deleteByUsernameAndTenantId(username, req.getTenantId());
    }

    public PageInfo<User> selectByPage(UserSelectCond cond, int pageNum, int pageSize) {
        return PageHelper.startPage(pageNum, pageSize).doSelectPageInfo(
                () -> userMapper.selectByCond(cond).stream().map(userConverterUtil::toUser).toList()
        );
    }

    @Transactional
    public void updateUserInfo(UpdateUserReq updateUserReq, String whoAmI, Integer tenantId) {
//        if (updateUserReq.getJupyterhubAdmin() != null) {
//            String jupyterToken = findJupyterToken(whoAmI);
//            var body = new JupyterUserUpdateRequest();
//            body.setName(updateUserReq.getUsername());
//            body.setAdmin(updateUserReq.getJupyterhubAdmin());
//            var jupyterResp = jupyterExchange.updateUser(updateUserReq.getUsername(), body, jupyterToken);
//            if (jupyterResp.getCode() != JR.SUCCESS) {
//                throw new BaseBusinessException("Exception in update jupyterhub-admin.");
//            }
//        }
        var param = userConverter.toUpdateUserParam(updateUserReq);
        userMapper.updateUserInfo(param);
    }

    @Transactional
    public void addPermission(String username, Integer tenantId, List<String> permList) {
        String jsonStr = JSONUtil.parseArray(permList).toString();
        if (tenantId == null) {
            tenantId = DEFAULT_TENANT_ID;
        }
        var insertParam = new PermissionInsertParam(username, tenantId, jsonStr);
        userMapper.appendPermission(insertParam);
        String loginId = loginIdUtil.appendLoginId(tenantId, username);
        redisJackson.delete(KEY_PREFIX_PERM + loginId);
    }

    @Transactional
    public void deletePermission(String username, Integer tenantId, String permission) {
        if (tenantId == null) {
            tenantId = DEFAULT_TENANT_ID;
        }
        var param = new DeleteUserPermissionParam(username, tenantId, permission);
        userMapper.deletePermission(param);
        String loginId = loginIdUtil.appendLoginId(tenantId, username);
        redisJackson.delete(KEY_PREFIX_PERM + loginId);
    }

    private String findJupyterToken(String loginId) {
        var ctx = jupyterService.findCtx(loginId);
        if (ctx == null) {
            throw new LoginException("用户的 jupyter-token 获取失败，请重新登陆");
        }
        return "token " + ctx;
    }
}
