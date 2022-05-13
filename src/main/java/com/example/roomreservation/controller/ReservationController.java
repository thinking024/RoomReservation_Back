package com.example.roomreservation.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.roomreservation.annotation.AdminToken;
import com.example.roomreservation.annotation.UserToken;
import com.example.roomreservation.common.BaseContext;
import com.example.roomreservation.common.JsonResult;
import com.example.roomreservation.dto.ReservationDto;
import com.example.roomreservation.pojo.Reservation;
import com.example.roomreservation.pojo.Room;
import com.example.roomreservation.service.BuildingService;
import com.example.roomreservation.service.ReservationService;
import com.example.roomreservation.service.RoomService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/reservation")
public class ReservationController {
    @Resource
    private ReservationService reservationService;

    @Resource
    private BuildingService buildingService;

    @Resource
    private RoomService roomService;

    /**
     * 查询所有预约信息
     *
     * @param page
     * @param pageSize
     * @param id
     * @return
     */
    @AdminToken
    @GetMapping("/page")
    public JsonResult<Page<ReservationDto>> page(int page, int pageSize, Integer id,
                                                 @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime beginTime,
                                                 @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        log.info("page = {},pageSize = {}", page, pageSize);
        LambdaQueryWrapper<Reservation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(id != null, Reservation::getId, id);
        if (beginTime != null) {
            wrapper.ge(Reservation::getBeginTime, beginTime.toLocalTime());
            wrapper.ge(Reservation::getDate, beginTime.toLocalDate());
        }
        if (endTime != null) {
            wrapper.le(Reservation::getEndTime, endTime.toLocalTime());
            wrapper.le(Reservation::getDate, endTime.toLocalDate());
        }
        /*wrapper.ge(beginTime != null, Reservation::getDate, beginTime.toLocalDate());
        wrapper.ge(beginTime != null, Reservation::getBeginTime, beginTime.toLocalTime());
        wrapper.le(endTime != null, Reservation::getDate, endTime.toLocalDate());
        wrapper.le(endTime != null, Reservation::getEndTime, endTime.toLocalTime());*/
        return JsonResult.success(reservationService.pageWithDto(page, pageSize, wrapper));
    }

    /**
     * 用户分页查询自己的预定信息
     *
     * @param page
     * @param pageSize
     * @return
     */
    @UserToken
    @GetMapping("/page/me")
    public JsonResult<Page<ReservationDto>> pageByUserId(int page, int pageSize) {
        log.info("page = {},pageSize = {}", page, pageSize);
        LambdaQueryWrapper<Reservation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Reservation::getUserId, BaseContext.getCurrent().get("id"));
        return JsonResult.success(reservationService.pageWithDto(page, pageSize, wrapper));
    }

    /**
     *
     * @param buildingId
     * @param roomId
     * @param date
     * @param beginTime
     * @param endTime
     * @return
     */
    @UserToken
    @GetMapping()
    public JsonResult<List<Reservation>> listByDateAndPlace(
            Integer buildingId, Integer roomId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
            @RequestParam(required = false) @DateTimeFormat(pattern = "HH:mm:ss") LocalTime beginTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "HH:mm:ss") LocalTime endTime) {

        /*if (buildingId == null && roomId == null) {
            return JsonResult.error(501,"请求参数错误");
        }*/

        LambdaQueryWrapper<Reservation> reservationWrapper = new LambdaQueryWrapper<>();
        reservationWrapper.eq(Reservation::getStatus, 1);
        if (roomId == null) {
            // 未指定具体房间
            LambdaQueryWrapper<Room> roomWrapper = new LambdaQueryWrapper<>();
            roomWrapper.eq(Room::getBuildingId, buildingId);
            List<Room> rooms = roomService.list(roomWrapper);
            reservationWrapper.in(Reservation::getRoomId, rooms.stream().map(Room::getId).toArray());
        } else {
            reservationWrapper.eq(Reservation::getRoomId, roomId);
        }

        reservationWrapper.eq(date != null, Reservation::getDate, date);
        reservationWrapper.ge(beginTime != null, Reservation::getBeginTime, beginTime);
        reservationWrapper.le(endTime != null, Reservation::getEndTime, endTime);
        return JsonResult.success(reservationService.list(reservationWrapper));
    }

    /**
     * 按id查询预约信息
     */
    @AdminToken
    @GetMapping("/{id}")
    public JsonResult<ReservationDto> get(@PathVariable Integer id) {
        return JsonResult.success(reservationService.getByIdWithDto(id));
    }

    /**
     * 返回某个用户的所有未开始的预订记录
     *
     * @return
     */
    @UserToken
    @GetMapping("/latest")
    public JsonResult<List<ReservationDto>> getSelfReservation() {
        LambdaQueryWrapper<Reservation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Reservation::getUserId, BaseContext.getCurrent().get("id"));
        wrapper.eq(Reservation::getStatus, 1);
        return JsonResult.success(reservationService.getWithDto(wrapper));
    }

    /**
     * 取消预订
     */
    @AdminToken
    @UserToken
    @PostMapping("/cancel")
    @Transactional
    public JsonResult<String> cancel(@RequestParam List<Integer> ids) {
        log.info("id数据" + ids);
        LambdaQueryWrapper<Reservation> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Reservation::getId, ids);
        // 身份为普通用户，则只能取消自己的预订记录
        Map<String, Integer> map = BaseContext.getCurrent();
        if (map.get("type") == 0) {
            wrapper.eq(Reservation::getUserId, map.get("id"));
        }

        List<Reservation> reservations = reservationService.list(wrapper);
        for (Reservation reservation : reservations) {
            if (map.get("type") == 0 && !reservation.getUserId().equals(map.get("id"))) {
                return JsonResult.error(202, "没有权限");
            }
            reservation.setStatus(0);
        }
        if (reservationService.updateBatchById(reservations)) {
            // 身份为管理员，则发送短信通知用户
            if (map.get("type") == 1) {
                for (Reservation reservation : reservations) {
                    // todo 发送短信通知
                }
            }
            return JsonResult.success("取消成功");
        }
        return JsonResult.error(301, "取消失败");
    }

    @UserToken
    @PostMapping
    public JsonResult<String> add(@RequestBody Reservation reservation) {
        Map<String, Integer> map = BaseContext.getCurrent();
        reservation.setUserId(map.get("id"));
        reservation.setStatus(1);
        reservation.setImportant(0);
        if (reservationService.checkBeforeSave(reservation)) {
            return JsonResult.success("预订成功");
        }
        return JsonResult.error(301, "预订失败");
    }
}
