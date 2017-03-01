package com.baifendian.swordfish.dao.mysql.model.flow;
/**
 * workflow 依赖关系
 * <p>
 *
 * @author : wenting.wang
 * @date : 2016年8月26日
 */
public class DepWorkflow {

    private int projectId;

    private int workflowId;

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public int getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(int workflowId) {
        this.workflowId = workflowId;
    }
}
