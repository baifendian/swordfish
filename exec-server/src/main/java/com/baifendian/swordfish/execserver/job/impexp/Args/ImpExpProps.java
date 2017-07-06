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
package com.baifendian.swordfish.execserver.job.impexp.Args;

import com.baifendian.swordfish.common.job.struct.node.impexp.ImpExpParam;
import com.baifendian.swordfish.dao.DaoFactory;
import com.baifendian.swordfish.dao.DatasourceDao;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;

/**
 * ImpExp 参数
 */
public class ImpExpProps {
  private DatasourceDao datasourceDao;

  /**
   * 读取的配置文件
   */
  private Configuration hadoopConf;
  private Configuration workConf;
  private Configuration hiveConf;

  /**
   * swordfish的导入导出配置
   */
  private ImpExpParam impExpParam;

  public ImpExpProps(ImpExpParam impExpParam,Logger logger) {
    this.impExpParam = impExpParam;
    datasourceDao = DaoFactory.getDaoInstance(DatasourceDao.class);
    try {
      hadoopConf = new PropertiesConfiguration("common/hadoop/hadoop.properties");
      workConf = new PropertiesConfiguration("worker.properties");
      hiveConf = new PropertiesConfiguration("common/hive/hive.properties");
    } catch (ConfigurationException e) {
      logger.error("Init impExpProps error", e);
      throw new IllegalArgumentException("Init impExpProps error!");
    }
  }

  public DatasourceDao getDatasourceDao() {
    return datasourceDao;
  }

  public void setDatasourceDao(DatasourceDao datasourceDao) {
    this.datasourceDao = datasourceDao;
  }

  public Configuration getHadoopConf() {
    return hadoopConf;
  }

  public void setHadoopConf(Configuration hadoopConf) {
    this.hadoopConf = hadoopConf;
  }

  public Configuration getWorkConf() {
    return workConf;
  }

  public void setWorkConf(Configuration workConf) {
    this.workConf = workConf;
  }

  public Configuration getHiveConf() {
    return hiveConf;
  }

  public void setHiveConf(Configuration hiveConf) {
    this.hiveConf = hiveConf;
  }

  public ImpExpParam getImpExpParam() {
    return impExpParam;
  }

  public void setImpExpParam(ImpExpParam impExpParam) {
    this.impExpParam = impExpParam;
  }


}
