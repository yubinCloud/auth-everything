package com.example.eusersso.dao.param;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EuserSelectCond {

    private String username;

    private String screenName;

    private String mobile;

    private Integer avueRoleId;

    private String apiId;

    private String dbAccessLabel;

    private Integer pageNum;

    private Integer pageSize;
}
