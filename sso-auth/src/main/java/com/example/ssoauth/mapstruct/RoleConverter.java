package com.example.ssoauth.mapstruct;


import com.example.ssoauth.dao.result.RoleDao;
import com.example.ssoauth.dto.request.NewRoleDto;
import com.example.ssoauth.entity.Role;
import com.example.ssoauth.mapstructutil.RoleConverterUtil;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Mappings;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        uses = {RoleConverterUtil.class}
)
public interface RoleConverter {

    @Mappings({
            @Mapping(source = "roleId", target = "id"),
            @Mapping(source = "permissionList", target = "permissionList", qualifiedByName = "jsonArrayToStrList")
    })
    Role toRole(RoleDao roleDao);

    @Mappings({
            @Mapping(target = "roleId", ignore = true),
            @Mapping(target = "permissionList", source = "permissionList", qualifiedByName = "strListToJsonArray")
    })
    RoleDao toRoleDao(NewRoleDto newRoleDto);
}
