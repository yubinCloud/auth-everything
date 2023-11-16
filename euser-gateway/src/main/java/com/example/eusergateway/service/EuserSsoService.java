package com.example.eusergateway.service;

import com.example.eusergateway.exchange.EuserSSOExchange;
import com.example.eusergateway.exchange.response.PersonalPermissionInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.example.eusergateway.dto.response.R;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@RequiredArgsConstructor
public class EuserSsoService {

    private final EuserSSOExchange euserSSOExchange;

    private final ExecutorService executorService = Executors.newFixedThreadPool(2);

    public PersonalPermissionInfo getPersonalPermissionInfo(String username) {
        var future = executorService.submit(() -> euserSSOExchange.getPersonalAllPermissions(username));
        R<PersonalPermissionInfo> resp;
        try {
            resp = future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("用户信息获取失败");
        }
        return resp.getData();
    }

}
