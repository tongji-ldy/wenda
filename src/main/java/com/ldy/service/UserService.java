package com.ldy.service;

import com.ldy.dao.LoginTicketDAO;
import com.ldy.dao.UserDAO;
import com.ldy.model.LoginTicket;
import com.ldy.model.User;
import com.ldy.utils.WendaUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

//IOC的实际体现之处
@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private LoginTicketDAO loginTicketDAO;

    public Map<String, String> register(String username, String password) {
        Map<String, String> map = new HashMap<>();
        if (StringUtils.isBlank(username)) {
            map.put("msg", "用户名不能为空！");
            return map;
        }
        if (StringUtils.isBlank(password)) {
            map.put("msg", "密码不能为空！");
            return map;
        }
        User user = userDAO.selectByName(username);
        if (user != null) {
            map.put("msg", "用户名已被注册！");
            return map;
        }
        user = new User();
        user.setName(username);
        user.setSalt(UUID.randomUUID().toString().substring(0, 5));
        user.setHeadUrl(String.format("http://images.nowcoder.com/head/%dt.png",
                new Random().nextInt(1000)));
        user.setPassword(WendaUtil.MD5(password + user.getSalt()));//原始密码+盐，再经过MD5加密比较安全
        userDAO.addUser(user);

        String ticket = addLoginTicket(user.getId());
        map.put("ticket", ticket);

        return map;
    }

    public Map<String, String> login(String username, String password) {
        Map<String, String> map = new HashMap<>();
        if (StringUtils.isBlank(username)) {
            map.put("msg", "用户名不能为空！");
            return map;
        }
        if (StringUtils.isBlank(password)) {
            map.put("msg", "密码不能为空！");
            return map;
        }
        User user = userDAO.selectByName(username);
        if (user == null) {
            map.put("msg", "用户名不存在！");
            return map;
        }
        if (!WendaUtil.MD5(password + user.getSalt()).equals(user.getPassword())) {
            map.put("msg", "密码不正确");
            return map;
        }

        String ticket = addLoginTicket(user.getId());
        map.put("ticket", ticket);
        //map.put("userId", String.valueOf(user.getId()));

        return map;
    }

    /**
     * 登陆成功时，在数据库中与登录用户关联一个ticket
     *
     * @param userId
     * @return
     */
    private String addLoginTicket(int userId) {
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(userId);
        Date date = new Date();
        date.setTime(date.getTime() + 1000 * 3600 * 24);
        loginTicket.setExpired(date);
        loginTicket.setStatus(0);
        loginTicket.setTicket(UUID.randomUUID().toString().replaceAll("-", ""));
        loginTicketDAO.addTicket(loginTicket);
        return loginTicket.getTicket();
    }

    public User getUser(int id) {
        return userDAO.selectById(id);
    }

    public void logout(String ticket) {
        loginTicketDAO.updateStatus(ticket, 1);
    }

}
