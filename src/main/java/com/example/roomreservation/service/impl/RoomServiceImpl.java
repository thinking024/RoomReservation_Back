package com.example.roomreservation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.roomreservation.common.CustomException;
import com.example.roomreservation.dto.RoomDto;
import com.example.roomreservation.mapper.RoomMapper;
import com.example.roomreservation.pojo.Building;
import com.example.roomreservation.pojo.Reservation;
import com.example.roomreservation.pojo.Room;
import com.example.roomreservation.service.BuildingService;
import com.example.roomreservation.service.ReservationService;
import com.example.roomreservation.service.RoomService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RoomServiceImpl extends ServiceImpl<RoomMapper, Room> implements RoomService {


    @Resource
    private BuildingService buildingService;

    @Resource
    private ReservationService reservationService;

    @Override
    public Page<RoomDto> pageWithDto(int page, int pageSize) {
        Page<Room> roomPage = new Page<>(page, pageSize);
        this.page(roomPage);
        Page<RoomDto> roomDtoPage = new Page<>(page, pageSize);
        BeanUtils.copyProperties(roomPage, roomDtoPage);

        roomDtoPage.setRecords(roomPage.getRecords().stream().map(room -> {
            RoomDto roomDto = new RoomDto();
            BeanUtils.copyProperties(room, roomDto);

            LambdaQueryWrapper<Building> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Building::getId, room.getBuildingId());
            roomDto.setBuildingName(buildingService.getOne(queryWrapper).getName());
            return roomDto;
        }).collect(java.util.stream.Collectors.toList()));

        return roomDtoPage;
    }

    @Override
    public boolean addUniqueRoom(Room room) {
        Integer buildingId = room.getBuildingId();
        LambdaQueryWrapper<Room> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Room::getBuildingId, buildingId);
        queryWrapper.eq(Room::getName, room.getName());
        if (this.count(queryWrapper) != 0) {
            throw new CustomException("此建筑楼中已存在名为" + room.getName() + "的房间");
        }
        /*LambdaQueryWrapper<Building> buildingWrapper = new LambdaQueryWrapper<>();
        buildingWrapper.eq(Building::getId, buildingId);
        buildingService.getOne()
        room.setStatus();*/
        return this.save(room);
//        return false;
    }

    @Override
    public boolean updateUniqueRoom(Room room) {
        LambdaQueryWrapper<Room> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Room::getBuildingId, room.getBuildingId());
        queryWrapper.eq(Room::getName, room.getName());
        queryWrapper.ne(Room::getId, room.getId());
        if (this.count(queryWrapper) == 0) {
            return this.updateById(room);
        }
        throw new CustomException("此建筑楼中已存在名为" + room.getName() + "的房间");
//        return false;
    }

    @Transactional
    @Override
    public boolean changeStatusBatchById(int status, List<Integer> ids) {
        ArrayList<Room> list = new ArrayList<>();
        for (Integer id : ids) {
            Room room;
            if (status == 1) {
                // 启用房间，需要先检查所属建筑楼状态
                room = this.getById(id);
                LambdaQueryWrapper<Building> buildingWrapper = new LambdaQueryWrapper<>();
                buildingWrapper.eq(Building::getId, room.getBuildingId());
                Building building = buildingService.getOne(buildingWrapper);
                if (building.getStatus() == 0) {
                    log.error("此建筑楼已被禁用，无法启用房间");
                    throw new CustomException("所属建筑楼已被禁用，无法开放房间，请先启用建筑楼");
                }
                room.setStatus(1);
            } else {
                // 禁用房间，取消预约
                room = new Room();
                room.setId(id);
                room.setStatus(status);
                LambdaQueryWrapper<Reservation> reservationWrapper = new LambdaQueryWrapper<>();
                reservationWrapper.eq(Reservation::getRoomId, id);
                reservationWrapper.eq(Reservation::getStatus, 1);
                reservationService.list(reservationWrapper).forEach(reservation -> {
                    reservation.setStatus(0);
                    reservationService.updateById(reservation);
                });
            }
            list.add(room);
        }
        return this.updateBatchById(list);
    }

    @Override
    public List<Map> getNameAndId(LambdaQueryWrapper<Room> wrapper) {
        List<Map> maps = new ArrayList<>();
        this.list(wrapper).forEach(room -> {
            Map<String, Object> map = new HashMap<>();
            map.put("value", room.getId());
            map.put("text", room.getName());
            maps.add(map);
        });
        return maps;
    }

    @Override
    public boolean deleteBatchById(List<Integer> ids) {
        return false;
    }


}
