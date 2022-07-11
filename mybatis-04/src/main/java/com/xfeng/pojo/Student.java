package com.xfeng.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @program: study-mybatis
 * @description:
 * @author: xiongfeng
 * @create: 2022-07-11 15:10
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Student {
    private int id;
    private String name;
    private Teacher teacher;
}
