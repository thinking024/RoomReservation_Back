package com.example.roomreservation.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.roomreservation.dto.RoomDto;
import com.example.roomreservation.pojo.Room;

import java.util.List;
import java.util.Map;

public interface RoomService extends IService<Room> {
    Page<RoomDto> pageWithDto(int page, int pageSize);

    boolean addUniqueRoom(Room room);

    boolean updateUniqueRoom(Room room);

    boolean changeStatusBatchById(int status, List<Integer> ids);

    List<Map> getNameAndId(LambdaQueryWrapper<Room> wrapper);
}
