package com.liujcc.activiti6;

import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.HistoryService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class Activiti6ApplicationTests {

    @Autowired
    ProcessEngine processEngine;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private TaskService taskService;

    @Test
    public void delProcessDefine() {
        processEngine.getRepositoryService()
                .deleteDeployment("22501", true);
    }

    @Test
    public void bpmnJSDeployment() {
        Deployment deployment = processEngine.getRepositoryService()
                .createDeployment()
                .addClasspathResource("processes/serviceBMPN.bpmn")
                //.addClasspathResource("processes/processVariables.bpmn")
                .name("serviceBMPN-" + new Date())
                .deploy();
        log.info("部署id=[{}]", deployment.getId());
        log.info("部署名称=[{}]", deployment.getName());

    }
    @Test
    public void taskCandidateOrAssignedDeployment() {
        Deployment deployment = processEngine.getRepositoryService()
                .createDeployment()
                .addClasspathResource("processes/taskCandidateOrAssigned.bpmn")
                //.addClasspathResource("processes/processVariables.bpmn")
                .name("taskCandidateOrAssigned-" + new Date())
                .deploy();
        log.info("部署id=[{}]", deployment.getId());
        log.info("部署名称=[{}]", deployment.getName());

    }
    @Test
    public void createProcessInstance() {
        String processDefinitionKey = "taskCandidateOrAssigned";
        HashMap<String, Object> hashMap = Maps.newHashMap();
//        hashMap.put("A", "AAA");
//        hashMap.put("B", "BBB");
//        hashMap.put("C", "CCC");
        ProcessInstance processInstance = processEngine.getRuntimeService()
                .startProcessInstanceByKey(processDefinitionKey, hashMap);

        log.info("流程实例ID:[{}]", processInstance.getId());//流程实例ID    101
        log.info("流程定义ID:[{}]", processInstance.getProcessDefinitionId());//流程定义ID   helloworld:1:4

    }

    @Test
    public void complateTask() {
        String taskId = "17505";
        taskService.complete(taskId);
        log.info("完成任务：任务ID：[{}]", taskId);

    }

    /**
     * 查询当前人的组任务
     */
    @Test
    public void findMyGroupTask() {
        String candidateUser = "zhangsan";
        List<Task> list = processEngine.getTaskService()//与正在执行的任务管理相关的Service
                .createTaskQuery()//创建任务查询对象
                /**查询条件（where部分）*/
                //.taskCandidateUser(candidateUser)//组任务的办理人查询
                //.taskAssignee("zhangsan")
                .taskCandidateOrAssigned(candidateUser)
                /**排序*/
                .orderByTaskCreateTime().asc()//使用创建时间的升序排列
                /**返回结果集*/
                .list();//返回列表
        if (list != null && list.size() > 0) {
            for (Task task : list) {
                System.out.println("任务ID:" + task.getId());
                System.out.println("任务名称:" + task.getName());
                System.out.println("任务的创建时间:" + LocalDateTime.ofInstant(
                        task.getCreateTime().toInstant(), ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                System.out.println("任务的办理人:" + task.getAssignee());
                System.out.println("流程实例ID：" + task.getProcessInstanceId());
                System.out.println("执行对象ID:" + task.getExecutionId());
                System.out.println("流程定义ID:" + task.getProcessDefinitionId());
                System.out.println("########################################################");
            }
        }
    }
}