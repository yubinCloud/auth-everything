package com.example.afleader.enums;

import lombok.Getter;

@Getter
public enum DynamicRouteTypeEnum {


    SQL_TYPE(1), CUSTOM_TYPE(2), HTTP_TYPE(3);

    private final int rtCode;

    DynamicRouteTypeEnum(int rtCode) {
        this.rtCode = rtCode;
    }

}
