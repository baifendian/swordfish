package com.baifendian.swordfish.dao.mysql.model;

import com.baifendian.swordfish.common.consts.Constants;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.ObjectUtils;

import java.util.Date;

/**
 * Created by caojingwei on 16/8/25.
 */
public class Project {
    private Integer id;
    private String name;
    private String desc;
    private Integer tenantId;
    private String tenantName;
    @JsonFormat(pattern = Constants.BASE_DATETIME_FORMAT)
    private Date createTime;
    @JsonFormat(pattern = Constants.BASE_DATETIME_FORMAT)
    private Date modifyTime;
    private Integer ownerId;
    private String ownerName;
    private Integer queueId;
    private String queueName;

    public Project(Integer id, String name, String desc, Integer tenantId, String tenantName
            , Date createTime, Date modifyTime, Integer ownerId, String ownerName
            , Integer queueId, String queueName) {
        this.id = id;
        this.name = name;
        this.desc = desc;
        this.tenantId = tenantId;
        this.tenantName = tenantName;
        this.createTime = createTime;
        this.modifyTime = modifyTime;
        this.ownerId = ownerId;
        this.ownerName = ownerName;
        this.queueId = queueId;
        this.queueName = queueName;
    }

    public Project(){}

    public Project(ProjectBuilder builder){
        this.id = builder.id;
        this.name = builder.name;
        this.desc = builder.desc;
        this.tenantId = builder.tenantId;
        this.tenantName = builder.tenantName;
        this.createTime = builder.createTime;
        this.modifyTime = builder.modifyTime;
        this.ownerId = builder.ownerId;
        this.ownerName = builder.ownerName;
        this.queueId = builder.queueId;
        this.queueName = builder.queueName;
    }

    static public class ProjectBuilder{
        private Integer id;
        private String name;
        private String desc;
        private Integer tenantId;
        private String tenantName;
        private Date createTime;
        private Date modifyTime;
        private Integer ownerId;
        private String ownerName;
        private Integer queueId;
        private String queueName;

        public Project build(){
            return new Project(this);
        }

        public ProjectBuilder id(int id){
            this.id = id;
            return this;
        }
        public ProjectBuilder name(String name){
            this.name = name;
            return this;
        }
        public ProjectBuilder desc(String desc){
            this.desc = desc;
            return this;
        }
        public ProjectBuilder tenantId(int id){
            this.tenantId = id;
            return this;
        }
        public ProjectBuilder tenantName(String tenantName){
            this.tenantName = tenantName;
            return this;
        }
        public ProjectBuilder createTime(Date createTime){
            this.createTime = createTime;
            return this;
        }
        public ProjectBuilder createTime(){
            this.createTime = new Date();
            return this;
        }
        public ProjectBuilder modifyTime(Date modifyTime){
            this.modifyTime = modifyTime;
            return this;
        }
        public ProjectBuilder ownerId(int ownerId){
            this.ownerId = ownerId;
            return this;
        }
        public ProjectBuilder ownerName(String ownerName){
            this.ownerName = ownerName;
            return this;
        }
        public ProjectBuilder queueId(int queueId){
            this.queueId = queueId;
            return this;
        }
        public ProjectBuilder queueName(String queueName){
            this.queueName = queueName;
            return this;
        }
    }

    //@JsonSerialize(using = DateSerializer.class)
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    //@JsonSerialize(using = DateSerializer.class)
    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Integer getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Integer ownerId) {
        this.ownerId = ownerId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public Integer getQueueId() {
        return queueId;
    }

    public void setQueueId(Integer queueId) {
        this.queueId = queueId;
    }

    public String getQueueName() {
        return queueName;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    public Integer getTenantId() {
        return tenantId;
    }

    public void setTenantId(Integer tenantId) {
        this.tenantId = tenantId;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public boolean equals(Object o){
        if (!(o instanceof Project))
            return false;
        Project project = (Project)o;
        if (ObjectUtils.notEqual(this.id,project.getId())){
            return false;
        }
        if (ObjectUtils.notEqual(this.name,project.getName())){
            return false;
        }
        if (ObjectUtils.notEqual(this.desc,project.getDesc())){
            return false;
        }
        if (ObjectUtils.notEqual(this.tenantId,project.tenantId)){
            return false;
        }
        if (ObjectUtils.notEqual(this.ownerId,project.getOwnerId())){
            return false;
        }
        if (ObjectUtils.notEqual(this.queueId,project.getQueueId())){
            return false;
        }
        return true;
    }
}
