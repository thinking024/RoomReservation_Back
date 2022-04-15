package com.example.roomreservation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.roomreservation.dto.ReservationDto;
import com.example.roomreservation.mapper.ReservationMapper;
import com.example.roomreservation.pojo.Reservation;
import com.example.roomreservation.pojo.Room;
import com.example.roomreservation.pojo.User;
import com.example.roomreservation.service.BuildingService;
import com.example.roomreservation.service.ReservationService;
import com.example.roomreservation.service.RoomService;
import com.example.roomreservation.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class ReservationServiceImpl extends ServiceImpl<ReservationMapper, Reservation> implements ReservationService {

    @Resource
    private BuildingService buildingService;

    @Resource
    private RoomService roomService;

    @Resource
    private UserService userService;

    @Override
    public Page<ReservationDto> pageWithDto(int page, int pageSize) {
        Page<Reservation> reservationPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<Reservation> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Reservation::getDate);
        wrapper.orderByDesc(Reservation::getBeginTime);
        this.page(reservationPage);
        Page<ReservationDto> reservationDtoPage = new Page<>(page, pageSize);
        BeanUtils.copyProperties(reservationPage, reservationDtoPage);

        reservationDtoPage.setRecords(reservationPage.getRecords().stream().map(reservation -> {
            ReservationDto reservationDto = new ReservationDto();
            BeanUtils.copyProperties(reservation, reservationDto);

            // 获取建筑名、房间名
            Room room = roomService.getById(reservation.getRoomId());
            reservationDto.setRoomName(room.getName());
            reservationDto.setBuildingName(buildingService.getById(room.getBuildingId()).getName());

            // 获取用户名、账号用户名
            User user = userService.getById(reservation.getUserId());
            reservationDto.setUsername(user.getName());
            reservationDto.setAccount(user.getAccount());
            return reservationDto;
        }).collect(java.util.stream.Collectors.toList()));

        return reservationDtoPage;
    }
}