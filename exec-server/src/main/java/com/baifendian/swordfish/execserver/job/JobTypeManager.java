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

package com.baifendian.swordfish.execserver.job;

import com.baifendian.swordfish.common.job.Job;
import com.baifendian.swordfish.common.job.JobProps;
import com.baifendian.swordfish.common.job.exception.ExecException;
import com.baifendian.swordfish.execserver.exception.ConfigException;
//import com.baifendian.swordfish.execserver.job.mr.MrJob;
import com.baifendian.swordfish.execserver.job.hive.AdHocSqlJob;
import com.baifendian.swordfish.execserver.job.hive.EtlSqlJob;
import com.baifendian.swordfish.execserver.job.mr.MrJob;
import com.baifendian.swordfish.execserver.job.process.DefaultProcessJob;
import com.baifendian.swordfish.execserver.job.shell.ShellJob;
import com.baifendian.swordfish.execserver.job.spark.SparkJob;
import com.baifendian.swordfish.execserver.job.upload.UploadJob;
import com.baifendian.swordfish.execserver.utils.CommandUtil;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * job生成工厂类
 *
 * @author : liujin
 * @date : 2017-03-02 10:30
 */
public class JobTypeManager {

  private static Map<String, Class<? extends Job>> jobTypeMap = new HashMap<>();

  static {
    initBaseJobType();
  }

  private static void initBaseJobType() {
    jobTypeMap.put("MR", MrJob.class);
    jobTypeMap.put("SHELL", ShellJob.class);
    jobTypeMap.put("SPARK_BATCH", SparkJob.class);
    jobTypeMap.put("VIRTUAL", NoopJob.class);
    jobTypeMap.put("SQL", EtlSqlJob.class);
    jobTypeMap.put("ADHOC_SQL", AdHocSqlJob.class);
    jobTypeMap.put("ADHOC_SQL", AdHocSqlJob.class);
    jobTypeMap.put("FILE_IMPORT_SQL", UploadJob.class);
  }

  public static void addJobType(String jobType, Class<? extends Job> jobClass) {
    if (jobTypeMap.containsKey(jobType)) {
      throw new ConfigException(String.format("job type {0} is config for {1}", jobType, jobTypeMap.get(jobType)));
    }
    jobTypeMap.put(jobType, jobClass);
  }

  public static Job newJob(String jobId, String jobType, JobProps props, Logger logger) throws IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchMethodException {
    Job job = null;
    Class jobClass = jobTypeMap.get(jobType);
    if (jobClass == null) {
      throw new ExecException("unsupport job type:" + jobType);
    } else {
      Constructor<Job> constructor = jobClass.getConstructor(String.class, JobProps.class, Logger.class);
      job = constructor.newInstance(jobId, props, logger);
    }
    return job;
  }

}
