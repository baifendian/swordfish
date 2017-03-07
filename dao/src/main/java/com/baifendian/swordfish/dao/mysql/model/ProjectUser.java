package com.baifendian.swordfish.dao.mysql.model;

import com.baifendian.swordfish.common.utils.BFDDateUtils;
import com.baifendian.swordfish.common.utils.json.DateSerializer;

import java.util.Date;

/**
 * Created by caojingwei on 16/8/25.
 */
public class ProjectUser {
    private int projectId;
    private int userId;
    private int createTime;

    public ProjectUser() {
    }

    public ProjectUser(int projectId, int userId, int createTime) {
        this.projectId = projectId;
        this.userId = userId;
        this.createTime = createTime;
    }
    public ProjectUser(int projectId, int userId) {
        this.projectId = projectId;
        this.userId = userId;
        this.createTime = BFDDateUtils.getSecs();
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

    public int getCreateTime() {
        return createTime;
    }

    public void setCreateTime(int createTime) {
        this.createTime = createTime;
    }
}
