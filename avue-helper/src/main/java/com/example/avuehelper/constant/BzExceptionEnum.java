package com.example.avuehelper.constant;

import lombok.Getter;

@Getter
public enum BzExceptionEnum {

    DS_WORKER_ERROR(1);

    private final int code;

    BzExceptionEnum(int code) {
        this.code = code;
    }
}
