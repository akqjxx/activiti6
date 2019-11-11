package com.liujcc.activiti6;

import com.liujcc.activiti6.web.bean.Businesswork;
import org.springframework.core.Conventions;

public class T1 {
    public static void main(String[] args) {
        Businesswork abc = new Businesswork();
        String name = Conventions.getVariableName(abc);
        System.out.println(name);
    }
}
