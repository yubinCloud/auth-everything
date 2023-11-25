package com.example.eusersso.exception;


public class ShanDongTongNotEnabledException extends BaseBusinessException {

    public ShanDongTongNotEnabledException() {
        super("山东通对接未开启，不允许调用该接口");
    }
}
