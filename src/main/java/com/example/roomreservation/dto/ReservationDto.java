package com.example.roomreservation.dto;

import com.example.roomreservation.pojo.Reservation;
import lombok.Data;

@Data
public class ReservationDto extends Reservation {
    private String account;
    private String username;
    private String buildingName;
    private String roomName;

    @Override
    public String toString() {
        return "ReservationDto{" +
                "account='" + account + '\'' +
                ",username='" + username + '\'' +
                ", buildingName='" + buildingName + '\'' +
                ", roomName='" + roomName + '\'' +
                '}' + super.toString();
    }
}
