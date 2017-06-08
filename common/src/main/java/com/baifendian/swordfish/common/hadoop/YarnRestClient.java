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

import com.baifendian.swordfish.dao.enums.FlowStatus;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class YarnRestClient {

  private static final Logger logger = LoggerFactory.getLogger(YarnRestClient.class);

  private static YarnRestClient instance;

  private YarnRestClient() throws HdfsException {
  }

  /**
   * 得到 yarn rest client 实例
   *
   * @return
   */
  public static YarnRestClient getInstance() {
    if (instance == null) {
      synchronized (YarnRestClient.class) {
        if (instance == null) {
          instance = new YarnRestClient();
        }
      }
    }

    return instance;
  }

  /**
   * 得到某个应用的状态
   *
   * @param appId
   * @return 返回可能是 null, 也可能是有其它解析异常
   * @throws JSONException
   * @throws IOException
   */
  public FlowStatus getApplicationStatus(String appId) throws JSONException, IOException {
    if (StringUtils.isEmpty(appId)) {
      return null;
    }

    String url = ConfigurationUtil.getApplicationStatusAddress(appId);

    CloseableHttpClient httpclient = HttpClients.createDefault();
    HttpGet httpget = new HttpGet(url);

    ResponseHandler<Pair<Integer, String>> rh = response -> {
      StatusLine statusLine = response.getStatusLine();
      HttpEntity entity = response.getEntity();
      if (statusLine.getStatusCode() >= 300) {
        if (statusLine.getStatusCode() == 404 || statusLine.getStatusCode() == 330) {
          return null;
        }

        throw new HttpResponseException(
            statusLine.getStatusCode(),
            statusLine.getReasonPhrase());
      }

      if (entity == null) {
        throw new ClientProtocolException("Response contains no content");
      }

      String content = EntityUtils.toString(entity);

      JSONObject o = null;
      try {
        o = new JSONObject(content);
      } catch (JSONException e) {
        throw new HttpResponseException(
            statusLine.getStatusCode(),
            content);
      }

      if (!o.has("app") || o.isNull("app")) {
        throw new RuntimeException("Response content not valid " + content);
      }

      try {
        return Pair.of(statusLine.getStatusCode(), o.getJSONObject("app").getString("finalStatus"));
      } catch (JSONException e) {
        throw new HttpResponseException(
            statusLine.getStatusCode(),
            content);
      }
    };

    Pair<Integer, String> pair = httpclient.execute(httpget, rh);

    if (pair == null) {
      return null;
    }

    switch (pair.getRight()) {
      case "FAILED":
        return FlowStatus.FAILED;
      case "KILLED":
        return FlowStatus.KILL;
      case "SUCCEEDED":
        return FlowStatus.SUCCESS;
      case "NEW":
      case "NEW_SAVING":
      case "SUBMITTED":
      case "ACCEPTED":
        return FlowStatus.INIT;
      case "RUNNING":
      default:
        return FlowStatus.RUNNING;
    }
  }

  public static void main(String[] args) throws Exception {
    System.out.println("Status is:" + YarnRestClient.getInstance().getApplicationStatus("application_1494488429167_0050"));
    System.out.println("Status is:" + YarnRestClient.getInstance().getApplicationStatus("application_1493947416024_014222"));
    System.out.println("Status is:" + YarnRestClient.getInstance().getApplicationStatus("application_1494488429167_0047"));
  }
}
