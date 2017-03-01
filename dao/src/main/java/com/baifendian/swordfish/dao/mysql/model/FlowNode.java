
package com.baifendian.swordfish.dao.mysql.model;

import com.baifendian.swordfish.common.utils.json.JsonUtil;
import com.baifendian.swordfish.common.utils.json.StringNodeJsonDeserializer;
import com.baifendian.swordfish.common.utils.json.StringNodeJsonSerializer;
import com.baifendian.swordfish.dao.mysql.enums.NodeType;
import com.baifendian.swordfish.dao.mysql.model.flow.params.BaseParam;
import com.baifendian.swordfish.dao.mysql.model.flow.params.ParamUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.List;

/**
 * Created by wenting on 8/24/16.
 */
public class FlowNode {

    private Integer id;

    private String name;

    private String desc;

    private Date createTime;

    private Date modifyTime;

    private double posX;

    private double posY;

    private int lastModifyBy;

    private NodeType type;

    private int flowId;

    @JsonDeserialize(using = StringNodeJsonDeserializer.class)
    @JsonSerialize(using = StringNodeJsonSerializer.class)
    private String param;

    @JsonIgnore
    private BaseParam paramObject;

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

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }

    public double getPosX() {
        return posX;
    }

    public void setPosX(double posX) {
        this.posX = posX;
    }

    public double getPosY() {
        return posY;
    }

    public void setPosY(double posY) {
        this.posY = posY;
    }

    public int getLastModifyBy() {
        return lastModifyBy;
    }

    public void setLastModifyBy(int lastModifyBy) {
        this.lastModifyBy = lastModifyBy;
    }

    public NodeType getType() {
        return type;
    }

    public void setType(NodeType type) {
        this.type = type;
    }

    public int getFlowId() {
        return flowId;
    }

    public void setFlowId(int flowId) {
        this.flowId = flowId;
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public String getInputTables() {
        return inputTables;
    }

    public void setInputTables(String inputTables) {
        this.inputTables = inputTables;
    }

    public String getOutputTables() {
        return outputTables;
    }

    public void setOutputTables(String ouputTables) {
        this.outputTables = ouputTables;
    }

    /**
     * 获取 param 对象
     * <p>
     *
     * @return param 对象
     */
    public BaseParam getParamObject() {
        if (paramObject == null) {
            paramObject = ParamUtil.parseParam(param, type);
        }
        return paramObject;
    }

    /**
     * 获取 Input Table 列表
     * <p>
     *
     * @return Input Table 列表
     */
    public List<Integer> getInputTableList() {
        if (inputTableList == null && StringUtils.isNotEmpty(inputTables)) {
            inputTableList = JsonUtil.parseObjectList(inputTables, Integer.class);
        }
        return inputTableList;
    }

    /**
     * 获取 Output Table 列表
     * <p>
     *
     * @return Output Table 列表
     */
    public List<Integer> getOutputTableList() {
        if (outputTableList == null && StringUtils.isNotEmpty(outputTables)) {
            outputTableList = JsonUtil.parseObjectList(outputTables, Integer.class);
        }
        return outputTableList;
    }
}
