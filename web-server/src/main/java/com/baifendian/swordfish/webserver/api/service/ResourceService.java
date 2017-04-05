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
import java.util.List;
import java.util.UUID;

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
   * 创建资源:<br>
   * 1) 先下载资源到本地, 为了避免并发问题, 随机生成一个文件名称<br>
   * 2) 拷贝到 hdfs 中, 给予合适的文件名称(包含后缀)<br>
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

    upload(project, name, file);

    // 插入数据
    resource = new Resource();
    Date now = new Date();

    resource.setName(name);

    // 设置后缀
    String fileSuffix = CommonUtil.fileSuffix(file.getOriginalFilename()); // file suffix
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

    if (count <= 0) {
      response.setStatus(HttpStatus.SC_NOT_MODIFIED);
      return null;
    }

    response.setStatus(HttpStatus.SC_CREATED);
    return resource;
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
  @Transactional(value = "TransactionManager")
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

    // 判断资源是否已经存在, 如果不存在, 直接返回
    Resource resource = resourceMapper.queryResource(project.getId(), name);

    if (resource == null) {
      response.setStatus(HttpStatus.SC_NOT_MODIFIED);
      return null;
    }

    Date now = new Date();

    if (file != null) {
      resource.setOriginFilename(file.getOriginalFilename());
      // 上传文件
      upload(project, name, file);
    }

    if (desc != null) {
      resource.setDesc(desc);
    }

    resource.setOwnerId(operator.getId());
    resource.setModifyTime(now);

    int count = resourceMapper.update(resource);

    if (count <= 0) {
      response.setStatus(HttpStatus.SC_NOT_MODIFIED);
      return null;
    }

    return resource;
  }

  /**
   * 上传文件到 hdfs 中
   *
   * @param project
   * @param name
   * @param file
   */
  private void upload(Project project, String name, MultipartFile file) {
    // 保存到本地
    String fileSuffix = CommonUtil.fileSuffix(file.getOriginalFilename()); // file suffix
    String filename = StringUtils.isEmpty(fileSuffix) ? name : String.format("%s.%s", name, fileSuffix); // destination filename

    String localFilename = BaseConfig.getLocalResourceFilename(project.getId(), UUID.randomUUID().toString()); // 随机的一个文件名称

    fileSystemStorageService.store(file, localFilename);

    // 保存到 hdfs 并删除源文件
    String hdfsFilename = BaseConfig.getHdfsResourcesFilename(project.getId(), filename);
    HdfsClient.getInstance().copyLocalToHdfs(localFilename, hdfsFilename, true, true);
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
  @Transactional(value = "TransactionManager")
  public Resource copyResource(User operator,
                               String projectName,
                               String srcResName,
                               String destResName,
                               String desc,
                               HttpServletResponse response) {

    // 源和目标不能是一样的名称
    if (StringUtils.endsWithIgnoreCase(srcResName, destResName)) {
      logger.error("Src resource mustn't the same to dest resource.");
      response.setStatus(HttpStatus.SC_NOT_MODIFIED);
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

    // 判断源是否存在
    Resource srcResource = resourceMapper.queryResource(project.getId(), srcResName);

    if (srcResource == null) {
      response.setStatus(HttpStatus.SC_NOT_MODIFIED);
      return null;
    }

    // 判断目标是否存在, 不存在则需要创建
    Resource destResource = resourceMapper.queryResource(project.getId(), destResName);

    Date now = new Date();

    // 不存在
    if (destResource == null) {
      destResource = new Resource();

      destResource.setName(destResName);
      destResource.setSuffix(srcResource.getSuffix());

      destResource.setOriginFilename(srcResource.getOriginFilename());
      destResource.setDesc(desc);
      destResource.setOwnerId(operator.getId());
      destResource.setOwner(operator.getName());
      destResource.setProjectId(project.getId());
      destResource.setProjectName(projectName);
      destResource.setCreateTime(now);
      destResource.setModifyTime(now);

      int count = resourceMapper.insert(destResource);

      if (count <= 0) {
        response.setStatus(HttpStatus.SC_NOT_MODIFIED);
        return null;
      }
    } else { // 存在
      if (!StringUtils.equalsIgnoreCase(srcResource.getSuffix(), destResource.getSuffix())) {
        logger.error("Src resource suffix must the same to dest resource suffix.");
        response.setStatus(HttpStatus.SC_NOT_MODIFIED);
        return null;
      }

      if (desc != null) {
        destResource.setDesc(desc);
      }

      destResource.setOwnerId(operator.getId());
      destResource.setModifyTime(now);

      resourceMapper.update(destResource);
    }

    // 开始拷贝文件
    String srcFilename = StringUtils.isEmpty(srcResource.getSuffix()) ? srcResName : String.format("%s.%s", srcResName, srcResource.getSuffix()); // destination filename
    String srcHdfsFilename = BaseConfig.getHdfsResourcesFilename(project.getId(), srcFilename);

    String destFilename = StringUtils.isEmpty(srcResource.getSuffix()) ? destResName : String.format("%s.%s", destResName, srcResource.getSuffix());
    String destHdfsFilename = BaseConfig.getHdfsResourcesFilename(project.getId(), destFilename);

    HdfsClient.getInstance().copy(srcHdfsFilename, destHdfsFilename, false, true);

    return destResource;
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
  @Transactional(value = "TransactionManager")
  public void deleteResource(User operator,
                             String projectName,
                             String name,
                             HttpServletResponse response) {

    // 判断是否具备相应的权限
    Project project = projectMapper.queryByName(projectName);

    if (project == null) {
      response.setStatus(HttpStatus.SC_BAD_REQUEST);
      return;
    }

    if (!projectService.hasWritePerm(operator.getId(), project)) {
      response.setStatus(HttpStatus.SC_UNAUTHORIZED);
      return;
    }

    // 查询先
    Resource resource = resourceMapper.queryResource(project.getId(), name);

    if (resource == null) {
      return;
    }

    // 删除数据库
    resourceMapper.delete(resource.getId());

    // 删除资源信息
    String filename = StringUtils.isEmpty(resource.getSuffix()) ? name : String.format("%s.%s", name, resource.getSuffix()); // filename
    String hdfsFilename = BaseConfig.getHdfsResourcesFilename(project.getId(), filename);

    HdfsClient.getInstance().delete(hdfsFilename, false);

    return;
  }

  /**
   * 得到资源列表
   *
   * @param operator
   * @param projectName
   * @param response
   * @return
   */
  public List<Resource> getResources(User operator, String projectName, HttpServletResponse response) {

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

    // 查询资源
    List<Resource> resources = resourceMapper.queryResourceDetails(project.getId());

    return resources;
  }

  /**
   * 得到某个资源的向详情信息
   *
   * @param operator
   * @param projectName
   * @param name
   * @param response
   * @return
   */
  public Resource getResource(User operator, String projectName, String name, HttpServletResponse response) {

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

    // 查询资源
    Resource resource = resourceMapper.queryResourceDetail(project.getId(), name);

    if (resource == null) {
      response.setStatus(HttpStatus.SC_NOT_FOUND);
      return null;
    }

    return resource;
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

    String localFilename = BaseConfig.getLocalDownloadFilename(project.getId(), filename);
    String hdfsFilename = BaseConfig.getHdfsResourcesFilename(project.getId(), filename);

    HdfsClient.getInstance().copyHdfsToLocal(hdfsFilename, localFilename, false, true);

    org.springframework.core.io.Resource file = fileSystemStorageService.loadAsResource(localFilename);

    return file;
  }
}
