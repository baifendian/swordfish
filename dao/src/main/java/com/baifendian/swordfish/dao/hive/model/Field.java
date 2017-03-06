package com.baifendian.swordfish.dao.hive.model;

/**
 * hive 表字段描述
 * <p>
 *
 * @author : wenting.wang
 * @date : 2016年9月9日
 */
public class Field {

    private String name;

    private String type;

    private String comment;

    public Field() {}

    public Field(String name, String type, String comment) {
        this.name = name;
        this.type = type;
        this.comment = comment;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public String toString() {
        return "Field{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", comment='" + comment + '\'' +
                '}';
    }
}
