package com.liujcc.activiti6.listener;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.task.Task;

import java.io.Serializable;
import java.util.List;

public class ApplayListener implements TaskListener, Serializable {

    private static final long serialVersionUID = 1L;

    @Override
    public void notify(DelegateTask delegateTask) {
        System.out.println("会签监听");
        String executionId = delegateTask.getExecutionId();
        System.out.println("获取执行对象id=" + executionId);
        ProcessEngine defaultProcessEngine = ProcessEngines.getDefaultProcessEngine();
        //获取任务service
        TaskService taskService = defaultProcessEngine.getTaskService();
        //获取运行时service
        RuntimeService runtimeService = defaultProcessEngine.getRuntimeService();
        boolean pass = (Boolean) runtimeService.getVariable(executionId, "pass");
        if (!pass) {
            //会签结束，设置参数result为N，下个任务为申请
            runtimeService.setVariable(executionId, "result", "N");
            //下个任务
            String processInstanceId = delegateTask.getProcessInstanceId();
            List<Task> list = taskService.createTaskQuery().processInstanceId(processInstanceId).list();
            //Task task = taskService.createTaskQuery().processInstanceId(processInstanceId).singleResult();
           // System.out.println("下个任务编码：" + task.getId() + "，下个任务名称：" + task.getName());
        } else {
            Integer complete = (Integer) runtimeService.getVariable(executionId, "nrOfCompletedInstances");
            Integer all = (Integer) runtimeService.getVariable(executionId, "nrOfInstances");
            //说明都完成了并且没有人拒绝
            if ((complete + 1) / all == 1) {
                runtimeService.setVariable(executionId, "result", "Y");
                //下个任务
                //String processInstanceId = delegateTask.getProcessInstanceId();
                //Task task = taskService.createTaskQuery().processInstanceId(processInstanceId).singleResult();
                //System.out.println("下个任务编码：" + task.getId() + "，下个任务名称：" + task.getName());
            }
        }

    }
}
