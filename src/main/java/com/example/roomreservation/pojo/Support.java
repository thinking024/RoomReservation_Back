package com.example.roomreservation.pojo;

import lombok.Data;

import java.io.Serializable;

@Data
public class Support implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;
    private String title;
    private String content;
}
