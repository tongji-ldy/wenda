package com.ldy.controller;

import com.ldy.model.Question;
import com.ldy.model.ViewObject;
import com.ldy.service.QuestionService;
import com.ldy.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.List;

/**
 * 首页控制器
 */
@Controller
public class HomeController {
    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private QuestionService questionService;

    /**
     * 从首页question中跳转到相关user页面
     *
     * @param model
     * @param userId
     * @return
     */
    @RequestMapping(path = {"/user/{userId}"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String userIndex(Model model, @PathVariable("userId") int userId) {
        model.addAttribute("vos", getQuestions(userId, 0, 10));
        return "index";
    }

    /**
     * 首页question显示
     *
     * @param model
     * @return
     */
    @RequestMapping(path = {"/", "/index"}, method = {RequestMethod.GET, RequestMethod.POST})//指定了两个路径映射
    public String index(Model model) {
        model.addAttribute("vos", getQuestions(0, 0, 10));
        return "index";
    }

    /**
     * 从userIndex与index中抽取出来的方法，将每个question与对应的user封装到vos中
     *
     * @param userId
     * @param offset
     * @param limit
     * @return
     */
    private List<ViewObject> getQuestions(int userId, int offset, int limit) {
        List<Question> questionList = questionService.getLatestQuestions(userId, offset, limit);
        List<ViewObject> vos = new ArrayList<>();
        for (Question question : questionList) {
            ViewObject vo = new ViewObject();
            vo.set("question", question);
            vo.set("user", userService.getUser(question.getUserId()));
            vos.add(vo);
        }
        return vos;
    }
}
