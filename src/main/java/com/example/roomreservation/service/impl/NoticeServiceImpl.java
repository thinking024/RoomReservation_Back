package com.example.roomreservation.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.roomreservation.mapper.NoticeMapper;
import com.example.roomreservation.pojo.Notice;
import com.example.roomreservation.service.NoticeService;
import org.springframework.stereotype.Service;

@Service
public class NoticeServiceImpl extends ServiceImpl<NoticeMapper, Notice> implements NoticeService {
}

