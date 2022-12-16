package com.example.roomreservation.pojo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class Message implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;
    private LocalDateTime dateTime;
    private String content;
    private Integer userId;
}

