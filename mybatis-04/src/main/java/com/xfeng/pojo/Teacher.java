package com.xfeng.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @program: study-mybatis
 * @description:
 * @author: xiongfeng
 * @create: 2022-07-11 15:12
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Teacher {
    private String name;
    private int id;
    private List<Student> studentList;
}
