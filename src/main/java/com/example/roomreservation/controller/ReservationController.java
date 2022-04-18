package com.example.roomreservation.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.roomreservation.annotation.AdminToken;
import com.example.roomreservation.annotation.UserToken;
import com.example.roomreservation.common.BaseContext;
import com.example.roomreservation.common.JsonResult;
import com.example.roomreservation.dto.ReservationDto;
import com.example.roomreservation.pojo.Reservation;
import com.example.roomreservation.service.ReservationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/reservation")
public class ReservationController {
    @Resource
    private ReservationService reservationService;

    /**
     * 查询所有预约信息
     * todo 根据用户名查询预定情况
     *
     * @param page
     * @param pageSize
     * @param id
     * @return
     */
    @AdminToken
    @GetMapping("/page")
    public JsonResult<Page<ReservationDto>> page(int page, int pageSize, Integer id) {
        log.info("page = {},pageSize = {}", page, pageSize);
        return JsonResult.success(reservationService.pageWithDto(page, pageSize, null));
    }

    /**
     * 用户分页查询自己的预定信息
     * todo me
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
     * todo 管理员和用户都可以访问此接口
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
                    // todo 发送邮件/短信通知
                }
            }
            return JsonResult.success("取消成功");
        }
        return JsonResult.error(301, "取消失败");
    }
}
