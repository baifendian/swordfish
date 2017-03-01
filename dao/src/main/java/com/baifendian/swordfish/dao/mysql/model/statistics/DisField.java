package com.baifendian.swordfish.dao.mysql.model.statistics;

import com.baifendian.swordfish.dao.mysql.enums.FlowType;
import com.baifendian.swordfish.dao.mysql.enums.NodeType;
import com.baifendian.swordfish.dao.mysql.enums.ScheduleType;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 *  工作流调度类型分布,工作流类型分布,任务类型分布
 * <p>
 *
 * @author : wenting.wang
 * @date : 2016年9月30日
 */
public class DisField {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private ScheduleType scheduleType;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private FlowType flowType;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private NodeType nodeType;

    private int value;

    public ScheduleType getScheduleType() {
        return scheduleType;
    }

    public void setScheduleType(ScheduleType scheduleType) {
        this.scheduleType = scheduleType;
    }

    public FlowType getFlowType() {
        return flowType;
    }

    public void setFlowType(FlowType flowType) {
        this.flowType = flowType;
    }

    public NodeType getNodeType() {
        return nodeType;
    }

    public void setNodeType(NodeType nodeType) {
        this.nodeType = nodeType;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
