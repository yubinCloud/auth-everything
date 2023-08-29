package com.example.ssoauth.mapstruct;

import com.example.ssoauth.dao.param.NewUserDao;
import com.example.ssoauth.dao.param.UpdateUserParam;
import com.example.ssoauth.dao.result.UserDao;
import com.example.ssoauth.dao.result.UserDetailDao;
import com.example.ssoauth.dao.result.UserWithRoleDao;
import com.example.ssoauth.dto.request.NewUserDto;
import com.example.ssoauth.dto.request.UpdateUserReq;
import com.example.ssoauth.dto.response.LoginResp;
import com.example.ssoauth.dto.response.UserInfoResp;
import com.example.ssoauth.mapstructutil.UserConverterUtil;
import org.mapstruct.*;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        uses = {UserConverterUtil.class}
)
public interface UserConverter {

    @Mappings({
            @Mapping(target = "token", ignore = true)
    })
    LoginResp toLoginResp(UserDetailDao userDetail);

    @Mappings({})
    UserInfoResp toUserInfoResp(UserDetailDao userDetail);


    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(source = "password", target = "password", qualifiedByName = "passwordEncode"),
            @Mapping(source = "permissionList", target = "permissionList", qualifiedByName = "strListToJsonArray")
    })
    UserDao toUserDao(NewUserDto userDto);

    @Mappings({
            @Mapping(source = "password", target = "password", qualifiedByName = "passwordEncode"),
            @Mapping(source = "permissionList", target = "permissionList", qualifiedByName = "strListToJsonStr")
    })
    NewUserDao toNewUserDao(NewUserDto userDto);

    @Mapping(source = "permissionList", target = "permissionList", qualifiedByName = "jsonArrayToStrList")
    UserInfoResp toUserInfoResp(UserWithRoleDao userDao);

    @Mapping(source = "permissionList", target = "permissionList", qualifiedByName = "jsonArrayToStrList")
    UserDetailDao toUserDetail(UserWithRoleDao userDao);

    @Mapping(source = "password", target = "password", qualifiedByName = "passwordEncode")
    UpdateUserParam toUpdateUserParam(UpdateUserReq updateUserReq);
}
