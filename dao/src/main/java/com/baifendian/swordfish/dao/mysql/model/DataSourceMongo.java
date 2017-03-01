package com.baifendian.swordfish.dao.mysql.model;

/**
 * @auth: ronghua.yu
 * @time: 16/8/19
 * @desc:
 */
public class DataSourceMongo extends DataSourceDbBase {
  private String address;
  private String database;

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public String getDatabase() {
    return database;
  }

  public void setDatabase(String database) {
    this.database = database;
  }

  @Override
  public String toString() {
    return "DataSourceMongo{" +
        "address='" + address + '\'' +
        ", database='" + database + '\'' +
        '}';
  }
}
