package com.example.roomreservation.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.roomreservation.dto.ReservationDto;
import com.example.roomreservation.pojo.Reservation;

import java.util.List;

public interface ReservationService extends IService<Reservation> {
    Page<ReservationDto> pageWithDto(int page, int pageSize, LambdaQueryWrapper<Reservation> wrapper);

    ReservationDto getByIdWithDto(Integer id);

    List<ReservationDto> getWithDto(LambdaQueryWrapper<Reservation> wrapper);
}
