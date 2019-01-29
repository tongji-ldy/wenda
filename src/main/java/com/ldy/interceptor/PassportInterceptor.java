package com.ldy.interceptor;

import com.ldy.dao.LoginTicketDAO;
import com.ldy.dao.UserDAO;
import com.ldy.model.HostHolder;
import com.ldy.model.LoginTicket;
import com.ldy.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@Component
public class PassportInterceptor implements HandlerInterceptor {
    @Autowired
    private LoginTicketDAO loginTicketDAO;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private HostHolder hostHolder;

    /**
     * 请求处理之前的拦截，判断这个用户是谁，把用户信息放进threadlocal中
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String ticket = null;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals("ticket")) {
                    ticket = cookie.getValue();
                    break;
                }
            }
        }
        if (ticket != null) {
            LoginTicket loginTicket = loginTicketDAO.selectByTicket(ticket);  //??????为什么数据库读的数据不对？？
            if (loginTicket == null || loginTicket.getExpired().before(new Date()) || loginTicket.getStatus() != 0) {//不存在或者过期或状态不为0
                return true;
            }
            User user = userDAO.selectById(loginTicket.getUserId());
            hostHolder.setUser(user);
        }
        return true;
    }


    /**
     * 渲染之前的操作，可以使所有的渲染页面访问到这个变量
     * @param request
     * @param response
     * @param handler
     * @param modelAndView
     * @throws Exception
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        if (modelAndView != null && hostHolder.getUser() != null) {
            modelAndView.addObject("user", hostHolder.getUser());
        }
    }


    /**
     * 结束时清空信息
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        hostHolder.clear();
    }
}














