package com.example.avuehelper.exception;

import com.example.avuehelper.constant.BzExceptionEnum;

public class BzExceptionFactory {

    static public BzException make(BzExceptionEnum bzExceptionEnum, String msg) {
        return new BzException(bzExceptionEnum.getCode(), msg, null);
    }
}
