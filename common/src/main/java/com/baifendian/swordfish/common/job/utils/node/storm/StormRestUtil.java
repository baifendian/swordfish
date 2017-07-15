package com.baifendian.swordfish.common.job.utils.node.storm;

import com.baifendian.swordfish.common.config.BaseConfig;
import com.baifendian.swordfish.common.job.struct.node.storm.dto.TopologyDto;
import com.baifendian.swordfish.common.job.struct.node.storm.dto.TopologyInfoDto;
import com.baifendian.swordfish.common.job.struct.node.storm.dto.TopologyOperationDto;
import com.baifendian.swordfish.common.job.struct.node.storm.dto.TopologySummaryDto;
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
   * kill 一个任务
   *
   * @param topologyId
   * @param waitTime
   * @return
   * @throws IOException
   */
  public static TopologyOperationDto topologyKill(String topologyId, long waitTime) throws IOException {
    String res = Request.Post(getTopologyKillUrl(topologyId, waitTime)).execute().returnContent().toString();

    return JsonUtil.parseObject(res, TopologyOperationDto.class);
  }

  /**
   * 暂停一个任务
   *
   * @param topologyId
   * @return
   * @throws IOException
   */
  public static TopologyOperationDto topologyDeactivate(String topologyId) throws IOException {
    String res = Request.Post(getTopologyDeactivateUrl(topologyId)).execute().returnContent().toString();

    return JsonUtil.parseObject(res, TopologyOperationDto.class);
  }

  /**
   * 恢复一个任务
   * @param topologyId
   * @param waitTime
   * @return
   * @throws IOException
   */
  public static TopologyOperationDto topologyRebalance(String topologyId, long waitTime) throws IOException {
    String res = Request.Post(getTopologyRebalanceUrl(topologyId, waitTime)).execute().returnContent().toString();
    return JsonUtil.parseObject(res, TopologyOperationDto.class);
  }
}
