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
package com.baifendian.swordfish.execserver.job.mr;

import com.baifendian.swordfish.common.job.struct.node.BaseParam;
import com.baifendian.swordfish.common.job.struct.node.mr.MrParam;
import com.baifendian.swordfish.dao.utils.json.JsonUtil;
import com.baifendian.swordfish.execserver.job.JobProps;
import com.baifendian.swordfish.execserver.job.yarn.AbstractYarnJob;
import com.baifendian.swordfish.execserver.parameter.ParamHelper;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * mr 作业 <p>
 */
public class MrJob extends AbstractYarnJob {

  /**
   * hadoop
   */
  private static final String HADOOP_COMMAND = "hadoop";

  /**
   * jar
   */
  private static final String HADOOP_JAR = "jar";

  /**
   * mapreduce param
   */
  private MrParam mrParam;

  /**
   * @param props
   * @param logger
   * @throws IOException
   */
  public MrJob(JobProps props, boolean isLongJob, Logger logger) {
    super(props, isLongJob, logger);
  }

  @Override
  public void initJob() {
    mrParam = JsonUtil.parseObject(props.getJobParams(), MrParam.class);
    mrParam.setQueue(props.getQueue());
  }

  /**
   * hadoop 命令示例为:
   * hadoop jar hadoop-mapreduce-examples-<ver>.jar wordcount \
   * -files dir1/dict.txt#dict1,dir2/dict.txt#dict2 \
   * -archives mytar.tgz#tgzdir \
   * -Dwordcount.case.sensitive=true \
   * input output
   */
  @Override
  public String createCommand() throws Exception {
    List<String> args = new ArrayList<>();

    args.add(HADOOP_COMMAND);
    args.add(HADOOP_JAR);

    // 添加其它参数
    args.addAll(HadoopJarArgsUtil.buildArgs(mrParam));

    String command = ParamHelper.resolvePlaceholders(String.join(" ", args), props.getDefinedParams());

    logger.info("mr job command:\n{}", command);

    return command;
  }

  @Override
  public BaseParam getParam() {
    return mrParam;
  }
}
