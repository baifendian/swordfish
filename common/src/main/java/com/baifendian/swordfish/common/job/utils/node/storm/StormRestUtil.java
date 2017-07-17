package com.baifendian.swordfish.common.job.utils.node.storm;

import com.baifendian.swordfish.common.config.BaseConfig;
import com.baifendian.swordfish.common.job.struct.node.storm.dto.*;
import com.baifendian.swordfish.dao.utils.json.JsonUtil;
import org.apache.avro.data.Json;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.StringUtils;
import org.apache.http.client.fluent.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Storm rest 服务API
 */
public class StormRestUtil {

  private static final Logger LOGGER = LoggerFactory.getLogger(BaseConfig.class);

  private static PropertiesConfiguration stormConf;

  private static String stormRestUrl;

  static {
    try {
      stormConf = new PropertiesConfiguration("common/storm.properties");
      stormRestUrl = stormConf.getString("storm.rest.url");
    } catch (ConfigurationException e) {
      LOGGER.error(e.getMessage(), e);
      System.exit(1);
    }
  }

  private final static String topologySummary = "/api/v1/topology/summary";

  private final static String topologyInfo = "/api/v1/topology/{0}";

  private final static String topologyKill = "/api/v1/topology/{0}/kill/{1}";

  private final static String topologyDeactivate = "/api/v1/topology/{0}/deactivate";

  private final static String topologyRebalance = "/api/v1/topology/{0}/rebalance/{1}";

  /**
   * 获取 topologySummary url
   *
   * @return
   */
  private static String getTopologySummaryUrl() {
    return stormRestUrl + topologySummary;
  }

  /**
   * 获取 查询任务详细信息的 url
   *
   * @param topologyId
   * @return
   */
  private static String getTopologyInfoUrl(String topologyId) {
    return stormRestUrl + MessageFormat.format(topologyInfo, topologyId);
  }

  /**
   * 获取杀死一个任务的url
   *
   * @param topologyId
   * @param waitTime
   * @return
   */
  private static String getTopologyKillUrl(String topologyId, long waitTime) {
    return stormRestUrl + MessageFormat.format(topologyKill, topologyId, waitTime);
  }

  /**
   * 获取暂停一个任务rul
   *
   * @param topologyId
   * @return
   */
  private static String getTopologyDeactivateUrl(String topologyId) {
    return stormRestUrl + MessageFormat.format(topologyDeactivate, topologyId);
  }

  /**
   * 恢复一个任务
   *
   * @param topologyId
   * @param waitTime
   * @return
   */
  private static String getTopologyRebalanceUrl(String topologyId, long waitTime) {
    return stormRestUrl + MessageFormat.format(topologyRebalance, topologyId, waitTime);
  }

  /**
   * 获取所有当前正在运行的任务的信息
   *
   * @return
   * @throws IOException
   */
  public static TopologySummaryDto getTopologySummary() throws IOException {
    String res = Request.Get(getTopologySummaryUrl())
            .execute().returnContent().toString();

    return JsonUtil.parseObject(res, TopologySummaryDto.class);
  }

  /**
   * 通过名称获取ID,只能的当前正在运行的任务的ID
   *
   * @param topologyName
   * @return
   */
  public static String getTopologyId(String topologyName) throws IOException {
    return getTopologySummary().getTopologies().stream().filter(t -> StringUtils.equals(t.getName(), topologyName)).findFirst().get().getId();
  }


  /**
   * 通过Id获取任务详细信息
   *
   * @param topologyId
   * @return
   */
  public static TopologyInfoDto getTopologyInfo(String topologyId) throws IOException {
    String res = Request.Get(getTopologyInfoUrl(topologyId))
            .execute().returnContent().toString();

    return JsonUtil.parseObject(res, TopologyInfoDto.class);
  }

  /**
   * 获取一个任务所有的log
   *
   * @param topologyId
   */
  public static List<String> getTopologyLogs(String topologyId) throws Exception {
    List<String> logs = new ArrayList<>();
    TopologyInfoDto topologyInfo = getTopologyInfo(topologyId);
    for (TopologyWorkerDto work : topologyInfo.getWorkers()) {
      logs.add(work.getWorkerLogLink());
    }
    return logs;
  }


  /**
   * kill 一个任务
   *
   * @param topologyId
   * @param waitTime
   * @return
   * @throws IOException
   */
  public static void topologyKill(String topologyId, long waitTime) throws Exception {
    String res = Request.Post(getTopologyKillUrl(topologyId, waitTime)).execute().returnContent().toString();

    TopologyOperationDto topologyOperation = JsonUtil.parseObject(res, TopologyOperationDto.class);

    if (topologyOperation == null) {
      throw new Exception("kill not result return!");
    }

    if (!StringUtils.equalsIgnoreCase(topologyOperation.getStatus(), "success")) {
      String msg = MessageFormat.format("Kill status not equal success: {0}", topologyOperation.getStatus());
      throw new Exception(msg);
    }
  }

  /**
   * 暂停一个任务
   *
   * @param topologyId
   * @return
   * @throws IOException
   */
  public static void topologyDeactivate(String topologyId) throws Exception {
    String res = Request.Post(getTopologyDeactivateUrl(topologyId)).execute().returnContent().toString();

    TopologyOperationDto topologyOperation = JsonUtil.parseObject(res, TopologyOperationDto.class);

    if (topologyOperation == null) {
      throw new Exception("Deactivate not result return!");
    }

    if (!StringUtils.equalsIgnoreCase(topologyOperation.getStatus(), "success")) {
      String msg = MessageFormat.format("Deactivate status not equal success: {0}", topologyOperation.getStatus());
      throw new Exception(msg);
    }
  }


  /**
   * 恢复一个任务
   *
   * @param topologyId
   * @param waitTime
   * @return
   * @throws IOException
   */
  public static void topologyRebalance(String topologyId, long waitTime) throws Exception {
    String res = Request.Post(getTopologyRebalanceUrl(topologyId, waitTime)).execute().returnContent().toString();

    TopologyOperationDto topologyOperation = JsonUtil.parseObject(res, TopologyOperationDto.class);

    if (topologyOperation == null) {
      throw new Exception("Rebalance not result return!");
    }

    if (!StringUtils.equalsIgnoreCase(topologyOperation.getStatus(), "success")) {
      String msg = MessageFormat.format("Rebalance status not equal success: {0}", topologyOperation.getStatus());
      throw new Exception(msg);
    }
  }
}
