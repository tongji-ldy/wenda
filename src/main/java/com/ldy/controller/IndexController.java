package com.ldy.controller;

import com.ldy.model.User;
import com.ldy.service.WendaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//@Controller
public class IndexController {

    @Autowired//IOC的实际体现之处
    WendaService wendaService;

    @RequestMapping(path = {"/", "/index"})//指定了两个路径映射
    @ResponseBody//表示返回的是字符串，而不是模板名
    public String index(HttpSession session) {
        return wendaService.getMessage(1) + " hello wenda! " + session.getAttribute("msg");
    }

    @RequestMapping(path = {"/profile/{groupName}/{userId}"}, method = {RequestMethod.GET})//method指定了请求的方式
    @ResponseBody
    public String profile(@PathVariable("groupName") String groupName,//将路径中的参数解析到变量中
                          @PathVariable("userId") int userId,
                          @RequestParam(value = "type", defaultValue = "1") int type,
                          @RequestParam(value = "key", defaultValue = "zz", required = false) String key) {//将请求参数解析到变量中，如果请求时没有此参数，则使用默认值,还可以设置默认是否需要此参数
        return String.format("Profile Page of %s / %d, t:%d k:%s", groupName, userId, type, key);
    }

    @RequestMapping(path = {"/ftl"}, method = {RequestMethod.GET})
    public String template(Model model) {
        model.addAttribute("value1", "v1");//给前台需要显示的一个变量赋值

        List<String> colors = Arrays.asList(new String[]{"RED", "GREEN", "BLUE"});
        model.addAttribute("colors", colors);

        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < 4; i++) {
            map.put(String.valueOf(i), String.valueOf(i * i));
        }
        model.addAttribute("map", map);

        model.addAttribute("user", new User("Lee"));

        return "home";//去掉了responseBody表示返回的是一个模板，而不是一个字符串。
    }

    @RequestMapping(path = {"/request"})
    @ResponseBody
    public String request(HttpServletRequest request,
                          HttpServletResponse response) {
        StringBuilder sb = new StringBuilder();
        sb.append(request.getCookies());
        sb.append(request.getHeaderNames());
        sb.append(request.getMethod());
        response.setHeader("haha", "lala");
        return sb.toString();
    }

    /**
     * 301跳转和302跳转
     *
     * @param code
     * @param session
     * @return
     */
    @RequestMapping(path = {"/redirect/{code}"})
    public RedirectView redirect(@PathVariable("code") int code,
                                 HttpSession session) {
        session.setAttribute("msg", "lalala");
        RedirectView red = new RedirectView("/", true);
        if (code == 301) {
            red.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
        }
        return red;
    }

    @RequestMapping(path = {"/admin"})
    @ResponseBody
    public String admin(@RequestParam("key") String key) {
        if ("admin".equals(key)) {
            return "hello admin!";
        } else throw new IllegalArgumentException("参数不对");
    }

    /**
     * 显示错误
     *
     * @param e
     * @return
     */
    @ExceptionHandler//用于接收错误显示错误页面
    @ResponseBody
    public String error(Exception e) {
        return "error: " + e.getMessage();
    }
}
