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

import static com.baifendian.swordfish.common.job.struct.node.JobType.HQL;
import static com.baifendian.swordfish.common.job.struct.node.JobType.IMPEXP;
import static com.baifendian.swordfish.common.job.struct.node.JobType.MR;
import static com.baifendian.swordfish.common.job.struct.node.JobType.SHELL;
import static com.baifendian.swordfish.common.job.struct.node.JobType.SPARK;
import static com.baifendian.swordfish.common.job.struct.node.JobType.SPARK_STREAMING;
import static com.baifendian.swordfish.common.job.struct.node.JobType.STORM;
import static com.baifendian.swordfish.common.job.struct.node.JobType.VIRTUAL;

import com.baifendian.swordfish.execserver.job.sql.EtlSqlJob;
import com.baifendian.swordfish.execserver.job.impexp.ImpExpJobManager;
import com.baifendian.swordfish.execserver.job.mr.MrJob;
import com.baifendian.swordfish.execserver.job.shell.ShellJob;
import com.baifendian.swordfish.execserver.job.spark.SparkJob;
import com.baifendian.swordfish.execserver.job.storm.StormJob;
import com.baifendian.swordfish.execserver.job.virtual.VirtualJob;
import org.slf4j.Logger;

public class JobManager {

  public static Job newJob(String jobTypeStr, JobProps props, Logger logger)
      throws IllegalArgumentException {
    switch (jobTypeStr) {
      case HQL:
        return new EtlSqlJob(props, false, logger);
      case MR:
        return new MrJob(props, false, logger);
      case SHELL:
        return new ShellJob(props, false, logger);
      case SPARK:
        return new SparkJob(props, false, logger);
      case VIRTUAL:
        return new VirtualJob(props, false, logger);
      case SPARK_STREAMING:
        return new SparkJob(props, true, logger);
      case STORM:
        return new StormJob(props, true, logger);
      case IMPEXP:
        return ImpExpJobManager.newJob(jobTypeStr, props, logger);
      default:
        logger.error("unsupport job type: {}", jobTypeStr);
        throw new IllegalArgumentException("Not support job type");
    }
  }
}
