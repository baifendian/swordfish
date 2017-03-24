package com.baifendian.swordfish.webserver.api.service.storage;

import org.apache.commons.io.FileUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FileSystemStorageService implements StorageService {

  /**
   * 将源文件, 放到目的文件中
   *
   * @param file
   * @param destFilename
   */
  @Override
  public void store(MultipartFile file, String destFilename) {
    try {
      if (file.isEmpty()) {
        throw new StorageException("Failed to store empty file " + file.getOriginalFilename());
      }

      File destFile = new File(destFilename);
      File destDir = new File(destFile.getParent());

      if (!destDir.exists()) {
        FileUtils.forceMkdir(destDir);
      }

      Files.copy(file.getInputStream(), Paths.get(destFilename));
    } catch (IOException e) {
      throw new StorageException("Failed to store file " + file.getOriginalFilename(), e);
    }
  }

  @Override
  public Resource loadAsResource(String filename) {
    try {
      Path file = Paths.get(filename);

      Resource resource = new UrlResource(file.toUri());
      if (resource.exists() || resource.isReadable()) {
        return resource;
      } else {
        throw new StorageFileNotFoundException("Could not read file: " + filename);

      }
    } catch (MalformedURLException e) {
      throw new StorageFileNotFoundException("Could not read file: " + filename, e);
    }
  }

  /**
   * 删除目录, 递归
   *
   * @param dir
   */
  @Override
  public void deleteDir(String dir) throws IOException {
    FileUtils.deleteDirectory(new File(dir));
  }

  /**
   * 删除文件
   *
   * @param filename
   */
  @Override
  public void deleteFile(String filename) throws IOException {
    FileUtils.forceDelete(new File(filename));
  }
}
