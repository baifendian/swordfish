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
package com.baifendian.swordfish.execserver.job.spark;

import com.baifendian.swordfish.common.job.struct.node.BaseParam;
import com.baifendian.swordfish.common.job.struct.node.spark.SparkParam;
import com.baifendian.swordfish.dao.utils.json.JsonUtil;
import com.baifendian.swordfish.execserver.job.JobProps;
import com.baifendian.swordfish.execserver.job.yarn.AbstractYarnJob;
import com.baifendian.swordfish.execserver.parameter.ParamHelper;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Spark 作业 <p>
 */
public class SparkJob extends AbstractYarnJob {
  /**
   * spark 命令
   */
  private static final String SPARK_COMMAND = "spark-submit";

  /**
   * 提交的参数
   */
  private SparkParam sparkParam;

  public SparkJob(JobProps props, Logger logger) {
    super(props, logger);
  }

  @Override
  public void initJob() {
    sparkParam = JsonUtil.parseObject(props.getJobParams(), SparkParam.class);
    sparkParam.setQueue(props.getQueue());

    if (sparkParam.getArgs() != null) {
      String args = ParamHelper.resolvePlaceholders(sparkParam.getArgs(), props.getDefinedParams());
      sparkParam.setArgs(args);
    }
  }

  /**
   * spark 示例:
   * <p>
   * spark-submit --class org.apache.spark.examples.FilesAndArchivesTest \
   * --master yarn \
   * --deploy-mode cluster \
   * --driver-cores 1 \
   * --driver-memory 512M \
   * --num-executors 4 \
   * --executor-cores 2 \
   * --executor-memory 1024M \
   * --files story.txt#st \
   * --archives dicts.tar.gz#z \
   * spark-examples-1.0-SNAPSHOT-hadoop2.6.0.jar st z blackheads,Adrien
   *
   * @return
   * @throws Exception
   */
  @Override
  public String createCommand() throws Exception {
    List<String> args = new ArrayList<>();

    args.add(SPARK_COMMAND);

    // 添加其它参数
    args.addAll(SparkSubmitArgsUtil.buildArgs(sparkParam));

    String command = ParamHelper.resolvePlaceholders(String.join(" \\\n", args), props.getDefinedParams());

    logger.info("spark job command:\n{}", command);

    return command;
  }

  /**
   * 查找日志连接
   *
   * @param line
   * @return
   */
  @Override
  public String findLogLinks(String line) {
    if (line.contains("tracking URL:")) {
      return line.substring(line.indexOf("URL:") + "URL:".length() + 1);
    }

    return null;
  }

  @Override
  public BaseParam getParam() {
    return sparkParam;
  }
}
