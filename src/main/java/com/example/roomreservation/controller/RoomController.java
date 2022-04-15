package com.example.roomreservation.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.roomreservation.annotation.AdminToken;
import com.example.roomreservation.common.JsonResult;
import com.example.roomreservation.dto.RoomDto;
import com.example.roomreservation.pojo.Room;
import com.example.roomreservation.service.RoomService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/room")
public class RoomController {
    @Resource
    private RoomService roomService;

    @AdminToken
    @GetMapping("/page")
    public JsonResult<Page<RoomDto>> page(int page, int pageSize, String buildingName) {
        log.info("page = {},pageSize = {}", page, pageSize);
        // 构造分页构造器
//        Page<Room> pageInfo = new Page<>(page, pageSize);
        // 构造条件构造器
//        LambdaQueryWrapper<Room> queryWrapper = new LambdaQueryWrapper<>();
        // 添加过滤条件，传入name时才去查询，否则不添加name查询条件
//        todo 按照楼层查询
//        queryWrapper.eq(StringUtils.isNotEmpty(buildingName), Room::getName, name);
//        roomService.page(pageInfo, queryWrapper);

        return JsonResult.success(roomService.pageWithDto(page, pageSize));
    }

    @AdminToken
    @GetMapping("/list")
    public JsonResult<List<Room>> list() {
        return JsonResult.success(roomService.list());
    }

    @AdminToken
    @GetMapping("/{id}")
    public JsonResult<Room> getById(@PathVariable Integer id) {
        log.info("id = {}", id);
        Room room = roomService.getById(id);
        return JsonResult.success(room);
    }

    @AdminToken
    @PostMapping
    public JsonResult<Room> add(@RequestBody Room room) {
        log.info("room = {}", room);
        if (roomService.addUniqueRoom(room)) {
            return JsonResult.success(room);
        }
        return JsonResult.error(301, "添加失败");
    }

    @AdminToken
    @PutMapping
    public JsonResult<Room> update(@RequestBody Room room) {
        log.info("room = {}", room);
        if (roomService.updateUniqueRoom(room)) {
            return JsonResult.success(room);
        }
        return JsonResult.error(301, "更新失败");
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
        if (roomService.changeStatusBatchById(type, ids)) {
            return JsonResult.success("修改成功");
        }
        return JsonResult.error(301, "修改失败");
    }

    /**
     * 批量删除
     * todo 是否考虑做逻辑删除，会议室删除后，预定怎么办
     *
     * @param ids
     * @return
     */
    @AdminToken
    @DeleteMapping
    public JsonResult<String> delete(@RequestParam List<Integer> ids) {
        log.info("ids:{}", ids);
        roomService.removeByIds(ids);
        return JsonResult.success("删除成功");
    }
}
