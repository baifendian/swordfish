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
package com.baifendian.swordfish.webserver.api.service;

import com.baifendian.swordfish.common.config.BaseConfig;
import com.baifendian.swordfish.common.hadoop.HdfsClient;
import com.baifendian.swordfish.common.utils.CommonUtil;
import com.baifendian.swordfish.dao.mapper.ProjectMapper;
import com.baifendian.swordfish.dao.mapper.ResourceMapper;
import com.baifendian.swordfish.dao.model.Project;
import com.baifendian.swordfish.dao.model.Resource;
import com.baifendian.swordfish.dao.model.User;
import com.baifendian.swordfish.webserver.api.service.storage.FileSystemStorageService;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@Service
public class ResourceService {

  private static Logger logger = LoggerFactory.getLogger(ResourceService.class.getName());

  @Autowired
  private ResourceMapper resourceMapper;

  @Autowired
  private ProjectService projectService;

  @Autowired
  private ProjectMapper projectMapper;

  @Autowired
  private FileSystemStorageService fileSystemStorageService;

  /**
   * 创建资源
   *
   * @param operator
   * @param projectName
   * @param name
   * @param desc
   * @param file
   * @param response
   * @return
   */
  @Transactional(value = "TransactionManager")
  public Resource createResource(User operator,
                                 String projectName,
                                 String name,
                                 String desc,
                                 MultipartFile file,
                                 HttpServletResponse response) {

    // 文件为空
    if (file.isEmpty()) {
      response.setStatus(HttpStatus.SC_BAD_REQUEST);
      return null;
    }

    // 判断是否具备相应的权限, 必须具备写权限
    Project project = projectMapper.queryByName(projectName);

    if (project == null) {
      response.setStatus(HttpStatus.SC_BAD_REQUEST);
      return null;
    }

    if (!projectService.hasWritePerm(operator.getId(), project)) {
      response.setStatus(HttpStatus.SC_UNAUTHORIZED);
      return null;
    }

    // 判断文件是否已经存在
    Resource resource = resourceMapper.queryResource(project.getId(), name);

    if (resource != null) {
      response.setStatus(HttpStatus.SC_CONFLICT);
      return null;
    }

    // 保存到本地
    String fileSuffix = CommonUtil.fileSuffix(file.getOriginalFilename()); // file suffix
    String filename = StringUtils.isEmpty(fileSuffix) ? name : String.format("%s.%s", name, fileSuffix);

    String localFilename = BaseConfig.getLocalResourceFilename(project.getId(), filename);

    fileSystemStorageService.store(file, localFilename);

    // 保存到 hdfs
    String hdfsFilename = BaseConfig.getHdfsResourcesFilename(project.getId(), filename);

    HdfsClient.getInstance().copyLocalToHdfs(localFilename, hdfsFilename, true, true);

    // 插入数据
    resource = new Resource();

    Date now = new Date();

    resource.setName(name);
    if (StringUtils.isNotEmpty(fileSuffix)) {
      resource.setSuffix(fileSuffix);
    }
    resource.setOriginFilename(file.getOriginalFilename());
    resource.setDesc(desc);
    resource.setOwnerId(operator.getId());
    resource.setOwner(operator.getName());
    resource.setProjectId(project.getId());
    resource.setProjectName(projectName);
    resource.setCreateTime(now);
    resource.setModifyTime(now);

    int count = resourceMapper.insert(resource);

    if (count == 1) {
      response.setStatus(HttpStatus.SC_CREATED);
      return resource;
    }

    response.setStatus(HttpStatus.SC_CONFLICT);
    return null;
  }

  /**
   * 修改资源, 不存在则创建
   *
   * @param operator
   * @param projectName
   * @param name
   * @param desc
   * @param file
   * @param response
   * @return
   */
  public Resource putResource(User operator,
                              String projectName,
                              String name,
                              String desc,
                              MultipartFile file,
                              HttpServletResponse response) {
    Resource resource = resourceMapper.queryByProjectAndResName(projectName, name);

    if (resource == null) {
      return createResource(operator, projectName, name, desc, file, response);
    }

    return modifyResource(operator, projectName, name, desc, file, response);
  }

  /**
   * 修改资源
   *
   * @param operator
   * @param projectName
   * @param name
   * @param desc
   * @param file
   * @param response
   * @return
   */
  public Resource modifyResource(User operator,
                                 String projectName,
                                 String name,
                                 String desc,
                                 MultipartFile file,
                                 HttpServletResponse response) {
    // 文件为空
    if (file != null && file.isEmpty()) {
      response.setStatus(HttpStatus.SC_BAD_REQUEST);
      return null;
    }

    // 判断是否具备相应的权限
    Project project = projectMapper.queryByName(projectName);

    if (project == null) {
      response.setStatus(HttpStatus.SC_BAD_REQUEST);
      return null;
    }

    if (!projectService.hasWritePerm(operator.getId(), project)) {
      response.setStatus(HttpStatus.SC_UNAUTHORIZED);
      return null;
    }

    // 判断文件是否已经存在
    Resource resource = resourceMapper.queryResource(project.getId(), name);

    if (resource != null) {
      response.setStatus(HttpStatus.SC_CONFLICT);
      return null;
    }

    // 上传 & 插入数据
    return null;
  }

  /**
   * 拷贝资源
   *
   * @param operator
   * @param projectName
   * @param srcResName
   * @param destResName
   * @param desc
   * @param response
   * @return
   */
  public Resource copyResource(User operator,
                               String projectName,
                               String srcResName,
                               String destResName,
                               String desc,
                               HttpServletResponse response) {
    return null;
  }

  /**
   * 删除资源
   *
   * @param operator
   * @param projectName
   * @param name
   * @param response
   * @return
   */
  public Resource deleteResource(User operator,
                                 String projectName,
                                 String name,
                                 HttpServletResponse response) {
    // 删除资源 & 数据库记录
    return null;
  }

  /**
   * 下载资源文件
   *
   * @param operator
   * @param projectName
   * @param name
   * @param response
   * @return
   */
  public org.springframework.core.io.Resource downloadResource(User operator,
                                                               String projectName,
                                                               String name,
                                                               HttpServletResponse response) {
    // 判断是否具备相应的权限, 必须具备读权限
    Project project = projectMapper.queryByName(projectName);

    if (project == null) {
      response.setStatus(HttpStatus.SC_BAD_REQUEST);
      return null;
    }

    if (!projectService.hasReadPerm(operator.getId(), project)) {
      response.setStatus(HttpStatus.SC_UNAUTHORIZED);
      return null;
    }

    // 下载文件
    Resource resource = resourceMapper.queryResource(project.getId(), name);

    if (resource == null) {
      logger.error("Download file not exist, project {}, resource {}", projectName, name);
      return null;
    }

    String filename = StringUtils.isEmpty(resource.getSuffix()) ? name : String.format("%s.%s", name, resource.getSuffix());

    String localFilename = BaseConfig.getLocalResourceFilename(project.getId(), filename);
    String hdfsFilename = BaseConfig.getHdfsResourcesFilename(project.getId(), filename);

    HdfsClient.getInstance().copyHdfsToLocal(hdfsFilename, localFilename, false, true);

    org.springframework.core.io.Resource file = fileSystemStorageService.loadAsResource(localFilename);

    return file;
  }
}
