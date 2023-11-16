package com.example.eusersso.converter;

import com.example.eusersso.dto.request.NewAvueRoleDto;
import com.example.eusersso.entity.AvueRole;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Mappings;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING
)
public interface AvueRoleConverter {

    @Mappings({
            @Mapping(target = "roleId", ignore = true),
    })
    AvueRole toAvueRole(NewAvueRoleDto dto);
}
