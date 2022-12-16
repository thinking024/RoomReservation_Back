package com.example.roomreservation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.roomreservation.dto.MessageDto;
import com.example.roomreservation.mapper.MessageMapper;
import com.example.roomreservation.pojo.Message;
import com.example.roomreservation.pojo.User;
import com.example.roomreservation.service.MessageService;
import com.example.roomreservation.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements MessageService {
    @Resource
    private UserService userService;

    @Override
    public Page<MessageDto> pageWithDto(int page, int pageSize) {
        Page<Message> messagePage = new Page<>(page, pageSize);
        this.page(messagePage);
        Page<MessageDto> messageDtoPage = new Page<>(page, pageSize);
        BeanUtils.copyProperties(messagePage, messageDtoPage);

        messageDtoPage.setRecords(messagePage.getRecords().stream().map(message -> {
            MessageDto messageDto = new MessageDto();
            BeanUtils.copyProperties(message, messageDto);

            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getId, message.getUserId());
            User user = userService.getOne(queryWrapper);
            messageDto.setUserName(user.getName());
            messageDto.setAccount(user.getAccount());
            messageDto.setTelephone(user.getTelephone());
            return messageDto;
        }).collect(java.util.stream.Collectors.toList()));

        return messageDtoPage;
    }
}
