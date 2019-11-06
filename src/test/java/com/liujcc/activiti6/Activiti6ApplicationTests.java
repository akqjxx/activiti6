package com.liujcc.activiti6;

import com.google.common.collect.Maps;
import com.liujcc.activiti6.web.bean.Businesswork;
import com.liujcc.activiti6.web.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.HistoryService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricDetail;
import org.activiti.engine.impl.util.IoUtil;
import org.activiti.engine.logging.LogMDC;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private UserService userService;

    @Test
    @Transactional
    public void delProcessDefine() throws Exception {
//          historyService
//        62501
//        75001

        processEngine.getRepositoryService()
                .deleteDeployment("25001", true);

        userService.delThrowEx();
    }

    /**
     * 多实例流程测试
     */
    @Test
    public void manyInstanceTest() {
        Deployment deployment = processEngine.getRepositoryService()
                .createDeployment()
                .addClasspathResource("processes/businesswork.bpmn")
                //.addClasspathResource("processes/processVariables.bpmn")
                .name("businesswork-" + new Date())
                .deploy();
        log.info("部署id=[{}]", deployment.getId());
        log.info("部署名称=[{}]", deployment.getName());

    }

    @Test
    public void bpmnJSDeployment() {
        Deployment deployment = processEngine.getRepositoryService()
                .createDeployment()
                .addClasspathResource("processes/parallel3.bpmn")
                //.addClasspathResource("processes/processVariables.bpmn")
                .name("manyInstance-" + new Date())
                .deploy();
        log.info("部署id=[{}]", deployment.getId());
        log.info("部署名称=[{}]", deployment.getName());

    }

    @Test
    public void taskCandidateOrAssignedDeployment() {
        Deployment deployment = processEngine.getRepositoryService()
                .createDeployment()
//                .addClasspathResource("processes/taskCandidateOrAssigned.bpmn")
                .addClasspathResource("processes/call1.bpmn")
                //.addClasspathResource("processes/processVariables.bpmn")
                .name("taskCandidateOrAssigned-" + new Date())
                .deploy();
        log.info("部署id=[{}]", deployment.getId());
        log.info("部署名称=[{}]", deployment.getName());

    }

    @Test
    public void createProcessInstance() {
        LogMDC.setMDCEnabled(true);
        String processDefinitionKey = "parallel";
        HashMap<String, Object> hashMap = Maps.newHashMap();
        Businesswork businesswork = new Businesswork();
        businesswork.setName("我的工单");
        //businesswork.setMsg("yes");
//
        hashMap.put("businesswork", businesswork);
//        hashMap.put("B", "BBB");
//        hashMap.put("C", "CCC");
        ProcessInstance processInstance = processEngine.getRuntimeService()
                .startProcessInstanceByKey(processDefinitionKey, hashMap);

        log.info("流程实例ID:[{}]", processInstance.getId());//流程实例ID    101
        log.info("流程定义ID:[{}]", processInstance.getProcessDefinitionId());//流程定义ID   helloworld:1:4

    }

    @Test
    public void complateTask() {
        String taskId = "90010";
        taskService.complete(taskId);
        log.info("完成任务：任务ID：[{}]", taskId);

    }


    @Test
    public void complateTaskPall() {
        String taskId = "65010";
        Businesswork businesswork = (Businesswork) taskService.getVariable(taskId, "businesswork");

        businesswork.setMsg("yes");
        HashMap<String, Object> objectObjectHashMap = Maps.newHashMap();
        objectObjectHashMap.put("businesswork", businesswork);

        taskService.complete(taskId, objectObjectHashMap);
        log.info("完成任务：任务ID：[{}]", taskId);

    }


    /**
     * 查询当前人的组任务
     */
    @Test
    public void findMyGroupTask() {
        String candidateUser = "B";
        List<Task> list = processEngine.getTaskService()//与正在执行的任务管理相关的Service
                .createTaskQuery()//创建任务查询对象
                /**查询条件（where部分）*/
                //.taskCandidateUser(candidateUser)//组任务的办理人查询
                //.taskAssignee("zhangsan")
                .taskCandidateOrAssigned(candidateUser)
                //task.getAssignee()返回值如果为null表示是组任务，
                // 需要taskService.claim(taskid,userid);拾取任务后才能提交
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

    /**
     * 拾取任务
     */
    @Test
    public void claimTask() {
        //taskService.createNativeTaskQuery().sql("");
        String taskId = "15005";
        taskService.claim(taskId, "李四");
    }

    /**
     * 查询最新版本的流程定义列表
     */
    @Test
    public void findLastList() {
        ProcessDefinitionQuery query = processEngine.getRepositoryService()
                .createProcessDefinitionQuery()
                .processDefinitionKey("businesswork")
                .orderByProcessDefinitionVersion()
                .asc();
        List<ProcessDefinition> list = query.list();

        Map<String, ProcessDefinition> map = new HashMap<String, ProcessDefinition>();
        for (ProcessDefinition pd : list) {
            map.put(pd.getKey(), pd);
        }
        System.out.println(map.values());
    }


    /**
     * 根据流程定义id获得png图片的输入流
     */
    @Test
    public void findPngStream() throws IOException {
        String pdId = "businesswork:2:12504";
        InputStream processDiagram = processEngine.getRepositoryService().getProcessDiagram(pdId);
        OutputStream outputStream = new FileOutputStream("D:/businesswork.png");
        IOUtils.copy(processDiagram,outputStream);

    }
}