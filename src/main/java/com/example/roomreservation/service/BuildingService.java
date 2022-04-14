package com.example.roomreservation.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.roomreservation.pojo.Building;

import java.util.List;

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
}
