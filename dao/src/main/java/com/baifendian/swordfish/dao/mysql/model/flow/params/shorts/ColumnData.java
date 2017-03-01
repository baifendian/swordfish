
package com.baifendian.swordfish.dao.mysql.model.flow.params.shorts;

/**
 * Created by shuanghu on 16-11-9.
 */
class ColumnData {
    private String name;

    private String type;

    private boolean partition;

    // 是否是主键
    private boolean pk;

    private String targetFamily;

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

    public boolean isPartition() {
        return partition;
    }

    public void setPartition(boolean partition) {
        this.partition = partition;
    }

    public boolean isPk() {
        return pk;
    }

    public void setPk(boolean pk) {
        this.pk = pk;
    }

    public String getTargetFamily() {
        return targetFamily;
    }

    public void setTargetFamily(String targetFamily) {
        this.targetFamily = targetFamily;
    }

}
