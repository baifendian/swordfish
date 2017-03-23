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

import com.baifendian.swordfish.dao.mapper.ProjectMapper;
import com.baifendian.swordfish.dao.mapper.ResourceMapper;
import com.baifendian.swordfish.dao.model.Project;
import com.baifendian.swordfish.dao.model.Resource;
import com.baifendian.swordfish.dao.model.User;
import org.apache.commons.httpclient.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

@Service
public class ResourceService {

  private static Logger logger = LoggerFactory.getLogger(ResourceService.class.getName());

  @Value("${max.file.size}")
  private long maxFileSize;

  @Autowired
  private ResourceMapper resourceMapper;

  @Autowired
  private ProjectService projectService;

  @Autowired
  private ProjectMapper projectMapper;

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
  public Resource createResource(User operator,
                                 String projectName,
                                 String name,
                                 String desc,
                                 MultipartFile file,
                                 HttpServletResponse response) {

    // 判断文件大小是否符合
    if (file.isEmpty() || file.getSize() > maxFileSize * 1024 * 1024) {
      response.setStatus(HttpStatus.SC_REQUEST_TOO_LONG);
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
    Resource resource = resourceMapper.queryResource(name);

    if (resource != null) {
      response.setStatus(HttpStatus.SC_CONFLICT);
      return null;
    }

    // 上传 & 插入数据

    return null;
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
    // 判断文件大小是否符合
    if (file != null && (file.isEmpty() || file.getSize() > maxFileSize * 1024 * 1024)) {
      response.setStatus(HttpStatus.SC_REQUEST_TOO_LONG);
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
    Resource resource = resourceMapper.queryResource(name);

    if (resource != null) {
      response.setStatus(HttpStatus.SC_CONFLICT);
      return null;
    }

    // 上传 & 插入数据
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
}
