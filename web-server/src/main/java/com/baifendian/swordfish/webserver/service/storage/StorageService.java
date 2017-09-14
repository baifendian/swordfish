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
package com.baifendian.swordfish.webserver.service.storage;

import java.io.IOException;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface StorageService {

  /**
   * 存储到目的文件
   *
   * @param file
   * @param destFilename
   */
  void store(MultipartFile file, String destFilename);

  /**
   * 加载文件
   *
   * @param filename
   * @return
   */
  Resource loadAsResource(String filename);

  /**
   * 删除目录
   *
   * @param dir
   */
  void deleteDir(String dir) throws IOException;

  /**
   * 删除文件
   *
   * @param filename
   */
  void deleteFile(String filename) throws IOException;
}
