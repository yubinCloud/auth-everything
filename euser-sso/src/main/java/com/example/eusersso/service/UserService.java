package com.example.eusersso.service;

import com.example.eusersso.dao.EuserDao;
import com.example.eusersso.mapper.EuserMapper;
import com.example.eusersso.util.PasswordEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final EuserMapper euserMapper;

    private final PasswordEncoder passwordEncoder;

    public int insertOne(EuserDao euserDao) {
        euserDao.setPassword(passwordEncoder.encode(euserDao.getPassword()));
        euserDao.setCreateTime(System.currentTimeMillis() / 1000);  // 获取自1970年1月1日以来的秒数
        return euserMapper.insertOne(euserDao);
    }

}
