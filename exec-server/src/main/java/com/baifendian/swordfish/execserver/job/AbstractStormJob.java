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
package com.baifendian.swordfish.execserver.job;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;

import java.util.List;


/**
 * storm 框架任务
 */
public abstract class AbstractStormJob extends Job {

  protected PropertiesConfiguration stormConf;

  /**
   * storm 任务名称
   */
  protected String TopologyName;

  /**
   * storm 任务Id
   */
  protected String TopologyId;

  public AbstractStormJob(JobProps props, boolean isLongJob, Logger logger) {
    super(props, isLongJob, logger);
  }

  @Override
  public void init() throws Exception {
    super.init();
    stormConf = new PropertiesConfiguration("common/storm.properties");
  }

  @Override
  public void logProcess(List<String> logs) {
    super.logProcess(logs);

  }




  @Override
  public void cancel(boolean cancelApplication) throws Exception {
    super.cancel(cancelApplication);
  }


}
