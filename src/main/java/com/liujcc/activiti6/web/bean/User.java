package com.liujcc.activiti6.web.bean;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class User  implements Serializable {


    private Integer id;
    private String name;
    private List<Student> userList = new ArrayList<>();

}
