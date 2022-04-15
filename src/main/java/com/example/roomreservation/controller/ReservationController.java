package com.example.roomreservation.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.roomreservation.annotation.AdminToken;
import com.example.roomreservation.common.JsonResult;
import com.example.roomreservation.dto.ReservationDto;
import com.example.roomreservation.pojo.Reservation;
import com.example.roomreservation.service.ReservationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

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
        return JsonResult.success(reservationService.pageWithDto(page, pageSize));
    }

    /**
     * 取消预订
     */
    @AdminToken
    @PostMapping("/cancel")
    public JsonResult<String> cancel(@RequestParam List<Integer> ids) {
        log.info("id数据" + ids);
        ArrayList<Reservation> list = new ArrayList<>();
        for (Integer id : ids) {
            Reservation reservation = new Reservation();
            reservation.setId(id);
            reservation.setStatus(0);
            list.add(reservation);
        }
        if (reservationService.updateBatchById(list)) {
            for (Reservation reservation : reservationService.listByIds(ids)) {
                // todo 发送邮件/短信通知
            }

            return JsonResult.success("取消成功");
        }
        return JsonResult.error(301, "取消失败");
    }
}
