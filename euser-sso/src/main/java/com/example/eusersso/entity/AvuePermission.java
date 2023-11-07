package com.example.eusersso.entity;

import lombok.Data;

import java.util.List;

@Data
public class AvuePermission {

    String visualId;

    List<String> components;
}
