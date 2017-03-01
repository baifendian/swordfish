package com.baifendian.swordfish.dao.mysql.model.flow.params.shorts;

/**
 * Created by shuanghu on 16-11-9.
 */
public class MappingRelation {
    // 目标表为HBase时，族列
    private String targetFamily;

    //目标字段名
    private String targetField;

    //目标字段类型
    private String targetType;

    //源字段名
    private String originField;

    //源字段类型
    private String originType;

    public MappingRelation(){

    }

    public MappingRelation(String originField, String originType, String targetField, String targetType){
        this.targetField = targetField;
        this.targetType = targetType;
        this.originField = originField;
        this.originType = originType;
    }

    public MappingRelation(String targetFamily, String targetField, String targetType, String originField, String originType) {
        this.targetFamily = targetFamily;
        this.targetField = targetField;
        this.targetType = targetType;
        this.originField = originField;
        this.originType = originType;
    }

    public String getTargetFamily() {
        return targetFamily;
    }

    public void setTargetFamily(String targetFamily) {
        this.targetFamily = targetFamily;
    }

    public String getTargetField() {
        return targetField;
    }

    public void setTargetField(String targetField) {
        this.targetField = targetField;
    }

    public String getTargetType() {
        return targetType;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    public String getOriginField() {
        return originField;
    }

    public void setOriginField(String originField) {
        this.originField = originField;
    }

    public String getOriginType() {
        return originType;
    }

    public void setOriginType(String originType) {
        this.originType = originType;
    }
}
