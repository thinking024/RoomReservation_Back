package com.example.roomreservation;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@Slf4j
@EnableScheduling
@SpringBootApplication
@MapperScan("com.example.roomreservation.mapper")
public class RoomReservationApplication {
    public static void main(String[] args) {
        SpringApplication.run(RoomReservationApplication.class, args);
        log.info("start application");
    }
}
