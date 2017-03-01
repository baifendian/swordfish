
package com.baifendian.swordfish.dao.mysql.model;

import com.baifendian.swordfish.common.utils.json.JsonUtil;
import com.baifendian.swordfish.common.utils.json.StringNodeJsonDeserializer;
import com.baifendian.swordfish.common.utils.json.StringNodeJsonSerializer;
import com.baifendian.swordfish.dao.mysql.model.flow.EdgeAttribute;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * 已发布workflow节点关系信息
 * <p>
 *
 * @author : wenting.wang
 * @author : dsfan
 * @date : 2016年8月24日
 */
public class FlowNodeRelation {

    private int flowId;

    private int startId;

    private int endId;

    @JsonDeserialize(using = StringNodeJsonDeserializer.class)
    @JsonSerialize(using = StringNodeJsonSerializer.class)
    private String attribute;

    @JsonIgnore
    private EdgeAttribute attributeObject;

    public int getFlowId() {
        return flowId;
    }

    public void setFlowId(int flowId) {
        this.flowId = flowId;
    }

    public int getStartId() {
        return startId;
    }

    public void setStartId(int startId) {
        this.startId = startId;
    }

    public int getEndId() {
        return endId;
    }

    public void setEndId(int endId) {
        this.endId = endId;
    }

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    /**
     * 获取 EdgeAttribute 对象
     * <p>
     *
     * @return EdgeAttribute 对象
     */
    public EdgeAttribute getAttributeObject() {
        if (attributeObject == null) {
            attributeObject = JsonUtil.parseObject(attribute, EdgeAttribute.class);
        }
        return attributeObject;
    }

}
