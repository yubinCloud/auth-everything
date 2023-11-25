package com.example.eusersso.entity;

import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Data
public class UserInShanDongTong {

    private String userId;

    private String name;

    private String mobile;

    private boolean hideMobile;

    private List<Integer> department;

    public static UserInShanDongTong fromUserDetailResp(Map<String, Object> resp) {
        UserInShanDongTong user = new UserInShanDongTong();
        user.setUserId((String) resp.get("userid"));
        user.setName((String) resp.get("name"));
        user.setDepartment((List<Integer>) resp.get("department"));
        Integer hideMobile = (Integer) resp.get("hide_mobile");
        user.setHideMobile((Objects.nonNull(hideMobile)) && hideMobile.equals(1));
        return user;
    }
}
