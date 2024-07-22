package com.example.avuehelper.dto.response;

import com.example.avuehelper.entity.VisualDB;
import lombok.Data;

import java.util.List;

@Data
public class PageResult<T> {

    private long total;

    private List<T> list;
}
