package com.xfeng.dao;



import com.xfeng.pojo.User;

import java.util.List;

/**
 * @program: study-mybatis
 * @description:
 * @author: xiongfeng
 * @create: 2022-07-08 23:05
 **/
public interface UserMapper {
    List<User> getUserList();

    User getById(int id);

    int insertUser(User user);

    int updateUser(User user);

    int deleteUser(int id);
}
