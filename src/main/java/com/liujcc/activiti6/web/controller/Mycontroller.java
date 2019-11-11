package com.liujcc.activiti6.web.controller;

import com.liujcc.activiti6.web.bean.User;
import com.liujcc.activiti6.web.service.UserService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.task.DelegationState;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class Mycontroller {


    @Autowired
    private UserService userService;

    @Autowired
    private ProcessEngine processEngine;


    @ResponseBody
    @RequestMapping("h1")
    public   User h1(){

        User user = new User();
        user.setId(1111);
        user.setName("zhangsan");

        return  user;
    }


    @ResponseBody
    @RequestMapping("processEngine")
    public  List<ProcessDefinition>  getprocessEngine(){
        List<ProcessDefinition> list = processEngine.getRepositoryService().createProcessDefinitionQuery().list();

        return list;
    }


    @ResponseBody
    @RequestMapping("task")
    public  Task  gettask(){
        String taskId = "92507";
        Task task = processEngine.getTaskService().createTaskQuery().taskId(taskId).singleResult();
        List<HistoricTaskInstance> list = processEngine.getHistoryService().createHistoricTaskInstanceQuery().taskId(taskId).list();
        boolean suspended = task.isSuspended();
        return task;
    }


    @ResponseBody
    @RequestMapping("activityInstanceQuery")
    public  List<HistoricTaskInstance>  activityInstanceQuery(){
        List<HistoricTaskInstance> list = processEngine.getHistoryService().
                createHistoricTaskInstanceQuery()
                .list();
        return list;
    }


    @RequestMapping(value = "get")
    @ResponseBody
    public String get() throws Exception {
        userService.delThrowEx();
        return "";
    }


    @RequestMapping(value = "get1")
    @ResponseBody
    public String get1(Model model) throws Exception {
        model.addAttribute("");
        userService.delThrowEx();
        return "";
    }
}
