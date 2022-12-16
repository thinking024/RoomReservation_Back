package com.example.roomreservation.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.roomreservation.annotation.AdminToken;
import com.example.roomreservation.annotation.PassToken;
import com.example.roomreservation.common.JsonResult;
import com.example.roomreservation.pojo.Support;
import com.example.roomreservation.service.SupportService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/support")
public class SupportController {
    @Resource
    private SupportService supportService;

    /**
     * 分页查询
     *
     * @param page
     * @param pageSize
     * @param title
     * @return
     */
    @AdminToken
    @GetMapping("/page")
    public JsonResult<Page<Support>> page(int page, int pageSize, String title) {
        log.info("page = {},pageSize = {}", page, pageSize);
        // 构造分页构造器
        Page<Support> pageInfo = new Page<>(page, pageSize);
        // 构造条件构造器
        LambdaQueryWrapper<Support> queryWrapper = new LambdaQueryWrapper<>();
        // 添加过滤条件，传入title时才去查询，否则不添加title查询条件
        queryWrapper.eq(StringUtils.isNotEmpty(title), Support::getTitle, title);
        supportService.page(pageInfo, queryWrapper);
        return JsonResult.success(pageInfo);
    }


    /**
     * 以列表形式返回所有通告
     *
     * @return
     */
    @PassToken
    @GetMapping("/list")
    public JsonResult<List<Support>> list() {
        return JsonResult.success(supportService.list());
    }

    @AdminToken
    @GetMapping("/id/{id}")
    public JsonResult<Support> getById(@PathVariable Integer id) {
        return JsonResult.success(supportService.getById(id));
    }

    /**
     * 添加帮助
     *
     * @param support
     * @return
     */
    @AdminToken
    @PostMapping()
    public JsonResult<String> add(@RequestBody Support support) {
        if (supportService.save(support)) {
            return JsonResult.success();
        }
        return JsonResult.error(301, "添加失败");
    }

    /**
     * 批量删除帮助
     *
     * @param ids
     * @return
     */
    @AdminToken
    @DeleteMapping
    public JsonResult<String> delete(@RequestParam List<Integer> ids) {
        log.info("ids:{}", ids);
        supportService.removeByIds(ids);
        return JsonResult.success("删除成功");
    }
}

