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
package com.baifendian.swordfish.execserver.utils;

import com.baifendian.swordfish.common.config.BaseConfig;
import com.baifendian.swordfish.common.hadoop.HdfsClient;
import com.baifendian.swordfish.execserver.utils.OsUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class EnvHelper {

  /**
   * 创建用户和目录
   *
   * @param execLocalPath
   * @param proxyUser
   * @param logger
   * @throws IOException
   */
  public static void workDirAndUserCreate(String execLocalPath, String proxyUser, Logger logger) throws IOException {
    // 如果存在, 首先清除该目录
    File execLocalPathFile = new File(execLocalPath);

    if (execLocalPathFile.exists()) {
      FileUtils.forceDelete(execLocalPathFile);
    }

    // 创建目录
    FileUtils.forceMkdir(execLocalPathFile);

    // proxyUser 用户处理, 如果系统不存在该用户，这里自动创建用户
    List<String> osUserList = OsUtil.getUserList();

    // 不存在, 则创建
    if (!osUserList.contains(proxyUser)) {
      String userGroup = OsUtil.getGroup();
      if (StringUtils.isNotEmpty(userGroup)) {
        logger.info("create os user:{}", proxyUser);

        String cmd = String.format("sudo useradd -g %s %s", userGroup, proxyUser);

        logger.info("exec cmd: {}", cmd);

        OsUtil.exeCmd(cmd);
      }
    }
  }

  /**
   * 拷贝 hdfs 资源到本地中
   *
   * @param projectId
   * @param execLocalPath
   * @param projectRes
   * @param logger
   */
  public static void copyResToLocal(int projectId, String execLocalPath, List<String> projectRes, Logger logger) {
    for (String res : projectRes) {
      File resFile = new File(execLocalPath, res);
      if (!resFile.exists()) {
        String resHdfsPath = BaseConfig.getHdfsResourcesFilename(projectId, res);

        logger.info("get project file:{}", resHdfsPath);

        HdfsClient.getInstance().copyHdfsToLocal(resHdfsPath, execLocalPath + File.separator + res, false, true);
      } else {
        logger.info("file:{} exists, ignore", resFile.getName());
      }
    }
  }
}
