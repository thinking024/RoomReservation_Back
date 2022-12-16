package com.example.roomreservation.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.roomreservation.annotation.AdminToken;
import com.example.roomreservation.annotation.PassToken;
import com.example.roomreservation.common.JsonResult;
import com.example.roomreservation.pojo.Notice;
import com.example.roomreservation.service.NoticeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/notice")
public class NoticeController {
    @Resource
    private NoticeService noticeService;

    /**
     * 分页查询
     *
     * @param page
     * @param pageSize
     * @return
     */
    @AdminToken
    @GetMapping("/page")
    public JsonResult<Page<Notice>> page(int page, int pageSize) {
        log.info("page = {},pageSize = {}", page, pageSize);
        // 构造分页构造器
        Page<Notice> pageInfo = new Page<>(page, pageSize);
        // 构造条件构造器
        LambdaQueryWrapper<Notice> queryWrapper = new LambdaQueryWrapper<>();
        noticeService.page(pageInfo, queryWrapper);
        return JsonResult.success(pageInfo);
    }

    /**
     * 以列表形式返回所有通告
     *
     * @return
     */
    @PassToken
    @GetMapping("/list")
    public JsonResult<List<Notice>> list() {
        return JsonResult.success(noticeService.list());
    }

    @AdminToken
    @GetMapping("/id/{id}")
    public JsonResult<Notice> getById(@PathVariable Integer id) {
        Notice notice = noticeService.getById(id);
        return JsonResult.success(notice);
    }

    /**
     * 添加通告
     *
     * @param notice
     * @return
     */
    @AdminToken
    @PostMapping()
    public JsonResult<String> add(@RequestBody Notice notice) {
        if (noticeService.save(notice)) {
            return JsonResult.success();
        }
        return JsonResult.error(301, "添加失败");
    }

    @AdminToken
    @PutMapping()
    public JsonResult<String> update(@RequestBody Notice notice) {
        if (noticeService.updateById(notice)) {
            return JsonResult.success();
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
        noticeService.removeByIds(ids);
        return JsonResult.success("删除成功");
    }
}
