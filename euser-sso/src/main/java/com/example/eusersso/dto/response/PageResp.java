package com.example.eusersso.dto.response;

import com.github.pagehelper.PageInfo;
import lombok.Data;

import java.util.List;

@Data
public class PageResp<T> {
    int pageNum;
    int pageSize;
    long total;
    List<T> list;

    public static <U> PageResp<U> of(PageInfo<U> pageInfo) {
        PageResp<U> pageResp = new PageResp<>();
        pageResp.setList(pageInfo.getList());
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setPageNum(pageInfo.getPageNum());
        pageResp.setPageSize(pageInfo.getPageSize());
        return pageResp;
    }
}
