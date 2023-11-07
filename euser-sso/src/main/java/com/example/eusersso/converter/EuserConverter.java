package com.example.eusersso.converter;


import com.example.eusersso.dao.EuserDao;
import com.example.eusersso.dto.request.NewUserDto;
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
            @Mapping(target = "token", ignore = true)
    })
    LoginResp toLoginResp(EuserDao euser);
}
