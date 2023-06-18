package com.example.ssoauth.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class PageResp<T> {
    int pageNum;
    int pageSize;
    long total;
    List<T> list;
}
