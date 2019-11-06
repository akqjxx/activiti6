package com.liujcc.activiti6.web.controller;

import com.liujcc.activiti6.web.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class Mycontroller {


    @Autowired
    private UserService userService;

    @RequestMapping(value = "get")
    @ResponseBody
    public String get() throws Exception {
        userService.delThrowEx();
        return "";
    }

}
