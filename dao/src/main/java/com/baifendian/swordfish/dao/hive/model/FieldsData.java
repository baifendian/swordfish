package com.baifendian.swordfish.dao.hive.model;

import java.util.List;

/**
 * hive 表中的所有字段描述
 * <p>
 *
 * @author : wenting.wang
 * @date : 2016年9月9日
 */
public class FieldsData{

    private List<Field> fields;

    public List<Field> getFields() {
        return fields;
    }

    public void setFields(List<Field> fields) {
        this.fields = fields;
    }
}
