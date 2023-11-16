package com.example.eusergateway.exchange;

import com.example.eusergateway.dto.response.R;
import com.example.eusergateway.exchange.response.PersonalPermissionInfo;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange
public interface EuserSSOExchange {

    @GetExchange("/user/permission/all")
    R<PersonalPermissionInfo> getPersonalAllPermissions(
            @RequestHeader("X-Euser") String username
    );
}
