package com.example.roomreservation.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.roomreservation.annotation.AdminToken;
import com.example.roomreservation.common.JsonResult;
import com.example.roomreservation.pojo.Building;
import com.example.roomreservation.service.BuildingService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/building")
public class BuildingController {
    @Resource
    private BuildingService buildingService;

    /**
     * 分页查询楼宇
     *
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @AdminToken
    @GetMapping("/page")
    public JsonResult<Page<Building>> page(int page, int pageSize, String name) {
        log.info("page = {},pageSize = {}", page, pageSize);
        // 构造分页构造器
        Page<Building> pageInfo = new Page<>(page, pageSize);
        // 构造条件构造器
        LambdaQueryWrapper<Building> queryWrapper = new LambdaQueryWrapper<>();
        // 添加过滤条件，传入name时才去查询，否则不添加name查询条件
        queryWrapper.eq(StringUtils.isNotEmpty(name), Building::getName, name);
        buildingService.page(pageInfo, queryWrapper);
        return JsonResult.success(pageInfo);
    }

    /**
     * 以列表形式返回所有楼宇
     *
     * @return
     */
    @AdminToken
    @GetMapping("/list")
    public JsonResult<List<Building>> list() {
        return JsonResult.success(buildingService.list());
    }

    /**
     * 添加楼宇
     *
     * @param building
     * @return
     */
    @AdminToken
    @PostMapping()
    public JsonResult<String> add(@RequestBody Building building) {
        if (buildingService.save(building)) {
            return JsonResult.success();
        }
        return JsonResult.error(301, "添加失败");
    }

    /**
     * 修改楼宇
     *
     * @param building
     * @return
     */
    @AdminToken
    @PutMapping()
    public JsonResult<String> update(@RequestBody Building building) {
        if (buildingService.updateById(building)) {
            return JsonResult.success();
        }
        return JsonResult.error(301, "修改失败");
    }

    /**
     * 批量修改状态
     *
     * @param type
     * @param ids
     * @return
     */
    @AdminToken
    @PostMapping("/status/{type}")
    public JsonResult<String> changeStatus(@PathVariable int type, @RequestParam List<Integer> ids) {
        log.info("type=" + type + "");
        log.info("id数据" + ids);
        if (buildingService.changeStatusBatchById(type, ids)) {
            return JsonResult.success("修改成功");
        }
        return JsonResult.error(301, "修改失败");
    }

    /**
     * 批量删除
     * todo 是否要做逻辑删除，删除联动怎么办
     *
     * @param ids
     * @return
     */
    @AdminToken
    @DeleteMapping
    public JsonResult<String> delete(@RequestParam List<Integer> ids) {
        log.info("ids:{}", ids);
        buildingService.removeBatchById(ids);
        return JsonResult.success("删除成功");
    }
}
