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
package com.baifendian.swordfish.execserver.job.upload;

import com.baifendian.swordfish.common.job.AbstractProcessJob;
import com.baifendian.swordfish.common.job.BaseParam;
import com.baifendian.swordfish.common.job.JobProps;
import com.baifendian.swordfish.common.job.exception.ExecException;
import com.baifendian.swordfish.common.utils.json.JsonUtil;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.metastore.HiveMetaStoreClient;
import org.apache.hadoop.hive.metastore.api.Table;
import org.slf4j.Logger;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author : liujin
 * @date : 2017-03-16 10:38
 */
public class UploadJob extends AbstractProcessJob {
  private UploadParam param;

  private Configuration conf;

  private static final String DEFAULT_CHARSET = "UTF-8";

  public UploadJob(String jobId, JobProps props, Logger logger) throws IOException, ConfigurationException {
    super(jobId, props, logger);

    conf = new PropertiesConfiguration("job/hive.properties");
  }

  @Override
  public void initJobParams() {
    this.param = JsonUtil.parseObject(props.getJobParams(), UploadParam.class);
  }

  @Override
  public ProcessBuilder createProcessBuilder() throws Exception {
    String sourceFile = getWorkingDirectory() + "/" + param.getFile();
    String outFile = getWorkingDirectory() + "/" + jobId + ".data";
    String targetFile = sourceFile;

    // 文件标题行和字符集处理
    if (param.isHasTitle() || !param.getCoding().equalsIgnoreCase(DEFAULT_CHARSET)) {
      try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(sourceFile), param.getCoding()));
           Writer writer = new BufferedWriter(new FileWriter(outFile))) {
        if (param.isHasTitle()) {
          reader.readLine();
        }
        char[] buf = new char[4096];
        int i;
        while (-1 != (i = reader.read(buf))) {
          writer.write(buf, 0, i);
        }
      }
      targetFile = outFile;
    }

    int maxColNum = param.getMappingRelation().stream().mapToInt(p -> p.getOriginFieldIndex()).max().getAsInt();

    String tempTableName = jobId;
    StringBuilder sb = new StringBuilder();
    String tempCreateTableSql = genTempCreateTableSql(tempTableName, param.getSeparator(), maxColNum);
    sb.append(tempCreateTableSql);
    sb.append(";");
    sb.append(String.format("LOAD DATA LOCAL INPATH '%s' OVERWRITE INTO TABLE %s;\n", targetFile, tempTableName));
    sb.append("INSERT ");
    if (param.getWriteMode().equalsIgnoreCase("OVERWRITE")) {
      sb.append("OVERWRITE ");
    } else {
      sb.append("INTO ");
    }
    if (StringUtils.isEmpty(param.getTargetDB())) {
      sb.append(String.format("TABLE %s ", param.getTargetTable()));
    } else {
      sb.append(String.format("TABLE %s.%s ", param.getTargetDB(), param.getTargetTable()));
    }

    // 根据hive表meta数据生成查询语句
    HiveConf hiveConf = new HiveConf();
    hiveConf.set(HiveConf.ConfVars.METASTOREURIS.name(), conf.getString(HiveConf.ConfVars.METASTOREURIS.name()));
    HiveMetaStoreClient hiveMetaStoreClient = new HiveMetaStoreClient(hiveConf);
    Table table = hiveMetaStoreClient.getTable(param.getTargetDB(), param.getTargetTable());
    List<String> cols = table.getSd().getCols().stream().map(p -> p.getName()).collect(Collectors.toList());
    List<String> selectStatement = new ArrayList<>();
    Map<String, String> colMappings = new HashMap<>();
    Set<String> colSet = new HashSet<>(cols);
    List<String> notFoundCols = new ArrayList<>();
    for (UploadParam.MappingRelation rel : param.getMappingRelation()) {
      if (!colSet.contains(rel.getTargetField())) {
        notFoundCols.add(rel.getTargetField());
      }
      colMappings.put(rel.getTargetField(), "c" + rel.getOriginFieldIndex());
    }
    if (!notFoundCols.isEmpty()) {
      throw new ExecException(String.format("%s.%s not found columns:%s",
              param.getTargetDB(), param.getTargetTable(), StringUtils.join(notFoundCols, ",")));
    }

    for (String colName : cols) {
      if (!colMappings.containsKey(colName)) {
        selectStatement.add("null");
      } else {
        selectStatement.add(colMappings.get(colName));
      }
    }
    sb.append("SELECT " + StringUtils.join(selectStatement, ",") + " FROM ");
    sb.append(tempTableName + ";\n");

    String tempSqlFile = getWorkingDirectory() + "/" + jobId + ".hql";
    FileUtils.writeStringToFile(new File(tempSqlFile), sb.toString());

    // 创建 ProcessBuilder
    ProcessBuilder processBuilder = new ProcessBuilder();
    processBuilder.command("hive -f " + tempSqlFile);
    return processBuilder;
  }

  public String genTempCreateTableSql(String tableName, String sep, int maxColNum) {
    StringBuilder sb = new StringBuilder();
    sb.append("CREATE TEMPORARY TABLE " + tableName);
    sb.append("(\n");
    sb.append(IntStream.range(0, maxColNum).mapToObj(i -> String.format("c%d string", i)).collect(Collectors.joining(",\n")));
    sb.append(")\n");
    sb.append("ROW FORMAT DELIMITED FIELDS TERMINATED BY '" + sep + "'\n");
    sb.append("STORED AS TEXTFILE\n");
    return sb.toString();
  }

  @Override
  public BaseParam getParam() {
    return param;
  }
}
