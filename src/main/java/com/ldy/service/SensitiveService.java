package com.ldy.service;

import org.apache.commons.lang.CharUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Service
public class SensitiveService implements InitializingBean {
    private static Logger logger = LoggerFactory.getLogger(SensitiveService.class);

    private static final String DEFAULT_REPLACEMENT = "***";//敏感词替代符

    /**
     * 字典树/前缀树
     */
    private class TrieNode {
        private boolean end = false;//是否是敏感词的结尾
        private Map<Character, TrieNode> subNodes = new HashMap<>();//当前节点下的所有子节点。key:下一个字符,value:对应的节点

        /*向指定位置添加节点树*/
        void addSubNode(Character key, TrieNode node) {
            subNodes.put(key, node);
        }

        /* 获取下个节点*/
        TrieNode getSubNode(Character key) {
            return subNodes.get(key);
        }

        boolean isKeywordEnd() {
            return end;
        }

        void setKeywordEnd(boolean end) {
            this.end = end;
        }

        public int getSubNodeCount() {
            return subNodes.size();
        }
    }

    private TrieNode rootNode = new TrieNode();//根节点

    /**
     * 判断是否是一个符号，防止如“色 情”通过，或中间添加颜文字等
     */
    private boolean isSymbol(char c) {
        int ic = (int) c;
        return !CharUtils.isAsciiAlphanumeric(c) && (ic < 0x2E80 || ic > 0x9FFF);// 0x2E80-0x9FF为东亚的文字范围
    }

    /**
     * 过滤敏感词
     * @param text
     * @return
     */
    public String filter(String text) {
        if (StringUtils.isBlank(text)) {
            return text;
        }
        String replacement = DEFAULT_REPLACEMENT;
        StringBuilder result = new StringBuilder();
        TrieNode tempNode = rootNode;
        int begin = 0;  // 回滚数
        int position = 0;   //当前的额位置

        while (position < text.length()) {
            char c = text.charAt(position);
            // 如果是空格的话就直接跳过
            if (isSymbol(c)) {
                if (tempNode == rootNode) {
                    result.append(c);
                    ++begin;
                }
                ++position;
                continue;
            }
            tempNode = tempNode.getSubNode(c);
            //当前的位置匹配结束
            if (tempNode == null) {
                // 以begin开始的字符串不存在敏感词
                result.append(text.charAt(begin));
                // 调到下一个字符开始测试
                position = begin + 1;
                begin = position;
                // 回到树初始节点
                tempNode = rootNode;
            } else if (tempNode.isKeywordEnd()) {
                // 发现敏感词,从begin到position的位置用replacement替换掉
                result.append(replacement);
                position = position + 1;
                begin = position;
                tempNode = rootNode;
            } else {
                ++position;
            }
        }
        result.append(text.substring(begin));

        return result.toString();
    }

    /**
     * 将敏感词文本中的敏感词添加到字典树中
     * @param lineTxt
     */
    private void addWord(String lineTxt) {
        TrieNode tempNode = rootNode;
        // 循环两个字节
        for (int i = 0; i < lineTxt.length(); i++) {
            Character c = lineTxt.charAt(i);
            // 过滤空格
            if (isSymbol(c)) {
                continue;
            }
            TrieNode node = tempNode.getSubNode(c);
            // 没有初始化
            if (node == null) {
                node = new TrieNode();
                tempNode.addSubNode(c, node);
            }

            tempNode = node;

            if (i == lineTxt.length() - 1) {
                // 关键词结束,设置结束标志
                tempNode.setKeywordEnd(true);
            }

        }
    }

    /**
     * 读取敏感词文件
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        rootNode = new TrieNode();
        try {
            InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("SensitiveWord.txt");
            InputStreamReader reader = new InputStreamReader(is);
            BufferedReader bufferedReader = new BufferedReader(reader);
            String lineTxt;
            while ((lineTxt = bufferedReader.readLine()) != null) {
                lineTxt = lineTxt.trim();
                addWord(lineTxt);
            }
            reader.close();
        } catch (IOException e) {
            logger.error("读取敏感词文件失败" + e.getMessage());
        }
    }


   /*public static void main(String[] args) {
        SensitiveService sensitiveService = new SensitiveService();
        sensitiveService.addWord("色情");
        sensitiveService.addWord("好色");
        System.out.println(sensitiveService.filter("你好色情X色**情XX"));
    }*/

}






















