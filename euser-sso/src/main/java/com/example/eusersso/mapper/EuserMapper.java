package com.example.eusersso.mapper;

import com.example.eusersso.dao.EuserDao;
import com.example.eusersso.dao.param.EuserSelectCond;
import com.example.eusersso.dto.request.UpdateEuserDto;
import com.example.eusersso.entity.Euser;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


@Mapper
public interface EuserMapper {

    EuserDao selectByUsername(String username);

    EuserDao selectByMobile(String mobile);

    int insertOne(EuserDao euser);

    int userTotalOfAvue();

    int userTotalOfPublicAPI();

    List<EuserDao> selectByCond(EuserSelectCond cond);

    int countByCond(EuserSelectCond cond);

    int updateUser(EuserDao update);

    int deleteByUsername(String username);
}
