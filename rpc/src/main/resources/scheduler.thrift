namespace java com.baifendian.swordfish.rpc

/* 返回结果对象 */
struct RetInfo {
	/** 返回状态码（0-成功，1-失败） */
	1: i32 status, 
	
	/** 错误信息，当有错误的情况下返回 */
   	2: string msg, 
}

/* Schedule 信息对象  */
struct ScheduleInfo {
	/** 调度的起始时间(long型)  */
	1: i64 startDate, 
	
	/** 调度的结束时间(long型) */
   	2: i64 endDate, 
   	
   	/** cron 表达式 */
   	3:string cronExpression,
}

/* 心跳汇报信息对象 */
struct HeartBeatData {
    /** 汇报时间 **/
    1: i64 reportDate,

    /** cpu使用率 **/
    2: double cpuUsed,

    /** 内存使用率 **/
    3: double memUsed,

}
	
/* Master 服务接口 */	
service MasterService {

	/**
     * 执行某个 workflow 
     * projectId : 项目 id
     * execId : 执行 id
     * flowType : workflow 类型（枚举字符串）
     */
	RetInfo execFlow(1:i32 projectId, 2:i64 execId, 3:string flowType),


	/**
     * 设置某个 workflow 的调度信息
     * projectId : 项目 id
     * flowId : workflow id
     * flowType : workflow 类型（枚举字符串）
     * scheduleInfo : schedule 详情
     */
	RetInfo setSchedule(1:i32 projectId, 2:i32 flowId, 3:string flowType, 4:ScheduleInfo scheduleInfo),
	
	/**
     * 删除某个 workflow 的调度
     * projectId : 项目 id
     * flowId : workflowId
     * flowType : workflow 类型（枚举字符串）
     */
	RetInfo deleteSchedule(1:i32 projectId, 2:i32 flowId, 3:string flowType),
	
	/**
     * 删除某个项目的所有调度
     * projectId : 项目 id
     */
	RetInfo deleteSchedules(1:i32 projectId),

    /**
     * 给一个workflow 补数据
     * projectId : 项目ID
     * flowId : 工作流ID
     * scheduleMeta: 补数据相关信息(此处不通过调度去执行)
     */
	RetInfo appendWorkFlow(1:i32 projectId, 2:i32 flowId, 3:string scheduleMeta),

    /**
     * 注册execServer
     * ip :  ip地址
     * port : 端口号
     */
	RetInfo registerExecutor(1:string ip, 2:i32 port),

    /**
     * execServer汇报心跳
     * ip :  ip地址
     * port : 端口号
     */
	RetInfo executorReport(1:string ip, 2:i32 port, 3:HeartBeatData heartBeatData),

}

/* Worker 服务接口 */	
service WorkerService {

	/**
     * 执行某个 workflow 
     * projectId : 项目 id
     * execId : 执行 id
     * flowType : workflow 类型（枚举字符串）
     */
	RetInfo execFlow(1:i32 projectId, 2:i64 execId, 3:string flowType),
	
	/**
     * 调度执行某个 workflow 
     * projectId : 项目 id
     * execId : 执行 id
     * flowType : workflow 类型（枚举字符串）
     * scheduleDate : 调度时间（预期的）
     */
	RetInfo scheduleExecFlow(1:i32 projectId, 2:i64 execId, 3:string flowType, 4:i64 scheduleDate),

	/**
     * 取消在执行的指定workflow
     * projectId : 项目 id
     * execId : 执行 id
     * flowType : workflow 类型（枚举字符串）
     */
	RetInfo cancelExecFlow(1:i32 projectId, 2:i64 execId, 3:string flowType),


	
}
