package com.example.afleader.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HandlerCode {

    /**
     * handler name
     */
    private String name;

    /**
     * handler code
     */
    private String code;

}
