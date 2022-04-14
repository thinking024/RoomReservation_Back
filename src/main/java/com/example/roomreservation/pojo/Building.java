package com.example.roomreservation.pojo;

import lombok.Data;

import java.io.Serializable;

@Data
public class Building implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;
    private String name;
    private Integer status;
    private String image;
    private String info;
}
