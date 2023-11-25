package com.example.eusersso.service;

import com.example.eusersso.dao.EuserDao;
import com.example.eusersso.entity.Euser;
import com.example.eusersso.entity.UserInShanDongTong;
import com.example.eusersso.exception.ShanDongTongLoginFailedException;
import com.example.eusersso.exchange.ShanDongTongExchange;
import com.example.eusersso.repository.ShanDongTongRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShanDongTongService {

    private final ShanDongTongRepository shanDongTongRepository;

    private final ShanDongTongExchange shanDongTongExchange;

    /**
     * 使用 code 向山东通进行测试
     * @param code
     * @return
     */
    public UserInShanDongTong authFromShanDongTong(String code) {
        String accessToken = shanDongTongRepository.getAccessToken();
        var userInfoResp = shanDongTongExchange.getUserInfo(accessToken, code);
        if (!userInfoResp.get("errcode").equals(0)) {
            throw new ShanDongTongLoginFailedException("请求 `/cgi-bin/user/getuserinfo` 接口失败，响应：" + userInfoResp);
        }
        String userId = (String) userInfoResp.get("UserId");
        var userDetailResp = shanDongTongExchange.getUserDetail(accessToken, userId, "1");
        if (!userDetailResp.get("errcode").equals(0)) {
            throw new ShanDongTongLoginFailedException("请求 `/cgi-bin/user/get` 接口失败，响应：" + userDetailResp);
        }
        return UserInShanDongTong.fromUserDetailResp(userDetailResp);
    }

}
