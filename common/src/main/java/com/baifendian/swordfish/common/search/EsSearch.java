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
package com.baifendian.swordfish.common.search;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.cluster.node.DiscoveryNode;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ResourceUtils;

public class EsSearch {

  private static Logger LOGGER = LoggerFactory.getLogger(EsSearch.class.getName());

  private static String esAddress;

  private static Integer esMaxRetryTimeoutMillis;

  private static String esClusterName;

  // 索引文件前缀
  private static String endpoint;

  private static EsSearch instance;

  private final TransportClient client;

  static {
    try {
      File dataSourceFile = ResourceUtils.getFile("classpath:common/search.properties");
      InputStream is = new FileInputStream(dataSourceFile);

      Properties properties = new Properties();
      properties.load(is);

      esAddress = properties.getProperty("es.address");
      esMaxRetryTimeoutMillis = Integer
          .parseInt(properties.getProperty("es.max.retry.timeout.millis"));

      esClusterName = properties.getProperty("es.cluster.name").trim();
      endpoint = properties.getProperty("swordfish.endpoint");
    } catch (IOException e) {
      LOGGER.error(e.getMessage(), e);
    }
  }

  private EsSearch() {
    String[] array = esAddress.split(",");

    Settings settings = Settings.builder()
        .put("client.transport.sniff", false)
        .put("cluster.name", esClusterName)
        .build();

    client = new PreBuiltTransportClient(settings);

    for (int i = 0; i < array.length; ++i) {
      String[] hostPort = array[i].split(":");

      String host = hostPort[0];
      Integer port = Integer.parseInt(hostPort[1]);

      try {
        client
            .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(host), port));

        LOGGER.info("Add (host,port) => ({},{})", host, port);
      } catch (UnknownHostException e) {
        LOGGER.error("Add host,port failed", e);
      }
    }

    for (DiscoveryNode node : client.connectedNodes()) {
      LOGGER.info("Connect nodes: {}", node.toString());
    }
  }

  public static EsSearch getInstance() {
    if (instance == null) {
      synchronized (EsSearch.class) {
        if (instance == null) {
          instance = new EsSearch();
        }
      }
    }

    return instance;
  }

  /**
   *
   */
  public void close() {
    try {
      client.close();
    } catch (Exception e) {
      LOGGER.error("close es client failed", e);
    }
  }

  /**
   * 搜索 job 的信息
   */
  public SearchResponse search(int from, int size, String query, String jobId) throws IOException {
    if (from <= 0) {
      from = 0;
    }

    QueryBuilder queryBuilder;

    if (StringUtils.isNotEmpty(query)) {
      queryBuilder = QueryBuilders.boolQuery()
          .filter(QueryBuilders.termQuery("jobId", jobId.toLowerCase()))
          .must(QueryBuilders.matchQuery("nest_msg", query));
    } else {
      queryBuilder = QueryBuilders.termQuery("jobId", jobId.toLowerCase());
    }

    SearchRequestBuilder builder = client.prepareSearch(endpoint).setQuery(queryBuilder);

    SearchResponse response = builder
        .setTimeout(TimeValue.timeValueMillis(esMaxRetryTimeoutMillis))
        .setFrom(from)
        .setSize(size)
        .addSort("nanoTime", SortOrder.ASC)
        .setExplain(true)
        .get();

    return response;
  }
}
