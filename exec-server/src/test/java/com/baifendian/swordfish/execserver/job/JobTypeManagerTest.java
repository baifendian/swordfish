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

import com.baifendian.swordfish.common.job.AbstractJob;
import com.baifendian.swordfish.common.job.Job;
import com.baifendian.swordfish.common.job.JobProps;
import com.baifendian.swordfish.execserver.job.mr.MrJob;
import com.baifendian.swordfish.execserver.job.shell.ShellJob;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author : liujin
 * @date : 2017-03-07 16:17
 */
public class JobTypeManagerTest {

  @Test
  public void testShellJob() throws Exception {
    JobProps props = new JobProps();
    props.setProjectId(1);
    props.setJobParams("{\"value\":\"ls -l\"}");
    Logger logger = LoggerFactory.getLogger(JobTypeManager.class);
    Job job = JobTypeManager.newJob("JOB_1", "SHELL", props, logger);
    assertTrue(ShellJob.class.isInstance(job));
  }

  @Test
  public void testMrJob() throws Exception {
    JobProps props = new JobProps();
    //props.setProperty(AbstractJob.PROJECT_ID, 1);
    String param = "{\"mainJar\":\"hadoop-mapreduce-examples-2.7.3.jar\",\"mainClass\":\"org.apache.hadoop.examples.ExampleDriver\",\"args\":[\"3\",\"5\"],\"properties\":[{\"prop\":\"aa\",\"value\":\"11\"},{\"prop\":\"bb\",\"value\":\"55\"}],\"jars\":[\"3\",\"5\"],\"files\":[\"x.conf\"],\"archives\":null,\"queue\":null,\"dargs\":[\"aa=11\",\"bb=55\"]}";
    System.out.println(param);
    props.setJobParams(param);
    Logger logger = LoggerFactory.getLogger(JobTypeManager.class);
    Job job = JobTypeManager.newJob("JOB_2", "MR", props, logger);
    assertTrue(MrJob.class.isInstance(job));
  }
}
