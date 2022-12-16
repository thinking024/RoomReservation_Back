package com.example.roomreservation.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.roomreservation.mapper.SupportMapper;
import com.example.roomreservation.pojo.Support;
import com.example.roomreservation.service.SupportService;
import org.springframework.stereotype.Service;

@Service
public class SupportServiceImpl extends ServiceImpl<SupportMapper, Support> implements SupportService {
}

