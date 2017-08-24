/*
 * Copyright (C) 2017 Baifendian Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.baifendian.swordfish.common.job.struct.datasource;

import org.junit.Test;

public class FtpDatasourceTest {

  @Test
  public void testIsConnectable() throws Exception {
//    FtpDatasource ftpDatasource = new FtpDatasource();
//
//    ftpDatasource.setHost("10.12.7.10");
//    ftpDatasource.setPort(21);
//    ftpDatasource.setUser("bfd_541");
//    ftpDatasource.setPassword("bfd123456");
//    ftpDatasource.isConnectable();
  }

  @Test(expected = Exception.class)
  public void testIsConnectable2() throws Exception {
    FtpDatasource ftpDatasource = new FtpDatasource();

    ftpDatasource.setHost("10.12.7.10");
    ftpDatasource.setPort(21);
    ftpDatasource.setUser("bfd_541");
    ftpDatasource.setPassword("bfd1234561111");
    ftpDatasource.isConnectable();
  }
}
