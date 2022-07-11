package com.xfeng.dao;

import com.xfeng.pojo.Student;
import com.xfeng.pojo.Teacher;
import com.xfeng.utils.MyBatisUtils;
import junit.framework.TestCase;
import org.apache.ibatis.session.SqlSession;
import org.junit.Test;

import java.util.List;

public class StudentMapperTest extends TestCase {
    @Test
    public void testGetStudent(){
        SqlSession sqlSeesion = MyBatisUtils.getSqlSeesion();
        //一对多 一个老师对应多个学生查询
        TeacherMapper teacherMapper = sqlSeesion.getMapper(TeacherMapper.class);
        List<Teacher> teacher = teacherMapper.getTeacherById1();
        for(Teacher t : teacher){
            System.out.println(t);
        }
        //多对一  一个学生对应多个老师查询
//        StudentMapper studentMapper = sqlSeesion.getMapper(StudentMapper.class);
//        List<Student> student = studentMapper.getStudent();
//        for(Student t : student){
//            System.out.println(t);
//        }

    }
}