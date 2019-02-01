package com.ldy.service;

import com.ldy.dao.MessageDAO;
import com.ldy.model.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService {
    @Autowired
    private  MessageDAO messageDAO;

    @Autowired
    private  SensitiveService sensitiveService;

    /**
     * 改变已读状态
     * @param conversationId
     */
    public void updateMessageReadStatus(String conversationId) {
        messageDAO.updateMessagesReadStatus(conversationId);
    }

    /**
     * 添加消息
     * @param message
     * @return
     */
    public int addMessage(Message message) {
        message.setContent(sensitiveService.filter(message.getContent()));//过滤
        return messageDAO.addMessage(message) > 0 ? message.getId() : 0;
    }

    /**
     * 根据某个会话id得到所有消息（发送发与接收方）
     * @param conversationId
     * @param offset
     * @param limit
     * @return
     */
    public List<Message> getConversationDetail(String conversationId, int offset, int limit) {
        return messageDAO.getConversationDetail(conversationId, offset, limit);
    }

    /**
     * 根据某个用户id得到他的所有消息
     * @param userId
     * @param offset
     * @param limit
     * @return
     */
    public List<Message> getConversationList(int userId, int offset, int limit) {
        return messageDAO.getConversationList(userId, offset, limit);
    }

    /**
     * 根据某个用户得到他未读消息数量
     * @param userId
     * @param conversationId
     * @return
     */
    public int getConversationUnreadCount(int userId, String conversationId) {
        return messageDAO.getConversationUnreadCount(userId, conversationId);
    }

}
