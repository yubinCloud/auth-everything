package com.example.ssoauth.mapstruct;

import com.example.ssoauth.dao.param.NewUserDao;
import com.example.ssoauth.dao.param.UpdateUserParam;
import com.example.ssoauth.dto.request.NewUserDto;
import com.example.ssoauth.dto.request.UpdateUserReq;
import com.example.ssoauth.dto.response.LoginResp;
import com.example.ssoauth.entity.User;
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
    LoginResp toLoginResp(User user);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(source = "password", target = "password", qualifiedByName = "passwordEncode"),
            @Mapping(source = "roleList", target = "roleList", qualifiedByName = "integerListToJsonStr"),
            @Mapping(source = "permissionList", target = "permissionList", qualifiedByName = "strListToJsonStr"),
    })
    NewUserDao toNewUserDao(NewUserDto userDto);

    @Mapping(source = "password", target = "password", qualifiedByName = "passwordEncode")
    UpdateUserParam toUpdateUserParam(UpdateUserReq updateUserReq);
}
