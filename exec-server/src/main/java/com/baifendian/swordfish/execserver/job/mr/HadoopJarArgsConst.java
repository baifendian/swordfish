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

/**
 * Hadoop jar 参数 <p>
 *
 * @author : dsfan
 * @date : 2016年11月22日
 */
public class HadoopJarArgsConst {

  /**
   * -D
   */
  public static final String D = "-D";

  /**
   * -libjars
   */
  public static final String JARS = "-libjars";

  /**
   * --files files
   */
  public static final String FILES = "--files";

  /**
   * -archives
   */
  public static final String ARCHIVES = "-archives";

  /**
   * -D mapreduce.job.queuename=XXX
   */
  public static final String QUEUE = "mapreduce.job.queuename";
}
