package com.ldy.service;

import com.ldy.dao.CommentDAO;
import com.ldy.model.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class CommentService {
    @Autowired
    private CommentDAO commentDAO;

    @Autowired
    private SensitiveService sensitiveService;

    public List<Comment> getCommentsByEntity(int entityId, int entityType) {
        return commentDAO.selectCommentByEntity(entityId, entityType);
    }

    public int addComment(Comment comment) {
        comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));//HTML标签过滤
        comment.setContent(sensitiveService.filter(comment.getContent()));//敏感词过滤
        return commentDAO.addComment(comment) > 0 ? comment.getId() : 0;//添加成功则返回评论的id
    }

    public int getCommentCount(int entityId, int entityType) {
        return commentDAO.getCommentCount(entityId, entityType);
    }

    public boolean deleteComment(int commentId) {
        return commentDAO.updateStatus(commentId, 1) > 0;// 状态为1则表示被逻辑删除
    }

    public Comment getCommentById(int id) {
        return commentDAO.getCommentById(id);
    }

    public int getUserCommentCount(int userId) {
        return commentDAO.getUserCommentCount(userId);
    }

}
















