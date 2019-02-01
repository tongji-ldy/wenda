package com.ldy.model;

import org.springframework.stereotype.Component;

/**
 * 存的是当前登录的用户。使后台都可以通过IOC访问登录用户信息，用于拦截器中等
 */
@Component
public class HostHolder {
    private static ThreadLocal<User> users = new ThreadLocal<>();//每个线程都有一份拷贝，调用以下方法时，会根据当前线程调用该线程变量。

    public User getUser() {
        return users.get();
    }

    public void setUser(User user) {
        users.set(user);
    }

    public void clear() {
        users.remove();
    }


}















