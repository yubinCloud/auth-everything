package com.example.eusersso.mapper;

import com.example.eusersso.dao.EuserDao;
import com.example.eusersso.dao.param.EuserSelectCond;
import com.example.eusersso.dto.request.UpdateAddPublicAPIPermissionRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


@Mapper
public interface EuserMapper {

    EuserDao selectByUsername(String username);

    EuserDao selectByMobile(String mobile);

    int insertOne(EuserDao euser);

    int userTotalOfAvue();

    int userTotalOfPublicAPI();

    int userTotalOfUapp();

    List<EuserDao> selectByCond(EuserSelectCond cond);

    int countByCond(EuserSelectCond cond);

    int updateUser(EuserDao update);

    int deleteByUsername(String username);

    String queryAfRoutePerms(String username, Integer tenantId);

    int appendPublicAPI(String username, Integer tenantId, List<String> routes);

    int deletePublicAPI(String username, Integer tenantId, String route);

    String queryCheckedByUsernameInPublicAPI(String username);

    EuserDao selectByUsernameAndTenantId(String username, Integer tenantId);

}
