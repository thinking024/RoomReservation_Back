package com.example.roomreservation.dto;

import com.example.roomreservation.pojo.Message;
import lombok.Data;

@Data
public class MessageDto extends Message {
    private String userName;
    private String account;
    private String telephone;

    @Override
    public String toString() {
        return "MessageDto{" +
                "userName='" + userName + '\'' +
                ", account='" + account + '\'' +
                ", telephone='" + telephone + '\'' +
                "} " + super.toString();
    }
}
