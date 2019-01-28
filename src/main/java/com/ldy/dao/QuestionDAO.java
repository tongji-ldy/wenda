package com.ldy.dao;

import com.ldy.model.Question;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
//mybatis注解
@Mapper
public interface QuestionDAO {
    String TABLE_NAME = " question ";
    String INSERT_FIELDS = " title, content, created_date, user_id, comment_count ";//前后加空格，以免拼接时出错
    String SELECT_FIELDS = " id, " + INSERT_FIELDS;

    /**
     * question表中插入数据
     * @param question
     * @return
     */
    @Insert({"insert into", TABLE_NAME, "(", INSERT_FIELDS,
            ") values (#{title},#{content},#{createdDate},#{userId},#{commentCount})"})
    int addQuestion(Question question);

    /**
     * 首页需要显示的questions，此处使用的是xml配置而非注解，见QuestionDAO.xml
     * @param userId
     * @param offset 数据库记录开始行，offset+1
     * @param limit 数据库记录行数
     * @return
     */
    List<Question> selectLatestQuestions(@Param("userId") int userId,
                                        @Param("offset") int offset,
                                        @Param("limit") int limit);//与xml中的三个参数一一匹配
}
