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

package com.baifendian.swordfish.execserver.job.mr;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Hadoop jar 参数构建器 <p>
 *
 * @author : dsfan
 * @date : 2016年11月22日
 */
public class HadoopJarArgsUtil {

  /**
   * 构建参数数组 <p>
   *
   * @return 参数数组
   */
  public static List<String> buildArgs(MrParam param) {
    List<String> args = new ArrayList<>();

    if (StringUtils.isNotEmpty(param.getMainJar().getRes())) {
      args.add(param.getMainJar().getRes());
    }

    if (StringUtils.isNotEmpty(param.getMainClass())) {
      args.add(param.getMainClass());
    }

    if (param.getDArgs() != null && !param.getDArgs().isEmpty()) {
      for (String darg : param.getDArgs()) {
        args.add(HadoopJarArgsConst.D);
        args.add(darg);
      }
    }

    if (param.getLibJars() != null && !param.getLibJars().isEmpty()) {
      args.add(HadoopJarArgsConst.JARS);
      args.add(StringUtils.join(param.getLibJars().stream().map(p -> p.getRes()).toArray(), ","));
    }

    if (param.getFiles() != null && !param.getFiles().isEmpty()) {
      args.add(HadoopJarArgsConst.FILES);
      args.add(StringUtils.join(param.getFiles().stream().map(p -> p.getSymbolicRes()).toArray(), ","));
    }

    if (param.getArchives() != null && !param.getArchives().isEmpty()) {
      args.add(HadoopJarArgsConst.ARCHIVES);
      args.add(StringUtils.join(param.getArchives().stream().map(p -> p.getSymbolicRes()).toArray(), ","));
    }

    if (StringUtils.isNotEmpty(param.getQueue())) {
      args.add(HadoopJarArgsConst.D);
      args.add(HadoopJarArgsConst.QUEUE + "=" + param.getQueue());
    }

    if (StringUtils.isNotEmpty(param.getArgs())) {
      args.add(param.getArgs());
    }
    return args;
  }

}

