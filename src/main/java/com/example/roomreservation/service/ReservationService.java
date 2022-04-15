package com.example.roomreservation.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.roomreservation.dto.ReservationDto;
import com.example.roomreservation.pojo.Reservation;

public interface ReservationService extends IService<Reservation> {
    Page<ReservationDto> pageWithDto(int page, int pageSize);
}
