package com.example.roomreservation.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.roomreservation.mapper.UserMapper;
import com.example.roomreservation.pojo.User;
import com.example.roomreservation.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
