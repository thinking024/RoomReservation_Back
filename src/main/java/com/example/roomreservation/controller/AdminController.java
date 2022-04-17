package com.example.roomreservation.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.roomreservation.annotation.AdminToken;
import com.example.roomreservation.annotation.PassToken;
import com.example.roomreservation.common.BaseContext;
import com.example.roomreservation.common.JsonResult;
import com.example.roomreservation.pojo.Admin;
import com.example.roomreservation.service.AdminService;
import com.example.roomreservation.util.JWTUtil;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/admin")
public class AdminController {
    @Resource
    private AdminService adminService;

    @PassToken
    @PostMapping("/login")
    public JsonResult<String> login(@RequestBody Admin admin) {
        LambdaQueryWrapper<Admin> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Admin::getAccount, admin.getAccount());
        wrapper.eq(Admin::getPassword, admin.getPassword());
        Admin one = adminService.getOne(wrapper);
        if (one == null) {
            return JsonResult.error(202, "账户或密码错误");
        }
        return JsonResult.success(JWTUtil.createToken(one.getId(), 1));
    }

    @AdminToken
    @PostMapping("/logout")
    public JsonResult<String> logout() {
        // todo 清除token
        BaseContext.removeCurrent();
        return JsonResult.success();
    }
}
