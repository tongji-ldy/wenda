package com.ldy.dao;

import com.ldy.model.Feed;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface FeedDAO {

    String TABLE_NAME = " feed ";
    String INSERT_FIELDS = " user_id, data, created_date, type ";
    String SELECT_FIELDS = " id, " + INSERT_FIELDS;

    @Insert({"insert into ", TABLE_NAME, "(", INSERT_FIELDS,
            ") values (#{userId},#{data},#{createdDate},#{type})"})
    int addFeed(Feed feed);

    /**
     * 推模式
     * @param id
     * @return
     */
    @Select({"select ", SELECT_FIELDS, " from ", TABLE_NAME, " where id=#{id}"})
    Feed getFeedById(int id);

    /**
     * 拉模式，此处用了动态sql，见feedDAO.xml文件
     * @param maxId
     * @param userIds
     * @param count
     * @return
     */
    List<Feed> selectUserFeeds(@Param("maxId") int maxId,//增量概念
                               @Param("userIds") List<Integer> userIds,
                               @Param("count") int count);


}














































