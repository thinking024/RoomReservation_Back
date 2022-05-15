package com.example.roomreservation.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.roomreservation.annotation.AdminToken;
import com.example.roomreservation.annotation.UserToken;
import com.example.roomreservation.common.JsonResult;
import com.example.roomreservation.dto.RoomDto;
import com.example.roomreservation.pojo.Reservation;
import com.example.roomreservation.pojo.Room;
import com.example.roomreservation.service.ReservationService;
import com.example.roomreservation.service.RoomService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/room")
public class RoomController {
    @Resource
    private RoomService roomService;
    @Resource
    private ReservationService reservationService;

    @AdminToken
    @GetMapping("/page")
    public JsonResult<Page<RoomDto>> page(int page, int pageSize, String buildingName) {
        log.info("page = {},pageSize = {}", page, pageSize);
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

    /**
     * 查找可预定的房间
     *
     * @param buildingId
     * @param roomId
     * @param date
     * @param beginTime
     * @param endTime
     * @return
     */
    @UserToken
    @GetMapping("/able")
    public JsonResult<List<Room>> able(
            Integer buildingId, Integer roomId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
            @RequestParam(required = false) @DateTimeFormat(pattern = "HH:mm:ss") LocalTime beginTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "HH:mm:ss") LocalTime endTime) {
        LambdaQueryWrapper<Room> roomWrapper = new LambdaQueryWrapper<>();
        roomWrapper.eq(Room::getBuildingId, buildingId);
        roomWrapper.eq(Room::getStatus, 1);
        // 未指定具体房间
        roomWrapper.eq(roomId != null, Room::getId, roomId);

        LambdaQueryWrapper<Reservation> reservationWrapper = new LambdaQueryWrapper<>();
        reservationWrapper.eq(Reservation::getStatus, 1);
        reservationWrapper.eq(date != null, Reservation::getDate, date);
        reservationWrapper.ge(beginTime != null, Reservation::getBeginTime, beginTime);
        reservationWrapper.le(endTime != null, Reservation::getEndTime, endTime);
        /*reservationService.list(reservationWrapper).forEach(reservation -> {
            roomWrapper.ne(Room::getId, reservation.getRoomId());
        });*/
        List<Reservation> reservations = reservationService.list(reservationWrapper);
        if (reservations.size() > 0) {
            roomWrapper.notIn(Room::getId, reservations.stream().map(Reservation::getRoomId).toArray());
        }
        return JsonResult.success(roomService.list(roomWrapper));
    }

    @UserToken
    @GetMapping("map/{buildingId}")
    public JsonResult<List<Map>> getNameAndIdByMap(@PathVariable Integer buildingId) {
        LambdaQueryWrapper<Room> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Room::getStatus, 1);
        queryWrapper.eq(Room::getBuildingId, buildingId);
        return JsonResult.success(roomService.getNameAndId(queryWrapper));
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
