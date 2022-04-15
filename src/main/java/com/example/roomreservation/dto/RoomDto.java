package com.example.roomreservation.dto;

import com.example.roomreservation.pojo.Room;
import lombok.Data;

@Data
public class RoomDto extends Room {
    private String buildingName;

    @Override
    public String toString() {
        return "RoomDto{" +
                "buildingName='" + buildingName + '\'' +
                "} " + super.toString();
    }
}
