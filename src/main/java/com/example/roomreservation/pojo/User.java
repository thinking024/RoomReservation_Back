package com.example.roomreservation.pojo;

import java.io.Serializable;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;
    private String account;
    private String password;
    private String name;
    private String telephone;
    private Integer count;
}
