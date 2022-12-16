package com.example.roomreservation.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.roomreservation.annotation.AdminToken;
import com.example.roomreservation.annotation.PassToken;
import com.example.roomreservation.annotation.UserToken;
import com.example.roomreservation.common.JsonResult;
import com.example.roomreservation.dto.MessageDto;
import com.example.roomreservation.pojo.Message;
import com.example.roomreservation.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/message")
public class MessageController {
    @Resource
    private MessageService messageService;

    /**
     * 分页查询
     *
     * @param page
     * @param pageSize
     * @return
     */
    @AdminToken
    @GetMapping("/page")
    public JsonResult<Page<MessageDto>> page(int page, int pageSize) {
        log.info("page = {},pageSize = {}", page, pageSize);
        return JsonResult.success(messageService.pageWithDto(page, pageSize));
    }

    /**
     * 以列表形式返回所有留言
     *
     * @return
     */
    @PassToken
    @GetMapping("/list")
    public JsonResult<List<Message>> list() {
        return JsonResult.success(messageService.list());
    }

    /**
     * 添加留言
     *
     * @param message
     * @return
     */
    @UserToken
    @PostMapping()
    public JsonResult<String> add(@RequestBody Message message) {
        if (messageService.save(message)) {
            return JsonResult.success();
        }
        return JsonResult.error(302, "留言失败");
    }
}
