package com.ldy.controller;

import com.ldy.model.EntityType;
import com.ldy.model.Question;
import com.ldy.model.ViewObject;
import com.ldy.service.FollowService;
import com.ldy.service.QuestionService;
import com.ldy.service.SearchService;
import com.ldy.service.UserService;
import org.apache.solr.client.solrj.SolrServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
public class SearchController {
    private static Logger logger = LoggerFactory.getLogger(SearchController.class);

    @Autowired
    private  SearchService searchService;

    @Autowired
    private  FollowService followService;

    @Autowired
    private  UserService userService;

    @Autowired
    private  QuestionService questionService;

    @RequestMapping(path = {"/search"}, method = {RequestMethod.POST, RequestMethod.GET})
    public String search(Model model, @RequestParam("q") String keyword,
                         @RequestParam(value = "offset", defaultValue = "0") int offset,
                         @RequestParam(value = "count", defaultValue = "10") int count) {
        try {
            List<Question> questionList = searchService.searchQuestion(keyword, offset, count, "<em>", "</em>");

            List<ViewObject> vos = new ArrayList<>();
            for (Question question : questionList) {
                Question q = questionService.getById(question.getId());
                ViewObject vo = new ViewObject();
                if (question.getContent() != null) {
                    q.setContent(question.getContent());
                }
                if (question.getTitle() != null) {
                    q.setTitle(question.getTitle());
                }
                vo.set("question", q);
                vo.set("followCount", followService.getFollowerCount(EntityType.ENTITY_QUESTION, question.getId()));
                vo.set("user", userService.getUser(q.getUserId()));
                vos.add(vo);
            }
            model.addAttribute("vos", vos);
            model.addAttribute("keyword", keyword);
        } catch (IOException e) {
            logger.error("传输流出错", e.getMessage());
        } catch (SolrServerException e) {
            logger.error("solr服务失败", e.getMessage());
        }
        return "result";
    }


}


























