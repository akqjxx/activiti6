package com.liujcc.activiti6;

import com.google.common.collect.Maps;
import com.liujcc.activiti6.web.bean.Applay;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.HistoryService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.TaskService;
import org.activiti.engine.logging.LogMDC;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class ApplayTests {

    @Autowired
    ProcessEngine processEngine;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private TaskService taskService;

    @Test
    public void delProcessDefine() {
//          historyService
        processEngine.getRepositoryService()
                .deleteDeployment("22501", true);
    }

    /**
     * 多实例流程测试
     */
    @Test
    public void manyInstanceTest() {
        Deployment deployment = processEngine.getRepositoryService()
                .createDeployment()
                .addClasspathResource("processes/applayWork.bpmn")
                //.addClasspathResource("processes/processVariables.bpmn")
                .name("applayWork测试-" + new Date())
                .deploy();
        log.info("部署id=[{}]", deployment.getId());
        log.info("部署名称=[{}]", deployment.getName());

    }

    /**
     * 流程启动
     */
    @Test
    public void createProcessInstance() {
        LogMDC.setMDCEnabled(true);
        //String processDefinitionKey = "manyInstance";
        String processDefinitionId = "myProcess:2:67504";
        HashMap<String, Object> hashMap = Maps.newHashMap();
        Applay applay = new Applay()
                .setId(1001)
                .setName("我的申请")
                .setOpDate(new Date())
                .setOpUser("傻大木")
                .setReason("巴拉巴拉巴拉......");
        hashMap.put("applay",applay);
        ProcessInstance processInstance = processEngine.getRuntimeService()
                .startProcessInstanceById(processDefinitionId,hashMap);
        // .startProcessInstanceByKey(processDefinitionKey, hashMap);

        log.info("流程实例ID:[{}]", processInstance.getId());//流程实例ID    101
        log.info("流程定义ID:[{}]", processInstance.getProcessDefinitionId());//流程定义ID   helloworld:1:4

    }

    @Test
    public void complateTask() {
        /**
         * 92522
         * 92524
         * 92526
         * */
        String taskId = "100007";

        Map<String, Object> maps = Maps.newHashMap();
        maps.put("check","N");
        taskService.complete(taskId,maps);

        log.info("完成任务：任务ID：[{}]", taskId);

    }

    /**
     * 设置会签多人任务
     */
    @Test
    public void complateManyInstanceTask() {
        String taskId = "90002";
        Map<String, Object> maps = Maps.newHashMap();
        List<String> list = Arrays.asList("A","B","C");
        maps.put("assigneeList",list);
        taskService.complete(taskId,maps);
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
        taskService.createNativeTaskQuery().sql("");
        String taskId = "20002";
        taskService.claim(taskId, "zhangsan");
    }
}