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

import com.baifendian.swordfish.common.job.struct.node.storm.StormParam;
import com.baifendian.swordfish.common.job.struct.node.storm.param.StormJarParam;
import com.baifendian.swordfish.common.job.struct.node.storm.param.StormShellParam;
import com.baifendian.swordfish.common.job.struct.node.storm.param.StormSqlParam;
import com.baifendian.swordfish.common.job.struct.resource.ResourceInfo;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static com.baifendian.swordfish.execserver.job.storm.StormSubmitArgsConst.*;

/**
 * storm命令参数拼接
 */
public class StormSubmitArgsUtil {

  public static List<String> buildArgs(StormParam stormParam) {
    switch (stormParam.getType()) {
      case JAR:
        return jarArgs((StormJarParam) stormParam.getStormParam());
      case SQL:
        return sqlArgs((StormSqlParam) stormParam.getStormParam());
      case SHELL:
        return shellArgs((StormShellParam) stormParam.getStormParam());
      default:
        throw  new IllegalArgumentException("storm type unsupport");
    }
  }

  private static List<String> jarArgs(StormJarParam stormJarParam) {
    List<String> args = new ArrayList<>();
    args.add(JAR);

    //add mainJar
    args.add(stormJarParam.getMainJar().getRes());

    //add mainClass
    args.add(stormJarParam.getMainClass());

    //add Jars
    List<ResourceInfo> jars = stormJarParam.getJars();
    if (CollectionUtils.isNotEmpty(jars)){
      args.add(JARS);
      args.add(StringUtils.join(jars.stream().map(p -> p.getRes()).toArray(), ","));
    }

    if (StringUtils.isNotEmpty(stormJarParam.getArgs())){
      args.add(stormJarParam.getArgs());
    }

    if (StringUtils.isNotEmpty(stormJarParam.getArtifacts())){
      args.add(ARTIFACTS);
      args.add(stormJarParam.getArtifacts());
    }

    if (StringUtils.isNotEmpty(stormJarParam.getArtifactRepositories())){
      args.add(ARTIFACTREPOSITORIES);
      args.add(stormJarParam.getArtifactRepositories());
    }

    return args;

  }

  private static List<String> sqlArgs(StormSqlParam stormSqlParam) {
    List<String> args = new ArrayList<>();
    args.add(SQL);

    args.add(stormSqlParam.getSqlFile().getRes());

    //add Jars
    List<ResourceInfo> jars = stormSqlParam.getJars();
    if (CollectionUtils.isNotEmpty(jars)){
      args.add(JARS);
      args.add(StringUtils.join(jars.stream().map(p -> p.getRes()).toArray(), ","));
    }

    if (StringUtils.isNotEmpty(stormSqlParam.getArtifacts())){
      args.add(ARTIFACTS);
      args.add(stormSqlParam.getArtifacts());
    }

    if (StringUtils.isNotEmpty(stormSqlParam.getArtifactRepositories())){
      args.add(ARTIFACTREPOSITORIES);
      args.add(stormSqlParam.getArtifactRepositories());
    }

    return args;
  }

  private static List<String> shellArgs(StormShellParam stormShellParam) {
    List<String> args = new ArrayList<>();
    args.add(SHELL);

    args.add(stormShellParam.getResources().getRes());

    args.add(stormShellParam.getCommand());

    return args;
  }
}
