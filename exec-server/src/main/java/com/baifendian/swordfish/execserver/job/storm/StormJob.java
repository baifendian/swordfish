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
package com.baifendian.swordfish.execserver.job.storm;

import com.baifendian.swordfish.common.enums.StormType;
import com.baifendian.swordfish.common.job.struct.node.BaseParam;
import com.baifendian.swordfish.common.job.struct.node.storm.StormBuilder;
import com.baifendian.swordfish.common.job.struct.node.storm.StormParam;
import com.baifendian.swordfish.common.job.struct.node.storm.param.StormShellParam;
import com.baifendian.swordfish.dao.utils.json.JsonUtil;
import com.baifendian.swordfish.execserver.job.AbstractStormProcessJob;
import com.baifendian.swordfish.execserver.job.AbstractYarnProcessJob;
import com.baifendian.swordfish.execserver.job.JobProps;
import com.baifendian.swordfish.execserver.parameter.ParamHelper;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

import net.lingala.zip4j.core.ZipFile;

import static com.baifendian.swordfish.execserver.job.storm.StormSubmitArgsConst.STORM_COMMAND;

/**
 * Storm 任务
 */
public class StormJob extends AbstractStormProcessJob {

  private StormParam stormParam;

  /**
   * @param props
   * @param isLongJob
   * @param logger    @throws IOException
   */
  public StormJob(JobProps props, boolean isLongJob, Logger logger) {
    super(props, isLongJob, logger);
    stormParam = JsonUtil.parseObject(props.getJobParams(), StormBuilder.class).buildStormParam();
  }

  @Override
  public void before() throws Exception {
    try {
      //提前解压需要解压的文件
      if (stormParam.getType() == StormType.SHELL) {
        logger.info("Start extract resources...");
        StormShellParam stormShellParam = (StormShellParam) stormParam.getStormParam();
        String resources = props.getWorkDir() + "/" + stormShellParam.getResources().getRes();
        logger.info("Resources paht: {}", resources);

        ZipFile zipFile = new ZipFile(resources);
        zipFile.extractAll(props.getWorkDir());
        logger.info("Finish extract resources to {}", props.getWorkDir());
      }
    }catch (Exception e){
      logger.error("before function error",e);
      throw e;
    }
  }

  @Override
  protected String createCommand() throws Exception {
    try {
      logger.info("start create command...");
      List<String> args = new ArrayList<>();

      args.add(STORM_COMMAND);

      args.addAll(StormSubmitArgsUtil.buildArgs(stormParam));

      String command = ParamHelper
              .resolvePlaceholders(String.join(" ", args), props.getDefinedParams());

      logger.info("Finish create command: {}", command);

      return command;
    } catch (Exception e) {
      logger.error("create command error", e);
      throw e;
    }

  }

  @Override
  public BaseParam getParam() {
    return stormParam;
  }
}
