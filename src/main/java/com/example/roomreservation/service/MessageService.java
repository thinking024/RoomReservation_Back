package com.example.roomreservation.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.roomreservation.dto.MessageDto;
import com.example.roomreservation.pojo.Message;
import org.springframework.stereotype.Service;

@Service
public interface MessageService extends IService<Message> {
    Page<MessageDto> pageWithDto(int page, int pageSize);
}

