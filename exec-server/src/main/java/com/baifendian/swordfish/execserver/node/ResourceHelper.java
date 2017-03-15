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

package com.baifendian.swordfish.execserver.node;

import com.baifendian.swordfish.common.hadoop.HdfsClient;
import com.baifendian.swordfish.dao.DaoFactory;
import com.baifendian.swordfish.dao.FlowDao;
import com.baifendian.swordfish.dao.mysql.model.flow.params.ResInfo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 资源获取
 * <p>
 * 
 * @author : dsfan
 * @date : 2016年11月23日
 */
public class ResourceHelper {

    /** LOGGER */
    private static final Logger LOGGER = LoggerFactory.getLogger(ResourceHelper.class);

    /** 执行的根目录 */
    public static final String EXECUTION_BASEPATH = "exec";

    /** {@link FlowDao} */
    private static FlowDao flowDao = DaoFactory.getDaoInstance(FlowDao.class);

    /** hdfs client */
    private static HdfsClient hdfsClient = HdfsClient.getInstance();

    /** hdfs url */
    private static String hdfsBaseUrl = hdfsClient.getUrl();

    /**
     * 获取执行的根路径（相对路径）
     * <p>
     *
     * @return 执行的根目录
     */
    public static String getExecBasePath() {
        return EXECUTION_BASEPATH;
    }

    /**
     * 获取执行的本地路径（相对路径）
     * <p>
     *
     * @param execId
     * @return 执行的本地路径
     */
    public static String getExecLocalPath(long execId) {
        String path = EXECUTION_BASEPATH + "/" + execId + "/";
        File file = new File(path);
        if (!file.exists()) {
            try {
                FileUtils.forceMkdir(file);
            } catch (IOException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
        return path;
    }

    /**
     * 获取执行的本地路径（绝对路径）
     * <p>
     *
     * @param execId
     * @return 执行的本地路径
     */
    public static String getExecAbsoluteLocalPath(long execId) {
        String path = System.getProperty("user.dir") + "/" + EXECUTION_BASEPATH + "/" + execId + "/";
        File file = new File(path);
        if (!file.exists()) {
            try {
                FileUtils.forceMkdir(file);
            } catch (IOException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
        return path;
    }

    /**
     * 清除执行目录
     * <p>
     *
     * @param execId
     */
    public static void cleanExecutionDir(long execId) {
        String path = EXECUTION_BASEPATH + "/" + execId;
        File file = new File(path);
        if (file.exists()) { // 如果目录存在
            try {
                FileUtils.deleteDirectory(file);
            } catch (IOException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }

    /**
     * 获取资源的本地路径
     * <p>
     *
     * @param resourceId
     * @param execId
     * @return 资源的本地路径
     */
    public static String getResourceLocalPath(int resourceId, long execId, boolean isPub) {
        String hdfsFile = getResourceHdfsPath(resourceId, isPub);
        String localFile = genLocalFilePath(hdfsFile, execId);
        hdfsClient.readFile(hdfsFile, localFile, false); // 读取 hdfs 文件
        return localFile;
    }

    /**
     * 获取资源的本地 paths
     * <p>
     *
     * @param resInfos
     * @param execId
     * @return 资源的本地 paths
     */
    public static List<String> getResourceLocalPaths(List<ResInfo> resInfos, long execId, boolean isPub) {
        if (CollectionUtils.isEmpty(resInfos)) {
            return null;
        }
        List<String> urList = new ArrayList<>();
        for (ResInfo resInfo : resInfos) {
            urList.add(getResourceLocalPath(resInfo.getId(), execId, isPub));
        }
        return urList;
    }

    /**
     * 获取资源的本地 paths（有别名的情况）
     * <p>
     *
     * @param resInfos
     * @param execId
     * @return 资源的本地 paths
     */
    public static List<String> getResourceLocalPathsWithAlias(List<ResInfo> resInfos, long execId, boolean isPub) {
        if (CollectionUtils.isEmpty(resInfos)) {
            return null;
        }
        List<String> urList = new ArrayList<>();
        for (ResInfo resInfo : resInfos) {
            if (StringUtils.isNotEmpty(resInfo.getAlias())) {
                urList.add(getResourceHdfsPath(resInfo.getId(), isPub) + "#" + resInfo.getAlias());
            } else {
                urList.add(getResourceHdfsPath(resInfo.getId(), isPub));
            }
        }
        return urList;
    }

    /**
     * 生成本地文件路径
     * <p>
     *
     * @param hdfsFile
     * @param execId
     * @return 本地文件路径
     */
    private static String genLocalFilePath(String hdfsFile, long execId) {
        String fileName = hdfsFile.substring(hdfsFile.lastIndexOf("/") + 1);
        return EXECUTION_BASEPATH + "/" + execId + "/" + fileName;
    }

    /**
     * 获取资源的 hdfs urls
     * <p>
     *
     * @param resInfos
     * @return 资源的 hdfs urls
     */
    public static List<String> getResourceHdfsUrls(List<ResInfo> resInfos, boolean isPub) {
        if (CollectionUtils.isEmpty(resInfos)) {
            return null;
        }
        List<String> urList = new ArrayList<>();
        for (ResInfo resInfo : resInfos) {
            urList.add(getResourceHdfsUrl(resInfo.getId(), isPub));
        }
        return urList;
    }

    /**
     * 获取资源的 hdfs urls（有别名的情况）
     * <p>
     *
     * @param resInfos
     * @return 资源的 hdfs urls
     */
    public static List<String> getResourceHdfsUrlsWithAlias(List<ResInfo> resInfos, boolean isPub) {
        if (CollectionUtils.isEmpty(resInfos)) {
            return null;
        }
        List<String> urList = new ArrayList<>();
        for (ResInfo resInfo : resInfos) {
            if (StringUtils.isNotEmpty(resInfo.getAlias())) {
                urList.add(getResourceHdfsUrl(resInfo.getId(), isPub) + "#" + resInfo.getAlias());
            } else {
                urList.add(getResourceHdfsUrl(resInfo.getId(), isPub));
            }
        }
        return urList;
    }

    /**
     * 获取资源的 hdfs url
     * <p>
     *
     * @param resourceId
     * @return 资源的 hdfs url
     */
    public static String getResourceHdfsUrl(int resourceId, boolean isPub) {
        return hdfsBaseUrl + getResourceHdfsPath(resourceId, isPub);
    }

    /**
     * 获取资源的 hdfs paths
     * <p>
     *
     * @param resInfos
     * @return 资源的 hdfs paths
     */
    public static List<String> getResourceHdfsPaths(List<ResInfo> resInfos, boolean isPub) {
        if (CollectionUtils.isEmpty(resInfos)) {
            return null;
        }
        List<String> urList = new ArrayList<>();
        for (ResInfo resInfo : resInfos) {
            urList.add(getResourceHdfsPath(resInfo.getId(), isPub));
        }
        return urList;
    }

    /**
     * 获取资源的 hdfs paths（有别名的情况）
     * <p>
     *
     * @param resInfos
     * @return 资源的 hdfs paths
     */
    public static List<String> getResourceHdfsPathsWithAlias(List<ResInfo> resInfos, boolean isPub) {
        if (CollectionUtils.isEmpty(resInfos)) {
            return null;
        }
        List<String> urList = new ArrayList<>();
        for (ResInfo resInfo : resInfos) {
            if (StringUtils.isNotEmpty(resInfo.getAlias())) {
                urList.add(getResourceHdfsPath(resInfo.getId(), isPub) + "#" + resInfo.getAlias());
            } else {
                urList.add(getResourceHdfsPath(resInfo.getId(), isPub));
            }
        }
        return urList;
    }

    /**
     * 获取资源的 hdfs path
     * <p>
     *
     * @param resourceId
     * @param isPub
     * @return 资源的 hdfs path
     */
    public static String getResourceHdfsPath(int resourceId, boolean isPub) {
        return flowDao.queryResourceHdfsPath(resourceId, isPub);
    }
}
