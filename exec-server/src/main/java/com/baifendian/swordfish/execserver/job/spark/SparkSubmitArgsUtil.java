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

import com.baifendian.swordfish.common.utils.CommonUtil;
import com.baifendian.swordfish.dao.model.flow.params.Property;
import com.baifendian.swordfish.execserver.job.ResourceInfo;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Spark 提交参数构建器 <p>
 *
 * @author : dsfan
 * @date : 2016年11月9日
 */
public class SparkSubmitArgsUtil {

  /**
   * 构建参数数组 <p>
   *
   * @return 参数数组
   */
  public static List<String> buildArgs(SparkParam param) {
    List<String> args = new ArrayList<>();

    args.add(SparkSubmitArgsConst.MASTER);
    args.add("yarn");

    args.add(SparkSubmitArgsConst.DEPLOY_MODE);
    args.add("cluster");

    if (StringUtils.isNotEmpty(param.getMainClass())) {
      args.add(SparkSubmitArgsConst.CLASS);
      args.add(param.getMainClass());
    }

    if (param.getDriverCores() != 0) {
      args.add(SparkSubmitArgsConst.DRIVER_CORES);
      args.add(String.format("%d", param.getDriverCores()));
    }

    if (StringUtils.isNotEmpty(param.getDriverMemory())) {
      args.add(SparkSubmitArgsConst.DRIVER_MEMORY);
      args.add(param.getDriverMemory());
    }

    if (param.getNumExecutors() != 0) {
      args.add(SparkSubmitArgsConst.NUM_EXECUTORS);
      args.add(String.format("%d", param.getNumExecutors()));
    }

    if (param.getExecutorCores() != 0) {
      args.add(SparkSubmitArgsConst.EXECUTOR_CORES);
      args.add(String.format("%d", param.getExecutorCores()));
    }

    if (StringUtils.isNotEmpty(param.getExecutorMemory())) {
      args.add(SparkSubmitArgsConst.EXECUTOR_MEMORY);
      args.add(param.getExecutorMemory());
    }

    if (param.getLibJars() != null && !param.getLibJars().isEmpty()) {
      args.add(SparkSubmitArgsConst.JARS);
      args.add(getFilesStr(param.getLibJars(), false));
    }

    if (param.getFiles() != null && !param.getFiles().isEmpty()) {
      args.add(SparkSubmitArgsConst.FILES);
      args.add(getFilesStr(param.getFiles(), true));
    }

    if (!param.getArchives().isEmpty()) {
      args.add(SparkSubmitArgsConst.ARCHIVES);
      args.add(getFilesStr(param.getArchives(), true));
    }

    if (StringUtils.isNotEmpty(param.getQueue())) {
      args.add(SparkSubmitArgsConst.QUEUE);
      args.add(param.getQueue());
    }

    if (!param.getProperties().isEmpty()) {
      for (Property property : param.getProperties()) {
        args.add(SparkSubmitArgsConst.CONF);
        args.add(property.getProp() + "=" + property.getValue());
      }
    }

    if (param.getMainJar() != null) {
      args.add(param.getMainJar().getRes());
    }

    if (StringUtils.isNotEmpty(param.getArgs())) {
      args.add(param.getArgs());
    }
    return args;
  }

  private static String getFilesStr(List<ResourceInfo> files, boolean isSymbolic) {
    if (files == null)
      return "";

    if (isSymbolic)
      return StringUtils.join(files.stream().map(p -> p.getSymbolicRes()).toArray(), ",");
    else
      return StringUtils.join(files.stream().map(p -> p.getRes()).toArray(), ",");
  }

}
