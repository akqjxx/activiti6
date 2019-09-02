
--部署
select *from ACT_RE_DEPLOYMENT  ;
--流程资源文件
select * from ACT_GE_BYTEARRAY where DEPLOYMENT_ID_='e9fc46c5-c5a9-11e9-8ac5-507b9d81ab9d';
--流程定义
select * from ACT_RE_PROCDEF ;
--正在执行对象
select * from ACT_RU_EXECUTION;
--流程实例历史表
select * from ACT_HI_PROCINST;
select * from ACT_HI_ACTINST where PROC_INST_ID_='50001';
--正在执行的任务
select * from ACT_RU_TASK where PROC_INST_ID_='50001';
--历史任务
select * from ACT_HI_TASKINST where PROC_INST_ID_='50001';

select * from ACT_RU_IDENTITYLINK where PROC_INST_ID_='50001';
select * from ACT_HI_IDENTITYLINK where PROC_INST_ID_='50001';
--流程变量
select * from ACT_RU_VARIABLE;
--历史流程变量
select * from ACT_HI_VARINST;
--任务备注
select * from ACT_HI_COMMENT;
--
select * from ACT_RE_MODEL;

select * from ACT_EVT_LOG;
select * from ACT_HI_DETAIL;
select * from ACT_ID_USER   ;
select * from ACT_HI_DETAIL;