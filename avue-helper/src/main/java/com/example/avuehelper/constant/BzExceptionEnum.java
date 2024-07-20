package com.example.avuehelper.constant;

import lombok.Getter;

@Getter
public enum BzExceptionEnum {

    DS_WORKER_ERROR(1),         // ds-worker 调用有错误
    SQL_EXEC_ERROR(2);          // SQL 执行有错误

    private final int code;

    BzExceptionEnum(int code) {
        this.code = code;
    }
}
