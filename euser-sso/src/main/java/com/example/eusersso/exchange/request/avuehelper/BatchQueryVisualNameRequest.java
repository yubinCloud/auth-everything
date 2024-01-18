package com.example.eusersso.exchange.request.avuehelper;

import lombok.Data;

import java.util.List;

@Data
public class BatchQueryVisualNameRequest {

    private List<Long> ids;
}
