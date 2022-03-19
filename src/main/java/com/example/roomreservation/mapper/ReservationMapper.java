package com.example.roomreservation.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.roomreservation.pojo.Reservation;
import org.apache.ibatis.annotations.Mapper;
@Mapper
public interface ReservationMapper extends BaseMapper<Reservation> {
}