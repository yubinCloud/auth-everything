package com.example.dsworker.mapstruct;

import com.example.dsworker.dto.request.DataSourceConf;
import com.zaxxer.hikari.HikariConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Mappings;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING
)
public interface DataSourceConfConverter {

    @Mappings({
            @Mapping(target = "driverClassName", source = "driverClass"),
            @Mapping(target = "jdbcUrl", source = "url")
    })
    HikariConfig toHikariConf(DataSourceConf dataSourceProp);
}
