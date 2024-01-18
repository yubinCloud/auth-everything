package com.example.eusersso.mapper;

import com.example.eusersso.dao.param.AvueRoleSelectCond;
import com.example.eusersso.entity.AvueRole;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AvueRoleMapper {

    List<AvueRole> selectBatchById(List<Integer> roleIds);

    List<String> selectBatchSysHomes(List<Integer> roleIds);

    void insertOne(AvueRole role);

    int updateOne(AvueRole role);

    List<AvueRole> selectByCond(AvueRoleSelectCond cond);

    int deleteById(int roleId);
}
