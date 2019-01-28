package com.ldy.service;

import org.springframework.stereotype.Service;

//IOC的实际体现之处
@Service
public class WendaService {
    public String getMessage(int userId) {
        return "hello message" + String.valueOf(userId);
    }
}
