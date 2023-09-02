package com.example.ssoauth.mapstructutil;

import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.example.ssoauth.dao.result.RoleDao;
import com.example.ssoauth.dao.result.UserDao;
import com.example.ssoauth.dto.response.LoginResp;
import com.example.ssoauth.entity.User;
import com.example.ssoauth.repository.RoleLookupTable;
import com.example.ssoauth.util.PasswordEncoder;
import com.example.ssoauth.util.PermissionUtil;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserConverterUtil {

    private final PasswordEncoder passwordEncoder;

    private final RoleLookupTable roleLookupTable;

    @Named("passwordEncode")
    public String passwordEncode(String s) {
        return passwordEncoder.encode(s);
    }

    @Named("jsonArrayToStrList")
    public List<String> jsonArrayToStrList(JSONArray jsonArray) {
        return CommonConverterUtil.jsonArrayToStrList(jsonArray);
    }

    @Named("strListToJsonArray")
    public JSONArray strListToJsonArray(List<String> list) {
        return JSONUtil.parseArray(list);
    }

    @Named("strListToJsonStr")
    public String strListToJsonStr(List<String> list) {
        return JSONUtil.parseArray(list).toString();
    }

    @Named("integerListToJsonStr")
    public String integerListToJsonStr(List<Integer> list) {
        return JSONUtil.parseArray(list).toString();
    }

    /**
     * 将 UserDao 类转为 User 实体类
     * @param userDao
     * @return
     */
    public User toUser(UserDao userDao) {
        var user = new User();
        user.setId(userDao.getId());
        user.setUsername(userDao.getUsername());
        user.setPassword(userDao.getPassword());
        user.setScreenName(userDao.getScreenName());
        user.setNote(userDao.getNote());
        user.setCreateTime(userDao.getCreateTime());
        List<RoleDao> roleDaoList = roleLookupTable.translateRoleIds(
                userDao.getRoleList().toList(Integer.class)
        );
        user.setRoleList(
                roleDaoList.stream().map(RoleDao::getName).collect(Collectors.toList())
        );
        var permissionList = userDao.getPermissionList().toList(String.class);
        permissionList.addAll(roleDaoList.stream()
                .map(RoleDao::getPermissionList)
                .map(jsonArray -> jsonArray.toList(String.class))
                .flatMap(List::stream)
                .toList());
        permissionList = permissionList.stream().distinct().toList(); // 去重
        user.setPermissionList(permissionList);
        return user;
    }
}
