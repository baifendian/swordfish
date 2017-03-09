
package com.baifendian.swordfish.dao.mysql.model;

import com.baifendian.swordfish.common.utils.json.JsonUtil;
import com.baifendian.swordfish.common.utils.json.StringNodeJsonDeserializer;
import com.baifendian.swordfish.common.utils.json.StringNodeJsonSerializer;
import com.baifendian.swordfish.dao.mysql.enums.FlowType;
import com.baifendian.swordfish.dao.mysql.enums.ScheduleStatus;
import com.baifendian.swordfish.dao.mysql.model.flow.params.Property;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import javafx.util.Pair;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * workflow信息
 * <p>
 *
 * @author : wenting.wang
 * @date : 2016年8月24日
 */
public class ProjectFlow {

    private int id;

    private List<FlowNode> flowsNodes;

    private List<FlowNodeRelation> flowsNodesRelation;

    private String name;

    private int createTime;

    private int modifyTime;

    private int lastModifyBy;

    private String lastModifyByName;

    private int lastPublishBy;

    private String LastPublishByName;

    private int ownerId;

    private String ownerName;

    private FlowType type;

    private String proxyUser;

    private String queue;

    private String mailGroups;

    private int projectId;

    private String projectName;

    private ScheduleStatus scheduleStatus;

    @JsonDeserialize(using = StringNodeJsonDeserializer.class)
    @JsonSerialize(using = StringNodeJsonSerializer.class)
    private String inputTables;

    @JsonIgnore
    private List<Integer> inputTableList;

    @JsonDeserialize(using = StringNodeJsonDeserializer.class)
    @JsonSerialize(using = StringNodeJsonSerializer.class)
    private String outputTables;

    @JsonIgnore
    private List<Integer> outputTableList;

    @JsonDeserialize(using = StringNodeJsonDeserializer.class)
    @JsonSerialize(using = StringNodeJsonSerializer.class)
    private String resources;

    @JsonIgnore
    private List<Resource> resourceList;

    @JsonDeserialize(using = StringNodeJsonDeserializer.class)
    @JsonSerialize(using = StringNodeJsonSerializer.class)
    private String datasources;

    @JsonIgnore
    private List<DataSource> datasourceList;

    @JsonDeserialize(using = StringNodeJsonDeserializer.class)
    @JsonSerialize(using = StringNodeJsonSerializer.class)
    private String userDefinedParams;

    @JsonIgnore
    private Map<String, String> userDefinedParamMap;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<FlowNode> getFlowsNodes() {
        return flowsNodes;
    }

    public void setFlowsNodes(List<FlowNode> flowsNodes) {
        this.flowsNodes = flowsNodes;
    }

    public List<FlowNodeRelation> getFlowsNodesRelation() {
        return flowsNodesRelation;
    }

    public void setFlowsNodesRelation(List<FlowNodeRelation> flowsNodesRelation) {
        this.flowsNodesRelation = flowsNodesRelation;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLastModifyBy() {
        return lastModifyBy;
    }


    public String getLastModifyByName() {
        return lastModifyByName;
    }

    public void setLastModifyByName(String lastModifyByName) {
        this.lastModifyByName = lastModifyByName;
    }

    public int getLastPublishBy() {
        return lastPublishBy;
    }

    public String getLastPublishByName() {
        return LastPublishByName;
    }

    public void setLastPublishByName(String lastPublishByName) {
        LastPublishByName = lastPublishByName;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public FlowType getType() {
        return type;
    }

    public void setType(FlowType type) {
        this.type = type;
    }

    public String getMailGroups() {
        return mailGroups;
    }

    public void setMailGroups(String mailGroups) {
        this.mailGroups = mailGroups;
    }

    public String getProxyUser() {
        return proxyUser;
    }

    public void setProxyUser(String proxyUser) {
        this.proxyUser = proxyUser;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public ScheduleStatus getScheduleStatus() {
        return scheduleStatus;
    }

    public void setScheduleStatus(ScheduleStatus scheduleStatus) {
        this.scheduleStatus = scheduleStatus;
    }

    public String getInputTables() {
        return inputTables;
    }

    public void setInputTables(String inputTables) {
        this.inputTables = inputTables;
    }

    public List<Integer> getInputTableList() {
        if (inputTableList == null && StringUtils.isNotEmpty(inputTables)) {
            inputTableList = JsonUtil.parseObjectList(inputTables, Integer.class);
        }
        return inputTableList;
    }

    public void setInputTableList(List<Integer> inputTableList) {
        this.inputTableList = inputTableList;
    }

    public String getOutputTables() {
        return outputTables;
    }

    public void setOutputTables(String outputTables) {
        this.outputTables = outputTables;
    }

    public List<Integer> getOutputTableList() {
        if (outputTableList == null && StringUtils.isNotEmpty(outputTables)) {
            outputTableList = JsonUtil.parseObjectList(outputTables, Integer.class);
        }
        return outputTableList;
    }

    public void setOutputTableList(List<Integer> outputTableList) {
        this.outputTableList = outputTableList;
    }

    public int getCreateTime() {
        return createTime;
    }

    public void setCreateTime(int createTime) {
        this.createTime = createTime;
    }

    public int getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(int modifyTime) {
        this.modifyTime = modifyTime;
    }

    public void setLastModifyBy(int lastModifyBy) {
        this.lastModifyBy = lastModifyBy;
    }

    public void setLastPublishBy(int lastPublishBy) {
        this.lastPublishBy = lastPublishBy;
    }

    public String getResources() {
        return resources;
    }

    public void setResources(String resources) {
        this.resources = resources;
    }

    public List<Resource> getResourceList() {
        if (resourceList == null && StringUtils.isNotEmpty(resources)) {
            resourceList = JsonUtil.parseObjectList(resources, Resource.class);
        }
        return resourceList;
    }

    public void setResourceList(List<Resource> resourceList) {
        this.resourceList = resourceList;
    }

    public String getDatasources() {
        return datasources;
    }

    public void setDatasources(String datasources) {
        this.datasources = datasources;
    }

    public List<DataSource> getDatasourceList() {
        if (datasourceList == null && StringUtils.isNotEmpty(datasources)) {
            datasourceList = JsonUtil.parseObjectList(datasources, DataSource.class);
        }
        return datasourceList;
    }

    public void setDatasourceList(List<DataSource> datasourceList) {
        this.datasourceList = datasourceList;
    }

    public String getUserDefinedParams() {
        return userDefinedParams;
    }

    public void setUserDefinedParams(String userDefinedParams) {
        this.userDefinedParams = userDefinedParams;
    }

    public Map<String, String> getUserDefinedParamMap() {
        List<Property> propList;
        if (userDefinedParamMap == null && StringUtils.isNotEmpty(userDefinedParams)) {
            propList = JsonUtil.parseObjectList(userDefinedParams, Property.class);
            userDefinedParamMap = propList.stream().collect(Collectors.toMap(Property::getProp, Property::getValue));
        }
        return userDefinedParamMap;
    }

    public void setUserDefinedParamMap(Map<String, String> userDefinedParamMap) {
        this.userDefinedParamMap = userDefinedParamMap;
    }
}
