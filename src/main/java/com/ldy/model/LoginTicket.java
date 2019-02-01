package com.ldy.model;

import java.util.Date;

/**
 * 存储登录ticket信息，放在cookie中
 */
public class LoginTicket {
    private int id;
    private int userId;
    private Date expired;//过期时间
    private int status;// 0有效,1无效
    private String ticket;

    public LoginTicket() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Date getExpired() {
        return expired;
    }

    public void setExpired(Date expired) {
        this.expired = expired;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }
}





















