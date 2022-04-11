package com.example.roomreservation.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.roomreservation.annotation.AdminToken;
import com.example.roomreservation.annotation.PassToken;
import com.example.roomreservation.common.JsonResult;
import com.example.roomreservation.pojo.User;
import com.example.roomreservation.service.UserService;
import com.example.roomreservation.util.JWTUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    private UserService userService;

    @PassToken
    @PostMapping("/login")
    public JsonResult login(@RequestBody User user) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getAccount, user.getAccount());
        wrapper.eq(User::getPassword, user.getPassword());
        User one = userService.getOne(wrapper);
        if (one == null) {
            return JsonResult.error(202, "账户或密码错误");
        }
        return JsonResult.success(JWTUtil.createToken(one.getId()));
    }

    // todo 批量新增用户
    @AdminToken
    @PostMapping("/addBatch")
    public JsonResult addBatch(@RequestBody List<User> users) {
        if (userService.saveBatch(users)) {
            return JsonResult.success();
        }
        return JsonResult.error(301, "添加失败");
    }

    @AdminToken
    @PostMapping("/add")
    public JsonResult add(@RequestBody User user) {
        boolean result = userService.save(user);
        if (result) {
            return JsonResult.success();
        }
        return JsonResult.error(301, "添加失败");
    }

    @AdminToken
    @DeleteMapping()
    public JsonResult delete(@RequestParam List<Integer> ids) {
        boolean result = userService.removeByIds(ids);
        if (result) {
            return JsonResult.success();
        }
        return JsonResult.error(301, "删除失败");
    }

    @AdminToken
    @PutMapping()
    public JsonResult update(@RequestBody User user) {
        boolean result = userService.updateById(user);
        if (result) {
            return JsonResult.success();
        }
        return JsonResult.error(301, "更新失败");
    }

    @AdminToken
    @GetMapping("/page")
    public JsonResult<Page> page(int page, int pageSize, String account) {
        log.info("page = {},pageSize = {}", page, pageSize);
        // 构造分页构造器
        Page pageInfo = new Page(page, pageSize);
        // 构造条件构造器
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper();
        // 添加过滤条件，传入name时才去查询，否则不添加name查询条件
        queryWrapper.eq(StringUtils.isNotEmpty(account), User::getAccount, account);
        //添加排序条件
//        queryWrapper.orderByDesc(Employee::getUpdateTime);
        // 执行查询
        userService.page(pageInfo, queryWrapper);
        return JsonResult.success(pageInfo);
    }

    @AdminToken
    @GetMapping("/{account}")
    public JsonResult<User> getByAccount(@PathVariable String account) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(User::getAccount, account);
        User user = userService.getOne(queryWrapper);
        return JsonResult.success(user);
    }
}
