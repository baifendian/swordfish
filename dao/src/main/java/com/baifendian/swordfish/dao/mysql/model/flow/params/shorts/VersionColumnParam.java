package com.baifendian.swordfish.dao.mysql.model.flow.params.shorts;

/**
 * @auth: ronghua.yu
 * @time: 17/1/17
 * @desc:
 */
public class VersionColumnParam {
    private Integer type;
    private String value;

    public VersionColumnParam() {}

    public VersionColumnParam(Integer type, String value) {
        this.type = type;
        this.value = value;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
