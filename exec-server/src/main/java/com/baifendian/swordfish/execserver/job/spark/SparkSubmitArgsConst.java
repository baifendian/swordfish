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

/**
 * spark 提交的参数常量 <p>
 *
 * @author : dsfan
 * @date : 2016年11月9日
 */
public class SparkSubmitArgsConst {

  public static final String MASTER = "--master";

  public static final String DEPLOY_MODE = "--deploy-mode";

  /**
   * --class CLASS_NAME
   */
  public static final String CLASS = "--class";

  /**
   * --conf
   */
  public static final String CONF = "--conf";

  /**
   * --driver-cores NUM
   */
  public static final String DRIVER_CORES = "--driver-cores";

  /**
   * --driver-memory MEM
   */
  public static final String DRIVER_MEMORY = "--driver-memory";

  /**
   * --num-executors NUM
   */
  public static final String NUM_EXECUTORS = "--num-executors";

  /**
   * --executor-cores NUM
   */
  public static final String EXECUTOR_CORES = "--executor-cores";

  /**
   * --executor-memory MEM
   */
  public static final String EXECUTOR_MEMORY = "--executor-memory";

  /**
   * --addJars jars
   */
  public static final String JARS = "--jars";

  /**
   * --files files
   */
  public static final String FILES = "--files";

  /**
   * --archives archives
   */
  public static final String ARCHIVES = "--archives";

  /**
   * --queue QUEUE
   */
  public static final String QUEUE = "--queue";
}
