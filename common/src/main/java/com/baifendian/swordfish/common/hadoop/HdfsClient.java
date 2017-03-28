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
package com.baifendian.swordfish.common.hadoop;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.fs.FileSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class HdfsClient implements Closeable {

  /**
   * LOGGER
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(HdfsClient.class);

  /**
   * HdfsClient 实例
   */
  private static volatile HdfsClient instance;

  /**
   * {@link FileSystem}
   */
  private FileSystem fileSystem;

  /**
   * @param conf
   * @throws HdfsException
   */
  private HdfsClient(Configuration conf) throws HdfsException {
    try {
      fileSystem = FileSystem.get(conf);
    } catch (IOException e) {
      throw new HdfsException("Create HdfsClient failed.", e);
    }
  }

  /**
   * 初始化，仅需调用一次
   *
   * @param conf
   */
  public static void init(Configuration conf) {
    if (instance == null) {
      synchronized (HdfsClient.class) {
        if (instance == null) {
          instance = new HdfsClient(conf);
        }
      }
    }
  }

  /**
   * 获取 HdfsClient 实例 (单例)
   *
   * @return {@link HdfsClient}
   */
  public static HdfsClient getInstance() throws HdfsException {
    if (instance == null) {
      LOGGER.error("Get HdfsClient instance failed，please call init(Configuration conf) first");
      throw new HdfsException("Get HdfsClient instance failed，please call init(Configuration conf) first");
    }

    return instance;
  }

  /**
   * 添加文件到 HDFS 指定目录
   *
   * @param fileName    文件名称
   * @param content     文件内容
   * @param destPath    目标目录
   * @param isOverwrite 当目标文件已经不存在时，是否覆盖
   */
  public void addFile(String fileName, byte[] content, String destPath, boolean isOverwrite) throws HdfsException {
    LOGGER.debug("Begin addFile. fileName: {}, path: {}", fileName, destPath);

    // 创建目标文件路径
    String destFile;
    if (destPath.charAt(destPath.length() - 1) != File.separatorChar) {
      destFile = destPath + File.separatorChar + fileName;
    } else {
      destFile = destPath + fileName;
    }

    // 判断文件是否存在
    Path path = new Path(destFile);

    try {
      // 文件已经存在
      if (fileSystem.exists(path)) {
        if (isOverwrite) { // 覆盖
          fileSystem.delete(path, false);// 先删除目标文件
        } else { // 不覆盖的情况
          LOGGER.error("File " + destFile + " already exists");
          return;
        }
      }
    } catch (IOException e) {
      LOGGER.error("Operator Hdfs exception", e);
      throw new HdfsException("Operator Hdfs exception", e);
    }

    try (
        FSDataOutputStream out = fileSystem.create(path);
        InputStream in = new BufferedInputStream(new ByteArrayInputStream(content));) {
      byte[] b = new byte[1024];
      int numBytes = 0;
      while ((numBytes = in.read(b)) > 0) {
        out.write(b, 0, numBytes);
      }

      out.hflush();
    } catch (IOException e) {
      LOGGER.error("Operator Hdfs exception", e);
      throw new HdfsException("Operator Hdfs exception", e);
    }

    LOGGER.debug("End addFile. fileName:" + fileName + ", path:" + destPath);
  }

  /**
   * 从 hdfs 读取文件内容，并写入到本地文件中
   *
   * @param hdfsFile  hdfs 文件路径
   * @param localFile 本地 文件路径
   * @param overwrite 是否覆盖已存在的本地文件
   */
  public void readFile(String hdfsFile, String localFile, boolean overwrite) throws HdfsException {

    // 文件路径
    Path pathObject = new Path(hdfsFile);
    File fileObject = new File(localFile);

    try {
      if (!isFile(pathObject)) {
        throw new HdfsException("File " + hdfsFile + " is not a valid file");
      }

      // 不覆盖的情况下，已经存在的文件，则不处理
      if (!overwrite && fileObject.exists()) {
        LOGGER.info("{} has exist, do not overwrite", localFile);
        return;
      }

      // 目标文件的目录不存在时，需要创建相关的目录
      File parentPath = fileObject.getParentFile();
      if (parentPath != null && !parentPath.exists()) {
        FileUtils.forceMkdir(parentPath);
      }
    } catch (IOException e) {
      LOGGER.error("Operator Hdfs exception", e);
      throw new HdfsException("Operator Hdfs exception", e);
    }

    try (
        FSDataInputStream in = fileSystem.open(pathObject);
        OutputStream out = new BufferedOutputStream(new FileOutputStream(fileObject));) {
      byte[] b = new byte[1024];
      int numBytes = 0;
      while ((numBytes = in.read(b)) > 0) {
        out.write(b, 0, numBytes);
      }
      out.flush();
    } catch (IOException e) {
      LOGGER.error("Operator Hdfs exception", e);
      throw new HdfsException("Operator Hdfs exception", e);
    }
  }

  /**
   * 从 hdfs 读取文件内容
   *
   * @param hdfsFile hdfs 文件路径
   * @return byte[]
   */
  public byte[] readFile(String hdfsFile) throws HdfsException {
    // 文件路径
    Path pathObject = new Path(hdfsFile);

    try {
      if (!isFile(pathObject)) {
        throw new HdfsException("File " + hdfsFile + " is not a valid file");
      }
    } catch (IOException e) {
      LOGGER.error("Operator Hdfs exception", e);
      throw new HdfsException("Operator Hdfs exception", e);
    }

    try (
        FSDataInputStream in = fileSystem.open(pathObject);) {
      return IOUtils.toByteArray(in);
    } catch (IOException e) {
      LOGGER.error("Operator Hdfs exception", e);
      throw new HdfsException("Operator Hdfs exception", e);
    }
  }

  /**
   * 判断路径是否是一个已经存在的文件
   *
   * @param pathObject
   * @return
   * @throws IOException
   */
  private boolean isFile(Path pathObject) throws IOException {
    if (!fileSystem.exists(pathObject) || fileSystem.isDirectory(pathObject)) {
      return false;
    }

    return true;
  }

  /**
   * 删除目录或文件
   *
   * @param path      hdfs 目录和文件路径
   * @param recursive 当路径表示目录时，是否递归删除子目录
   * @return 是否删除成功
   */
  public boolean delete(String path, boolean recursive) throws HdfsException {
    Path pathObject = new Path(path);

    try {
      return fileSystem.delete(pathObject, recursive);
    } catch (IOException e) {
      LOGGER.error("Delete path exception", e);
      throw new HdfsException("Delete path exception", e);
    }
  }

  /**
   * 重命名目录或文件
   *
   * @param path     hdfs 目录和文件路径
   * @param destPath hdfs 目标路径
   * @return 是否重命名成功
   */
  public boolean rename(String path, String destPath) throws HdfsException {
    Path pathObject = new Path(path);
    Path destPathObject = new Path(destPath);
    try {
      return fileSystem.rename(pathObject, destPathObject);
    } catch (IOException e) {
      LOGGER.error("Rename path exception", e);
      throw new HdfsException("Rename path exception", e);
    }
  }

  /**
   * 创建目录
   *
   * @param dir
   * @throws HdfsException
   */
  public void mkdir(String dir) throws HdfsException {
    Path path = new Path(dir);

    try {
      if (fileSystem.exists(path)) {
        LOGGER.error("Dir {} already exists", dir);
        return;
      }

      fileSystem.mkdirs(path);
    } catch (IOException e) {
      LOGGER.error("Create dir exception", e);
      throw new HdfsException("Create dir exception", e);
    }
  }

  /**
   * copy 一个文件到另一个目标文件
   *
   * @param srcPath      hdfs 源文件
   * @param dstPath      hdfs 目标文件
   * @param deleteSource 是否删除源文件
   * @param overwrite    是否覆盖目标文件
   * @return 是否成功
   */
  public boolean copy(String srcPath, String dstPath, boolean deleteSource, boolean overwrite) throws HdfsException {
    Path srcPathObj = new Path(srcPath);
    Path dstPathObj = new Path(dstPath);

    try {
      return FileUtil.copy(fileSystem, srcPathObj, fileSystem, dstPathObj, deleteSource, overwrite, fileSystem.getConf());
    } catch (IOException e) {
      LOGGER.error("Copy exception", e);
      throw new HdfsException("Copy exception", e);
    }
  }

  /**
   * 本地文件拷贝到 hdfs
   *
   * @param srcPath
   * @param dstPath
   * @param deleteSource
   * @param overwrite
   * @return
   * @throws HdfsException
   */
  public boolean copyLocalToHdfs(String srcPath, String dstPath, boolean deleteSource, boolean overwrite) throws HdfsException {
    Path srcPathObj = new Path(srcPath);
    Path dstPathObj = new Path(dstPath);

    try {
      fileSystem.copyFromLocalFile(deleteSource, overwrite, srcPathObj, dstPathObj);
    } catch (IOException e) {
      LOGGER.error("Copy exception", e);
      throw new HdfsException("Copy exception", e);
    }

    return true;
  }

  /**
   * 拷贝 hdfs 文件到本地
   *
   * @param srcPath
   * @param dstPath
   * @param deleteSource
   * @param overwrite
   * @return
   * @throws HdfsException
   */
  public boolean copyHdfsToLocal(String srcPath, String dstPath, boolean deleteSource, boolean overwrite) throws HdfsException {
    Path srcPathObj = new Path(srcPath);
    File dstFile = new File(dstPath);

    try {
      if (dstFile.exists()) {
        if (dstFile.isFile()) {
          if (overwrite) {
            dstFile.delete();
          }
        } else {
          throw new HdfsException("Destination must not be a dir");
        }
      }

      // hdfs 文件拷贝到本地
      FileUtil.copy(fileSystem, srcPathObj, dstFile, deleteSource, fileSystem.getConf());
    } catch (IOException e) {
      LOGGER.error("Copy exception", e);
      throw new HdfsException("Copy exception", e);
    }

    return true;
  }

  /**
   * 获取文件或目录的逻辑空间大小（单位为 Byte)
   *
   * @param filePath
   * @return 文件或目录的大小
   */
  public long getFileLength(String filePath) {
    return getContentSummary(filePath).getLength();
  }

  /**
   * 判断文件是否存在
   *
   * @param filePath
   * @return
   * @throws IOException
   */
  public boolean exists(String filePath) throws IOException {
    return fileSystem.exists(new Path(filePath));
  }

  /**
   * 获取文件或目录的状态信息
   *
   * @param filePath
   * @return {@link FileStatus}
   */
  public FileStatus getFileStatus(String filePath) {
    Path path = new Path(filePath);
    try {
      return fileSystem.getFileStatus(path);
    } catch (IOException e) {
      LOGGER.error("Get file status exception", e);
      throw new HdfsException("Get file status exception", e);
    }
  }

  /**
   * 获取目录下的文件列表
   *
   * @param filePath
   * @return {@link FileStatus}
   */
  public FileStatus[] listFileStatus(String filePath) {
    Path path = new Path(filePath);
    try {
      return fileSystem.listStatus(new Path(filePath));
    } catch (IOException e) {
      LOGGER.error("Get file list exception", e);
      throw new HdfsException("Get file list exception", e);
    }
  }

  /**
   * 获取文件或目录的内容概要（包括逻辑空间大小、物理空间大小等）
   *
   * @param filePath
   * @return {@link ContentSummary}
   */
  public ContentSummary getContentSummary(String filePath) {
    Path path = new Path(filePath);
    try {
      return fileSystem.getContentSummary(path);
    } catch (IOException e) {
      LOGGER.error("Get file summary information exception", e);
      throw new HdfsException("Get file summary information exception", e);
    }
  }

  /**
   * 获取 hdfs url地址
   *
   * @return url 地址
   */
  public String getUrl() {
    return fileSystem.getUri().toString();
  }

  /**
   * 关闭 hdfs client
   *
   * @throws HdfsException
   */
  @Override
  public void close() throws HdfsException {
    if (fileSystem != null) {
      try {
        fileSystem.close();
      } catch (IOException e) {
        LOGGER.error("Close HdfsClient instance failed", e);
        throw new HdfsException("Close HdfsClient instance failed", e);
      }
    }
  }

  /**
   * 得到 hdfs 空间总容量以及剩余容量信息
   *
   * @return
   * @throws IOException
   */
  public FsStatus getCapacity() throws IOException {
    FsStatus ds = fileSystem.getStatus();
    return ds;
  }

}
