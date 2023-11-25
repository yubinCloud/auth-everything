package com.example.eusersso.exchange;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import java.util.Map;


/**
 * 山东通的接口，参考《山东通平台应用接入与开发指南 v1.2》
 */
@HttpExchange
public interface ShanDongTongExchange {

    @GetExchange("/cgi-bin/gettoken")
    Map<String, Object> getAccessToken(
            @RequestParam("corpid") String corpId,
            @RequestParam("corpsecret") String corpSecret
    );

    @GetExchange("/cgi-bin/user/getuserinfo")
    Map<String, Object> getUserInfo(
            @RequestParam("access_token") String accessToken,
            @RequestParam String code
    );

    @GetExchange("/cgi-bin/user/get")
    Map<String, Object> getUserDetail(
            @RequestParam("access_token") String accessToken,
            @RequestParam("userid") String userId,
            @RequestParam(value = "avatar_addr", defaultValue = "1") String avatarAddr
    );
}
