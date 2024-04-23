package com.example.ssoauth.util;

import com.alibaba.cloud.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class FuzzyQueryUtil {
    public String concat(String queryParam){
        if (StringUtils.isBlank(queryParam)) {
            queryParam = null;
        } else {
            queryParam = "%" + queryParam + "%";
        }
        return queryParam;
    }
}
