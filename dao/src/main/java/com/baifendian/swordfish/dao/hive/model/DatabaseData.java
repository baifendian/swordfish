package com.baifendian.swordfish.dao.hive.model;

/**
 * database基本信息
 * <p>
 *
 * @author : wenting.wang
 * @date : 2016年9月8日
 */
public class DatabaseData{

    /**数据库名**/
    private String name;

    /**描述信息**/
    private String description;

    /**数据位置信息**/
    private String location;

    /**数据的拥有者**/
    private String ownerName;

    /**数据拥有者的类型**/
    private int ownerType;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public int getOwnerType() {
        return ownerType;
    }

    public void setOwnerType(int ownerType) {
        this.ownerType = ownerType;
    }
}
