package com.xfeng.dao;

import com.xfeng.pojo.Student;
import com.xfeng.pojo.Teacher;

import java.util.List;

/**
 * @program: study-mybatis
 * @description:
 * @author: xiongfeng
 * @create: 2022-07-11 15:14
 **/
public interface StudentMapper {
    List<Student> getStudent();
    List<Student> getStudent2();

}
