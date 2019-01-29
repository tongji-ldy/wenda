package com.ldy.service;

import com.ldy.dao.QuestionDAO;
import com.ldy.model.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

//IOC的实际体现之处
@Service
public class QuestionService {
    @Autowired
    QuestionDAO questionDAO;

    @Autowired
    SensitiveService sensitiveService;

    public List<Question> getLatestQuestions(int userId, int offset, int limit) {
        return questionDAO.selectLatestQuestions(userId, offset, limit);
    }

    public int updateCommentCount(int id, int count) {
        return questionDAO.updateCommentCount(id, count);
    }

    public Question getById(int id) {
        return questionDAO.getById(id);
    }

    public int addQuestion(Question question) {
        //html标签过滤
        question.setTitle(HtmlUtils.htmlEscape(question.getTitle()));
        question.setContent(HtmlUtils.htmlEscape(question.getContent()));
        //敏感词过滤
        question.setTitle(sensitiveService.filter(question.getTitle()));
        question.setContent(sensitiveService.filter(question.getContent()));
        return questionDAO.addQuestion(question) > 0 ? question.getId() : 0;//提交成功则返回question的id，否则返回0
    }
}
