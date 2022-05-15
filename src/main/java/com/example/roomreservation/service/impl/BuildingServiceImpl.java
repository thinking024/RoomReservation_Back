package com.example.roomreservation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.roomreservation.mapper.BuidingMapper;
import com.example.roomreservation.pojo.Building;
import com.example.roomreservation.pojo.Room;
import com.example.roomreservation.service.BuildingService;
import com.example.roomreservation.service.RoomService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BuildingServiceImpl extends ServiceImpl<BuidingMapper, Building> implements BuildingService {

    @Resource
    private RoomService roomService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeBatchById(List<Integer> ids) {
        LambdaQueryWrapper<Building> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Building::getId, ids);
        List<Building> list = this.list(queryWrapper);
        this.removeByIds(ids);
    }

    /**
     *
     * @param status
     * @param ids
     * @return
     */
    @Transactional
    @Override
    public boolean changeStatusBatchById(int status, List<Integer> ids) {
        if (ids == null || ids.size() == 0) {
            return false;
        }
        ArrayList<Building> buildings = new ArrayList<>();
        for (Integer id : ids) {
            Building building = new Building();
            building.setId(id);
            building.setStatus(status);
            buildings.add(building);
        }
        // 同时修改下属房间状态
        LambdaQueryWrapper<Room> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Room::getBuildingId, ids);
        List<Room> rooms = roomService.list(wrapper);
        for (Room room : rooms) {
            room.setStatus(status);
        }
        if (rooms != null && rooms.size() > 0) {
            roomService.updateBatchById(rooms);
            return this.updateBatchById(buildings) && roomService.updateBatchById(rooms);
        }
        return this.updateBatchById(buildings);
    }

    @Override
    public List<Map> getNameAndId(LambdaQueryWrapper<Building> wrapper) {
        List<Map> maps = new ArrayList<>();
        this.list(wrapper).forEach(building -> {
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("value", building.getId());
            hashMap.put("text", building.getName());
            maps.add(hashMap);
        });
        return maps;
    }
}
