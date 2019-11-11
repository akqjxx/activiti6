package com.liujcc.activiti6;

import com.google.common.collect.Maps;
import com.liujcc.activiti6.web.bean.Businesswork;
import com.liujcc.activiti6.web.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.GraphicInfo;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.*;
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
import org.springframework.core.Conventions;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

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


    @Autowired
    private RepositoryService repositoryService;

    @Test
    @Transactional
    public void delProcessDefine() throws Exception {
//          historyService
//        62501
//        75001

        taskService.setAssignee("2", "2");


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
        String taskId = "92507";
        taskService.claim(taskId, "zhangsan");

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
        IOUtils.copy(processDiagram, outputStream);

    }


    /**
     * 查询当前人的组任务
     */
    @Test
    public void findMyComplainWork() {

        Businesswork abc = new Businesswork();
        // String name = Conventions.getVariableName(abc);
        String candidateUser = "zhangsan";
        List<Task> list = processEngine.getTaskService()//与正在执行的任务管理相关的Service
                .createTaskQuery()//创建任务查询对象
                /**查询条件（where部分）*/
                //.taskCandidateUser(candidateUser)//组任务的办理人查询
                //.taskAssignee("zhangsan")
                .taskCandidateOrAssigned(candidateUser)
                //.taskVariableValueEquals(name)

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


    @Test
    public void t1() {
        Businesswork abc = new Businesswork();
        String name = Conventions.getVariableName(abc);
        System.out.println(name);
    }


    @Test
    public void t2() {

        String taskId = "92504";
        //返回存放连线的名称集合
        List<String> list = new ArrayList<>();
        //1:使用任务ID，查询任务对象
        Task task = taskService.createTaskQuery()//
                .taskId(taskId)//使用任务ID查询
                .singleResult();

        //获取流程定义
        ProcessDefinition processDefinition = repositoryService.getProcessDefinition(task.getProcessDefinitionId());
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinition.getId());

        //可以获取当前任务坐标信息
//        GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(task.getTaskDefinitionKey());
        org.activiti.bpmn.model.Process process = bpmnModel.getMainProcess();
        //Process process = bpmnModel.getProcesses().get(0);

        UserTask flowElement = (UserTask) process.getFlowElement(task.getTaskDefinitionKey());
        //获取当前任务图连线 outgoing ,incoming
        List<SequenceFlow> outgoingFlows = flowElement.getOutgoingFlows();
        outgoingFlows.stream().forEach(flows -> {
            System.out.println(flows.getName());
            System.out.println(flows.getId());
        });

//        HashMap<String, Object> objectObjectHashMap = Maps.newHashMap();
//        objectObjectHashMap.put("graphicInfo",graphicInfo);
//        objectObjectHashMap.put("flowElement",flowElement);
//


    }


    /**
     * 个人任务转成组任务
     */
    @Test
    public void t5() {
        String taskId = "92507";
        taskService.setAssignee(taskId, null);
        taskService.addCandidateUser(taskId, "zhangsan");
        taskService.addCandidateUser(taskId, "lisi");

    }


    /*
    * 挂起
    * */
    @Test
    public void t6() {
        String taskId = "92507";
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        String processInstanceId = task.getProcessInstanceId();
        runtimeService.suspendProcessInstanceById(processInstanceId);


    }
}