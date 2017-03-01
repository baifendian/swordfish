package com.baifendian.swordfish.dao.mysql.model;

import com.baifendian.swordfish.common.utils.json.DateSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.Date;

/**
 * Created by caojingwei on 16/8/25.
 */
public class ProjectUser {
    private int projectId;
    private int userId;
    private Date createTime;

    public ProjectUser() {
    }

    public ProjectUser(int projectId, int userId, Date createTime) {
        this.projectId = projectId;
        this.userId = userId;
        this.createTime = createTime;
    }
    public ProjectUser(int projectId, int userId) {
        this.projectId = projectId;
        this.userId = userId;
        this.createTime = new Date();
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @JsonSerialize(using = DateSerializer.class)
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
