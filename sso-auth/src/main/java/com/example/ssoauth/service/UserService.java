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
import com.example.ssoauth.exception.PermissionException;
import com.example.ssoauth.exception.UserAddException;
import com.example.ssoauth.exchange.JupyterExchange;
import com.example.ssoauth.exchange.request.JupyterUsrCreateRequest;
import com.example.ssoauth.exchange.response.JR;
import com.example.ssoauth.mapper.UserMapper;
import com.example.ssoauth.mapstruct.UserConverter;
import com.example.ssoauth.mapstructutil.UserConverterUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final JupyterService jupyterService;

    private final UserMapper userMapper;

    private final UserConverter userConverter;

    private final UserConverterUtil userConverterUtil;

    private final JupyterExchange jupyterExchange;

    private final SaTokenDaoRedisJackson redisJackson;

    static private final String KEY_PREFIX_PERM = "aet:auth-perm:";
    static private final Integer DEFAULT_TENANT_ID = 1;

    @Transactional
    public void addUser(NewUserDto userDto, String whoAmI, Integer tenantId) {
        //权限校验
        List<Integer> roleList = userDto.getRoleList();
        boolean permission = roleCheck(whoAmI, roleList);
        if (!permission) {
            throw new PermissionException();
        }

        //查询当前操作人的 jupyter token
        String jupyterToken = findJupyterToken(whoAmI);
        var jupyterReq = new JupyterUsrCreateRequest();
        jupyterReq.setAdmin(userDto.getJupyterhubAdmin());
        String username = userDto.getUsername();
        //jupyter注册用户
        var jupyterResp = jupyterExchange.createUser(username, jupyterReq, jupyterToken);
        if (jupyterResp.getCode() != JR.SUCCESS) {
            throw new UserAddException("Exception in jupyter-service: " + jupyterResp.getData());
        }

        NewUserDao userDao = userConverter.toNewUserDao(userDto);
        //添加默认租户id
        if (userDao.getTenantId() == null) {
            userDao.setTenantId(DEFAULT_TENANT_ID);
        }
        //数据库添加用户
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
    public void deleteByUsernameAndTenantId(DeleteUserReq req, String whoAmI, Integer myTenantId) {
        //获取被修改用户的 roleList
        List<Integer> roleList = userMapper.selectByUsername(req.getUsername()).getRoleList().stream().mapToInt(role -> Integer.parseInt(role.toString())).boxed().toList();
        //校验当前用户是否有权限修改
        boolean permission = roleCheck(whoAmI, roleList);
        if (!permission) {
            throw new PermissionException();
        }

        String jupyterToken = findJupyterToken(whoAmI);
        String username = req.getUsername();
        // jupyter 根据 username 删除用户, token 为当前操作人的 token
        var jupyterResp = jupyterExchange.deleteUser(username, jupyterToken);
        if (jupyterResp.getCode() != JR.SUCCESS) {
            throw new BaseBusinessException("Exception in jupyter-service: " + jupyterResp.getData());
        }
        // MYSQL 删除 user
        userMapper.deleteByUsernameAndTenantId(username, req.getTenantId());
    }

    public PageInfo<User> selectByPage(UserSelectCond cond, int pageNum, int pageSize) {
        return PageHelper.startPage(pageNum, pageSize).doSelectPageInfo(
                () -> userMapper.selectByCond(cond).stream().map(userConverterUtil::toUser).toList()
        );
    }

    @Transactional
    public String updateUserInfo(UpdateUserReq updateUserReq, String whoAmI, Integer tenantId) {
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
        //获取被修改用户的 roleList
        UserDao updateUser = userMapper.selectByUsername(updateUserReq.getUsername());
        List<Integer> updateUserRoleList = updateUser.getRoleList().stream().mapToInt(role -> Integer.parseInt(role.toString())).boxed().toList();
        //校验当前用户是否有权限修改
        boolean permission = roleCheck(whoAmI, updateUserRoleList);
        if (!permission) {
            throw new PermissionException();
        }
        //校验通过,修改用户信息
        var param = userConverter.toUpdateUserParam(updateUserReq);
        userMapper.updateUserInfo(param);
        return "update success";

    }

    @Transactional
    public void addPermission(String username, Integer tenantId, List<String> permList) {
        String jsonStr = JSONUtil.parseArray(permList).toString();
        if (tenantId == null) {
            tenantId = DEFAULT_TENANT_ID;
        }
        var insertParam = new PermissionInsertParam(username, tenantId, jsonStr);
        userMapper.appendPermission(insertParam);
        redisJackson.delete(KEY_PREFIX_PERM + username);
    }

    @Transactional
    public void deletePermission(String username, Integer tenantId, String permission) {
        if (tenantId == null) {
            tenantId = DEFAULT_TENANT_ID;
        }
        var param = new DeleteUserPermissionParam(username, tenantId, permission);
        userMapper.deletePermission(param);
        redisJackson.delete(KEY_PREFIX_PERM + username);
    }

    private String findJupyterToken(String loginId) {
        var ctx = jupyterService.findCtx(loginId);
        if (ctx == null) {
            throw new LoginException("用户的 jupyter-token 获取失败，请重新登陆");
        }
        return "token " + ctx;
    }

    private boolean roleCheck(String myUsername, List<Integer> modifiedRoleList) {

        UserDao whoAmI = userMapper.selectByUsername(myUsername);
        List<Integer> myRoleList = whoAmI.getRoleList().stream().mapToInt(role -> Integer.parseInt(role.toString())).boxed().toList();
        //双层校验
        boolean modifiedIsSuperAdmin = !modifiedRoleList.stream().filter(role -> role == 1).toList().isEmpty();
        boolean modifiedIsAdmin = !modifiedRoleList.stream().filter(role -> role == 4).toList().isEmpty();
        boolean IAmSuperAdmin = !myRoleList.stream().filter(role -> role == 1).toList().isEmpty();
        boolean IAmAdmin = !myRoleList.stream().filter(role -> role == 4).toList().isEmpty();

        if (modifiedIsSuperAdmin) {
            //若被修改用户为 super-admin , 检查当前用户是否为 super-admin
            return IAmSuperAdmin;
        } else {
            //若被修改的用户为 admin , 检查当前用户是否为 admin 或 super-admin
            if (modifiedIsAdmin) {
                return (IAmAdmin || IAmSuperAdmin);
            } else return true;
        }
    }
}
