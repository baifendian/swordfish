package com.baifendian.swordfish.dao.mysql.model.flow.params.shorts;

/**
 * @auth: ronghua.yu
 * @time: 17/1/17
 * @desc:
 */
public class RowkeyColumnParam {
    private String form;
    private String type;
    private String value;

    public RowkeyColumnParam() {}

    public RowkeyColumnParam(String form, String type, String value) {
        this.form = form;
        this.type = type;
        this.value = value;
    }

    public String getForm() {
        return form;
    }

    public void setForm(String form) {
        this.form = form;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
