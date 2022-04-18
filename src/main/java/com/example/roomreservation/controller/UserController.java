package com.example.roomreservation.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.roomreservation.annotation.AdminToken;
import com.example.roomreservation.annotation.PassToken;
import com.example.roomreservation.annotation.UserToken;
import com.example.roomreservation.common.BaseContext;
import com.example.roomreservation.common.JsonResult;
import com.example.roomreservation.pojo.User;
import com.example.roomreservation.service.UserService;
import com.example.roomreservation.util.JWTUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    private UserService userService;

    @PassToken
    @PostMapping("/login")
    public JsonResult<String> login(@RequestBody User user) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getAccount, user.getAccount());
        wrapper.eq(User::getPassword, user.getPassword());
        User one = userService.getOne(wrapper);
        if (one == null) {
            return JsonResult.error(202, "账户或密码错误");
        }
        return JsonResult.success(JWTUtil.createToken(one.getId(), 0));
    }

    // todo 批量新增用户
    @AdminToken
    @PostMapping("/addBatch")
    public JsonResult<String> addBatch(@RequestBody List<User> users) {
        if (userService.saveBatch(users)) {
            return JsonResult.success();
        }
        return JsonResult.error(301, "添加失败");
    }

    @AdminToken
    @PostMapping()
    public JsonResult<String> add(@RequestBody User user) {
        if (userService.save(user)) {
            return JsonResult.success();
        }
        return JsonResult.error(301, "添加失败");
    }

    @AdminToken
    @DeleteMapping()
    public JsonResult<String> delete(Integer id) {
        if (userService.removeById(id)) {
            return JsonResult.success();
        }
        return JsonResult.error(301, "删除失败");
    }

    // todo 对于姓名、电话为null，并不会将其置为null，而是保留原来的值
    @UserToken
    @AdminToken
    @PutMapping()
    public JsonResult<String> update(@RequestBody User user) {
        // 用户登录，只能修改自己的信息
        Map<String, Integer> map = BaseContext.getCurrent();
        if (map.get("type").equals(0) && !user.getId().equals(map.get("id"))) {
            return JsonResult.error(202, "没有权限");
        }
        boolean result = userService.updateById(user);
        if (result) {
            return JsonResult.success();
        }
        return JsonResult.error(301, "更新失败");
    }

    @AdminToken
    @GetMapping("/page")
    public JsonResult<Page<User>> page(int page, int pageSize, String account) {
        log.info("page = {},pageSize = {}", page, pageSize);
        // 构造分页构造器
        Page<User> pageInfo = new Page<>(page, pageSize);
        // 构造条件构造器
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        // 添加过滤条件，传入name时才去查询，否则不添加name查询条件
        queryWrapper.eq(StringUtils.isNotEmpty(account), User::getAccount, account);
        //添加排序条件
//        queryWrapper.orderByDesc(Employee::getUpdateTime);
        // 执行查询
        userService.page(pageInfo, queryWrapper);
        return JsonResult.success(pageInfo);
    }

    @AdminToken
    @GetMapping("/account/{account}")
    public JsonResult<User> getByAccount(@PathVariable String account) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getAccount, account);
        User user = userService.getOne(queryWrapper);
        return JsonResult.success(user);
    }

    @AdminToken
    @GetMapping("/id/{id}")
    public JsonResult<User> getById(@PathVariable Integer id) {
        User user = userService.getById(id);
        return JsonResult.success(user);
    }

    /**
     * todo me
     *
     * @return
     */
    @UserToken
    @GetMapping("/me")
    public JsonResult<User> getSelfInfo() {
        User user = userService.getById(BaseContext.getCurrent().get("id"));
        return JsonResult.success(user);
    }
}
