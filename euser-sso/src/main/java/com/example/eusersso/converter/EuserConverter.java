package com.example.eusersso.converter;


import com.example.eusersso.dao.EuserDao;
import com.example.eusersso.dto.request.NewUserDto;
import com.example.eusersso.dto.request.UpdateEuserDto;
import com.example.eusersso.dto.response.EuserListItem;
import com.example.eusersso.dto.response.LoginResp;
import com.example.eusersso.entity.Euser;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Mappings;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING
)
public interface EuserConverter {

    @Mappings({
            @Mapping(target = "avueAccessList", ignore = true),
            @Mapping(target = "apiAccessList", ignore = true)
    })
    Euser toEntity(EuserDao euserDao);

    @Mappings({
            @Mapping(target = "createTime", ignore = true),
            @Mapping(target = "createdBy", ignore = true),
            @Mapping(target = "labels", ignore = true),
            @Mapping(target = "uid", ignore = true),
            @Mapping(target = "publicApiIds", ignore = true),
            @Mapping(target = "lastUpdatedIuser", ignore = true),
            @Mapping(target = "lastUpdatedTime", ignore = true)
    })
    EuserDao toEuserDao(NewUserDto newUserDto);

    @Mapping(target = "uid", ignore = true)
    @Mapping(target = "lastUpdatedTime", ignore = true)
    @Mapping(target = "lastUpdatedIuser", ignore = true)
    @Mapping(target = "labels", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    EuserDao toEuserDao(UpdateEuserDto updateEuserDto);

    @Mappings({
            @Mapping(target = "token", ignore = true)
    })
    LoginResp toLoginResp(EuserDao euser);


    @Mappings({
            @Mapping(target = "avueRoles", ignore = true),
    })
    EuserListItem toEuserListItem(EuserDao euser);
}
