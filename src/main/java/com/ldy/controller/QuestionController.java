package com.ldy.controller;

import com.ldy.model.HostHolder;
import com.ldy.model.Question;
import com.ldy.service.QuestionService;
import com.ldy.service.UserService;
import com.ldy.utils.WendaUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

import static com.ldy.utils.WendaUtil.ANONYMOUS_USERID;

@Controller
public class QuestionController {
    private static Logger logger = LoggerFactory.getLogger(QuestionController.class);

    @Autowired
    HostHolder hostHolder;

    @Autowired
    QuestionService questionService;

    @Autowired
    UserService userService;

    @RequestMapping(value = "/question/add", method = {RequestMethod.POST})
    @ResponseBody
    public String addQuestion(@RequestParam("title") String title,
                              @RequestParam("content") String content) {
        try {
            Question question = new Question();
            question.setContent(content);
            question.setCreatedDate(new Date());
            question.setCommentCount(0);
            question.setTitle(title);
            if (hostHolder.getUser() == null) {
                // 这里可以设置一个匿名用户,见wendautil
                question.setUserId(ANONYMOUS_USERID );
                //return WendaUtil.getJSONString(999);
            } else {
                question.setUserId(hostHolder.getUser().getId());
            }
            if (questionService.addQuestion(question) > 0) {
                return WendaUtil.getJSONString(0);
            }
        } catch (Exception e) {
            logger.error("增加题目失败" + e.getMessage());
        }
        return WendaUtil.getJSONString(1, "失败");
    }

    @RequestMapping(value = "/question/{qid}", method = {RequestMethod.GET})
    public String questionDetail(Model model, @PathVariable("qid") int qid) {

        Question question = questionService.getById(qid);
        model.addAttribute("question", question);
        model.addAttribute("user", userService.getUser(question.getUserId()));
        return "detail";
    }
}


















