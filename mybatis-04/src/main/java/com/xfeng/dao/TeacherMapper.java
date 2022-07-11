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
public interface TeacherMapper {
    List<Teacher> getTeacherById();
    List<Teacher> getTeacherById1();
    List<Teacher> getTeacherById2();
    List<Student>getStudentByTeacherId();
}
