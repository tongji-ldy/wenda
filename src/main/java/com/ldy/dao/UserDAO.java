package com.ldy.dao;

import com.ldy.model.User;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

@Repository
//mybatis注解
@Mapper
public interface UserDAO {
    String TABLE_NAME = " user ";
    String INSERT_FIELDS = " name, password, salt, head_url ";//前后加空格，以免拼接时出错
    String SELECT_FIELDS = " id, " + INSERT_FIELDS;

    @Insert({"insert into", TABLE_NAME, "(", INSERT_FIELDS,
            ") values (#{name},#{password},#{salt},#{headUrl})"})
    int addUser(User user);

    @Select({"select", SELECT_FIELDS, "from", TABLE_NAME, "where id = #{id}"})
    User selectById(int id);

    @Select({"select", SELECT_FIELDS, "from", TABLE_NAME, "where name = #{name}"})
    User selectByName(String name);

    @Update({"update", TABLE_NAME, "set password = #{password} where id = #{id}"})
    void updatePassword(User user);

    @Delete({"delete from", TABLE_NAME, "where id= #{id}"})
    void deleteById(int id);
}
