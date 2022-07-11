package com.xfeng.dao;

import com.xfeng.pojo.User;
import com.xfeng.utils.MyBatisUtils;
import junit.framework.TestCase;
import org.apache.ibatis.session.SqlSession;
import org.junit.Test;

import java.util.List;

public class UserMapperTest extends TestCase {
    @Test
    public void test() {
        //获取SqlSession对象
        SqlSession sqlSeesion = MyBatisUtils.getSqlSeesion();
        //方式一：getMapper
        UserMapper userMapper = sqlSeesion.getMapper(UserMapper.class);
        List<User> userList = userMapper.getUserList();
        for(User user:userList){
            System.out.println(user);
        }
//
//        User byId = userMapper.getById(1);
//        System.out.println(byId);
//        sqlSeesion.close();
    }
    @Test
    public void insertUser(){
        SqlSession sqlSeesion = MyBatisUtils.getSqlSeesion();
        UserMapper userMapper = sqlSeesion.getMapper(UserMapper.class);
//        int res = userMapper.insertUser(new User(6, "xiaoming6", "1"));
//        if(res>0){
//            System.out.println("插入成功");
//        }



        int res2 = userMapper.updateUser(new User(6, "xiaoming6", "1"));
        System.out.println(res2);

        int i = userMapper.deleteUser(6);
        System.out.println(i);
        sqlSeesion.commit();
        sqlSeesion.close();
    }
}