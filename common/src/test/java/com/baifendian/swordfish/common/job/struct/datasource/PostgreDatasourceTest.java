package com.baifendian.swordfish.common.job.struct.datasource;

import org.junit.Test;

public class PostgreDatasourceTest {
  @Test
  public void testIsConnectable() throws Exception {
    PostgreDatasource postgreDatasource = new PostgreDatasource();
    postgreDatasource.setAddress("jdbc:postgresql://172.24.8.98:5432");
    postgreDatasource.setUser("postgres");
    postgreDatasource.setDatabase("test01");
    postgreDatasource.setPassword("postgres-2017");
    postgreDatasource.isConnectable();
  }
}
