package com.ldy.service;

import org.springframework.stereotype.Service;

@Service//IOC的实际体现之处
public class WendaService {
    public String getMessage(int userId) {
        return "hello message" + String.valueOf(userId);
    }
}
