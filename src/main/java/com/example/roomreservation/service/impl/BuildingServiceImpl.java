package com.example.roomreservation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.roomreservation.mapper.BuidingMapper;
import com.example.roomreservation.pojo.Building;
import com.example.roomreservation.pojo.Room;
import com.example.roomreservation.service.BuildingService;
import com.example.roomreservation.service.RoomService;
import com.example.roomreservation.util.FileUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class BuildingServiceImpl extends ServiceImpl<BuidingMapper, Building> implements BuildingService {

    @Resource
    private RoomService roomService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeBatchById(List<Integer> ids) {
        // todo 建筑楼与房间之间删除的联动

        /*int count = this.count(queryWrapper);
        if(count > 0){
            //如果不能删除，抛出一个业务异常
            throw new CustomException("套餐正在售卖中，不能删除");
        }*/

        // 删除图片
        LambdaQueryWrapper<Building> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Building::getId, ids);
        List<Building> list = this.list(queryWrapper);
        for (Building building : list) {
            FileUtil.deleteFile(building.getImage());
        }
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
        return this.updateBatchById(buildings) && roomService.updateBatchById(rooms);
    }
}
