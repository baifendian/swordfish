package com.baifendian.swordfish.dao.config;

import com.baifendian.swordfish.common.hive.HiveConnectionClient;
import com.baifendian.swordfish.common.hive.HiveMetaPoolClient;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hive.conf.HiveConf;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource({"classpath:common/hive/hive.properties"})
public class HiveConfig {

  @Value("${hive.metastore.uris}")
  private String metastoreUris;

  @Value("${hive.thrift.uris}")
  private String thriftUris;

  @Value("${hive.root.user}")
  private String rootUser;

  @Value("${hive.root.password}")
  private String password;

  @Value("${javax.jdo.option.ConnectionURL}")
  private String jdoUrl;

  @Value("${javax.jdo.option.ConnectionUserName}")
  private String jdoUser;

  @Value("${javax.jdo.option.ConnectionPassword}")
  private String jdoPassword;

  @Value("${javax.jdo.option.ConnectionDriverName}")
  private String jdoDriverName;

  @Value("${hive.db.name}")
  private String hiveDbName;

  @Value("${hive.user.name}")
  private String hiveUserName;

  org.apache.hadoop.conf.Configuration conf = new org.apache.hadoop.conf.Configuration();

  @Bean
  public HiveMetaPoolClient hiveMetaPoolClient() {
    HiveMetaPoolClient.init(metastoreUris, jdoUrl,
        jdoUser, jdoPassword, jdoDriverName);
    return HiveMetaPoolClient.getInstance();
  }

  @Bean
  public HiveConnectionClient hiveConnectionClient() {
    return HiveConnectionClient.getInstance();
  }

  @Bean
  public HiveConf  hiveConf() {
    HiveConf hConf;
    org.apache.hadoop.conf.Configuration conf = new org.apache.hadoop.conf.Configuration();
    if(StringUtils.isNotEmpty(metastoreUris)) {
      conf.set("hive.metastore.uris", metastoreUris);
    } else {
      conf.set("javax.jdo.option.ConnectionURL", jdoUrl);
      conf.set("javax.jdo.option.ConnectionUserName", jdoUser);
      conf.set("javax.jdo.option.ConnectionPassword", jdoPassword);
      conf.set("javax.jdo.option.ConnectionDriverName", jdoDriverName);
    }
    hConf = new HiveConf(conf, HiveConf.class);
    return  hConf;
  }

  public String getMetastoreUris() {
    return metastoreUris;
  }

  public void setMetastoreUris(String metastoreUris) {
    this.metastoreUris = metastoreUris;
  }

  public String getThriftUris() {
    return thriftUris;
  }

  public void setThriftUris(String thriftUris) {
    this.thriftUris = thriftUris;
  }

  public String getRootUser() {
    return rootUser;
  }

  public void setRootUser(String rootUser) {
    this.rootUser = rootUser;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getJdoUrl() {
    return jdoUrl;
  }

  public void setJdoUrl(String jdoUrl) {
    this.jdoUrl = jdoUrl;
  }

  public String getJdoUser() {
    return jdoUser;
  }

  public void setJdoUser(String jdoUser) {
    this.jdoUser = jdoUser;
  }

  public String getJdoPassword() {
    return jdoPassword;
  }

  public void setJdoPassword(String jdoPassword) {
    this.jdoPassword = jdoPassword;
  }

  public String getJdoDriverName() {
    return jdoDriverName;
  }

  public void setJdoDriverName(String jdoDriverName) {
    this.jdoDriverName = jdoDriverName;
  }

  public String getHiveDbName() {
    return hiveDbName;
  }

  public void setHiveDbName(String hiveDbName) {
    this.hiveDbName = hiveDbName;
  }

  public String getHiveUserName() {
    return hiveUserName;
  }

  public void setHiveUserName(String hiveUserName) {
    this.hiveUserName = hiveUserName;
  }
}
