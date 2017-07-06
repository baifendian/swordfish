package com.baifendian.swordfish.execserver.job.impexp;

import com.baifendian.swordfish.common.job.struct.node.BaseParam;
import com.baifendian.swordfish.common.job.struct.node.impexp.setting.Setting;
import com.baifendian.swordfish.dao.utils.json.JsonUtil;
import com.baifendian.swordfish.execserver.job.AbstractYarnProcessJob;
import com.baifendian.swordfish.execserver.job.JobProps;
import com.baifendian.swordfish.execserver.job.impexp.Args.ImpExpProps;
import com.baifendian.swordfish.execserver.job.impexp.Args.ReaderArg;
import com.baifendian.swordfish.execserver.job.impexp.Args.WriterArg;
import com.baifendian.swordfish.execserver.parameter.ParamHelper;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.util.UUID;

import static com.baifendian.swordfish.execserver.job.impexp.ImpExpJobConst.*;

/**
 * DataX 类型任务
 */
public abstract class DataXJob extends AbstractYarnProcessJob {

  protected ImpExpProps impExpProps;

  public DataXJob(JobProps props, boolean isLongJob, Logger logger, ImpExpProps impExpProps) {
    super(props, isLongJob, logger);
    this.impExpProps = impExpProps;
  }

  /**
   * 获取 dataX 的 reader
   */
  public abstract ReaderArg getDataXReaderArg() throws Exception;

  /**
   * 获取 dataX 的 writer
   */
  public abstract WriterArg getDateXWriterArg() throws Exception;

  /**
   * 生成 datax 需要的 json
   */
  public final String getDataXJson() throws Exception {
    logger.info("Start get DataX json ...");

    ReaderArg readerArg = getDataXReaderArg();
    WriterArg writerArg = getDateXWriterArg();
    Setting setting = impExpProps.getImpExpParam().getSetting();
    String readerJson = JsonUtil.toJsonString(readerArg);
    String writerJson = JsonUtil.toJsonString(writerArg);
    String settingJson = JsonUtil.toJsonString(setting);
    String json = MessageFormat
        .format(DATAX_JSON, readerArg.dataxName(), readerJson, writerArg.dataxName(), writerJson,
            settingJson);
    logger.info("Finish get DataX json: {}", json);
    logger.info("Start parameter replacement...");
    json = ParamHelper.resolvePlaceholders(json, props.getDefinedParams());
    logger.info("Finish parameter replacement, json:{}", json);
    return json;
  }

  /**
   * 生成 datax 配置文件
   */
  public final File createDataXParam(String dataXJson) throws Exception {
    // 工作目录
    logger.info("Start create DataX json file...");

    String fileName = DATAX_FILE_NAME + UUID.randomUUID() + ".json";
    String path = MessageFormat.format("{0}/{1}", props.getWorkDir(), fileName);

    logger.info("Datax json file path: {}", path);

    File file = new File(path);

    try {
      FileUtils.writeStringToFile(file, dataXJson, Charset.forName("utf-8"));
    } catch (IOException e) {
      logger.error("Create dataX json file error", e);
      throw e;
    }

    logger.info("Finish create DataX json file!");
    return file;
  }

  /**
   * 生成 dataX 命令
   */
  @Override
  protected final String createCommand() throws Exception {
    logger.info("Start create DataX command...");
    File dataXJson = createDataXParam(getDataXJson());

    String dataxMian = MessageFormat
        .format("{0}/bin/datax.py", impExpProps.getWorkConf().getString("executor.datax.home"));
    String command = MessageFormat.format(COMMAND, dataxMian, dataXJson.getAbsolutePath());

    logger.info("Finish create DataX commond: {}", command);

    return command;
  }

  @Override
  public BaseParam getParam() {
    return impExpProps.getImpExpParam();
  }
}
