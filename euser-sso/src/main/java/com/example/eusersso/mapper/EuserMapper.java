package com.example.eusersso.mapper;

import com.example.eusersso.dao.EuserDao;
import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface EuserMapper {

    EuserDao selectByUsername(String username);

    int insertOne(EuserDao euser);
}
