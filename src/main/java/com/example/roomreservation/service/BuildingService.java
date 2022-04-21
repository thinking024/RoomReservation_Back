package com.example.roomreservation.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.roomreservation.pojo.Building;

import java.util.List;
import java.util.Map;

public interface BuildingService extends IService<Building> {
    void removeBatchById(List<Integer> ids);

    /**
     * 根据id批量修改状态
     *
     * @param status
     * @param ids
     * @return
     */
    boolean changeStatusBatchById(int status, List<Integer> ids);

    List<Map> getNameAndId(LambdaQueryWrapper<Building> wrapper);
}
