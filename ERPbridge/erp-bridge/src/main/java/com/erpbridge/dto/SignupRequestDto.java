package com.erpbridge.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupRequestDto {
    private String name;
    private String loginId;
    private String email;
    private String dept;
    private String password;
}