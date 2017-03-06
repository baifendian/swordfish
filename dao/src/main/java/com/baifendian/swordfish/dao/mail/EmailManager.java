/*
 * Create Author  : dsfan
 * Create Date    : 2016年12月7日
 * File Name      : EmailManager.java
 */

package com.baifendian.swordfish.dao.mail;

import com.baifendian.swordfish.common.utils.BFDDateUtils;
import com.baifendian.swordfish.dao.DaoFactory;
import com.baifendian.swordfish.dao.mysql.enums.FlowRunType;
import com.baifendian.swordfish.common.job.FlowStatus;
import com.baifendian.swordfish.dao.mysql.enums.FlowType;
import com.baifendian.swordfish.dao.mysql.model.ExecutionFlow;
import com.baifendian.swordfish.dao.mysql.model.ProjectFlow;

import java.text.MessageFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 邮件内容管理
 * <p>
 * 
 * @author : dsfan
 * @date : 2016年12月7日
 */
public class EmailManager {

    /** {@link MailSendService} */
    private static MailSendService mailSendService;

    static {
        mailSendService = DaoFactory.getDaoInstance(MailSendService.class);
    }

    /** 邮件标题格式 */
    private static final String TITLE_FORMAT = "【调度系统】【{0}】【{1}】";

    /** 获取邮件任务 */
    private static final String CONTENT_FORMAT = "<b>{0}</b><hr/>项目：{1}<br/>工作流名称：{2}<br/>工作流类型：{3}<br/>调度时间：{4}<br/>执行结果：{5}<br/><br/><I>备注：详细执行情况见【运维中心】-【调度日志】</I>";

    /** 邮件内容的结尾 */
    private static final String TAIL_FORMAT = "<br/><br/><I>备注：详细执行情况见【运维中心】-【调度日志】</I>";

    /** 补数据内容头部 */
    private static final String ADD_DATA_HEAD_FORMAT = "<b>{0}</b><hr/>项目：{1}<br/>工作流名称：{2}<br/>工作流类型：{3}<br/><br/><b>补数据详情</b>";

    /** 补数据的每个元素内容 */
    private static final String ADD_DATA_ITEM_FORMAT = "<hr style=\"border:1px dotted #036\" />调度时间：{0}<br/>执行结果：{1}";

    /** 补数据的结尾 */
    private static final String ADD_DATA_TAIL_FORMAT = "<br/><br/><I>备注：详细执行情况见【运维中心】-【调度日志】</I>";

    /**
     * 发送 EMAIL（调度）
     * <p>
     *
     * @param executionFlow
     */
    public static void sendEmail(ExecutionFlow executionFlow) {
        String title = genTitle(executionFlow.getType(), executionFlow.getStatus());
        String content = genContent(executionFlow.getType(), executionFlow.getProjectName(), executionFlow.getFlowName(), executionFlow.getFlowType(),
                                    executionFlow.getScheduleTime(), executionFlow.getStatus());
        mailSendService.sendToFlowMails(executionFlow.getFlowId(), title, content, true);
    }

    /**
     * 发送 EMAIL(补数据)
     * <p>
     *
     * @param projectFlow
     * @param isSuccess
     * @param resultList
     */
    public static void sendAddDataEmail(ProjectFlow projectFlow, boolean isSuccess, List<Map.Entry<Date, Boolean>> resultList) {
        String title = MessageFormat.format(TITLE_FORMAT, "补数据", isSuccess ? "成功" : "失败");
        StringBuilder builder = new StringBuilder();
        String head = MessageFormat.format(ADD_DATA_HEAD_FORMAT, "补数据", projectFlow.getProjectName(), projectFlow.getName(), getFlowTypeCnName(projectFlow.getType()));
        builder.append(head);
        for (Map.Entry<Date, Boolean> entry : resultList) {
            String item = MessageFormat.format(ADD_DATA_ITEM_FORMAT, BFDDateUtils.defaultFormat(entry.getKey()), getResultStatus(entry.getValue()));
            builder.append(item);
        }
        builder.append(ADD_DATA_TAIL_FORMAT);
        mailSendService.sendToFlowMails(projectFlow.getProjectId(), title, builder.toString(), true);
    }

    /**
     * 获取结果状态字符串
     * <p>
     *
     * @param isSuccess
     * @return 结果状态
     */
    private static String getResultStatus(Boolean isSuccess) {
        if (isSuccess == null) {
            return "未开始";
        }
        return isSuccess ? "<font color=\"green\">成功</font>" : "<font color=\"red\">失败</font>";
    }

    /**
     * 获取邮件标题
     * <p>
     *
     * @param runType
     * @param flowStatus
     * @return 标题
     */

    public static String genTitle(FlowRunType runType, FlowStatus flowStatus) {
        return MessageFormat.format(TITLE_FORMAT, getRunTypeCnName(runType), getFlowStatusCnName(flowStatus));
    }

    /**
     * 生成邮件内容
     * <p>
     *
     * @param runType
     * @param projectName
     * @param flowName
     * @param flowType
     * @param scheduleDate
     * @param flowStatus
     * @return 内容
     */
    public static String genContent(FlowRunType runType, String projectName, String flowName, FlowType flowType, int scheduleDate, FlowStatus flowStatus) {
        return MessageFormat.format(CONTENT_FORMAT, getRunTypeCnName(runType), projectName, flowName,
                                getFlowTypeCnName(flowType), BFDDateUtils.defaultFormat(scheduleDate),
                                    getFlowStatusCnNameH5(flowStatus));
    }

    /**
     * 获取执行类型的中文名
     * <p>
     *
     * @param runType
     * @return 中文名
     */
    private static String getRunTypeCnName(FlowRunType runType) {
        String cnName;
        switch (runType) {
            case ADD_DATA:
                cnName = "补数据";
                break;

            case DIRECT_RUN:
                cnName = "直接运行";
                break;

            case DISPATCH:
                cnName = "调度";
                break;

            case STREAMING:
                cnName = "流任务";
                break;

            default:
                cnName = "未知";
        }
        return cnName;
    }

    /**
     * 获取执行状态的中文名
     * <p>
     *
     * @param status
     * @return 中文名
     */
    private static String getFlowStatusCnName(FlowStatus status) {
        String cnName;
        // 仅当失败或者被 kill 的时候认为任务失败，否则认为成功
        switch (status) {
            case KILL:
            case FAILED:
                cnName = "失败";
                break;

            default:
                cnName = "成功";
        }
        return cnName;
    }

    /**
     * 获取执行状态的中文名
     * <p>
     *
     * @param status
     * @return 中文名
     */
    private static String getFlowStatusCnNameH5(FlowStatus status) {
        String cnName;
        // 仅当失败或者被 kill 的时候认为任务失败，否则认为成功
        switch (status) {
            case KILL:
            case FAILED:
                cnName = "<font color=\"red\">失败</font>";
                break;

            default:
                cnName = "<font color=\"green\">成功</font>";
        }
        return cnName;
    }

    /**
     * 获取工作流类型的中文名
     * <p>
     *
     * @param type
     * @return 中文名
     */
    private static String getFlowTypeCnName(FlowType type) {
        String cnName;
        switch (type) {
            case SHORT:
                cnName = "普通ETL";
                break;
            case LONG:
                cnName = "流任务";
                break;
            case ETL:
                cnName = "图形化ETL";
                break;

            default:
                cnName = "其他";
        }
        return cnName;
    }

}
