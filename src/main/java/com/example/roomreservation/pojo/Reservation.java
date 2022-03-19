package com.example.roomreservation.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class Reservation implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;
    private Integer userId;
    private Integer roomId;
    private String telephone;
    private Integer status; // todo
    @TableField(value = "is_important")
    private Integer important;
    private LocalDate date;
    private LocalTime beginTime;
    private LocalTime endTime;
}
