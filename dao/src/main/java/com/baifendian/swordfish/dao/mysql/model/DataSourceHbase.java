package com.baifendian.swordfish.dao.mysql.model;

/**
 * @auth: ronghua.yu
 * @time: 16/12/22
 * @desc:
 */
public class DataSourceHbase extends DataSourceDbBase {
    private String address;
    private String zkQuorum;
    private Boolean distributed;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getZkQuorum() {
        return zkQuorum;
    }

    public void setZkQuorum(String zkQuorum) {
        this.zkQuorum = zkQuorum;
    }

    public Boolean getDistributed() {
        return distributed;
    }

    public void setDistributed(Boolean distributed) {
        this.distributed = distributed;
    }

    @Override
    public String toString() {
        return "DataSourceHbase{" +
                "address='" + address + '\'' +
                ", zkQuorum='" + zkQuorum + '\'' +
                ", distributed=" + distributed +
                '}';
    }
}
