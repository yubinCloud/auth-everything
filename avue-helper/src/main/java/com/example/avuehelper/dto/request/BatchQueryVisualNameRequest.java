package com.example.avuehelper.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class BatchQueryVisualNameRequest {

    private List<Long> ids;
}
