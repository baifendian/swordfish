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
package com.baifendian.swordfish.execserver.servlet;

import com.baifendian.swordfish.execserver.ExecServer;
import com.baifendian.swordfish.execserver.flow.FlowRunnerManager;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import static com.baifendian.swordfish.execserver.ExecServer.SERVLET_CONTEXT_KEY;

/**
 * executor servlet
 */
public class ExecutorServlet extends HttpServlet implements ConnectorParams {

  private Logger logger = LoggerFactory.getLogger(ExecutorServlet.class);

  private FlowRunnerManager flowRunnerManager;
  private ExecServer application;

  public static final String JSON_MIME_TYPE = "application/json";

  public ExecutorServlet() {
    super();
  }

  @Override
  public void init(ServletConfig config) throws ServletException {
    application =
            (ExecServer) config.getServletContext().getAttribute(
                    SERVLET_CONTEXT_KEY);

    if (application == null) {
      throw new IllegalStateException(
              "No batch application is defined in the servlet context!");
    }

    flowRunnerManager = application.getFlowRunnerManager();
  }

  protected void writeJSON(HttpServletResponse resp, Object obj)
          throws IOException {
    resp.setContentType(JSON_MIME_TYPE);
    ObjectMapper mapper = new ObjectMapper();
    OutputStream stream = resp.getOutputStream();
    mapper.writeValue(stream, obj);
  }

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp)
          throws ServletException, IOException {
    HashMap<String, Object> respMap = new HashMap<String, Object>();
    // logger.info("ExecutorServer called by " + req.getRemoteAddr());
    try {
      if (!hasParam(req, ACTION_PARAM)) {
        logger.error("Parameter action not set");
        respMap.put("error", "Parameter action not set");
      } else {
        String action = getParam(req, ACTION_PARAM);
        int execid = Integer.parseInt(getParam(req, EXECID_PARAM));
        String user = getParam(req, USER_PARAM, null);

        logger.info("User " + user + " has called action " + action + " on "
                + execid);
        if (action.equals(EXECUTE_ACTION)) {
          handleAjaxExecute(req, respMap, execid);
        } else if (action.equals(CANCEL_ACTION)) {
          logger.info("Cancel called.");
          handleAjaxCancel(respMap, execid, user);
        }
      }
    } catch (Exception e) {
      logger.error("error", e);
      respMap.put(RESPONSE_ERROR, e.getMessage());
    }
    writeJSON(resp, respMap);
    resp.flushBuffer();
  }


  private void handleAjaxExecute(HttpServletRequest req,
                                 Map<String, Object> respMap, int execId) throws ServletException {
    try {
      flowRunnerManager.submitFlow(execId);
    } catch (Exception e) {
      e.printStackTrace();
      logger.error("error", e);
      respMap.put(RESPONSE_ERROR, e.getMessage());
    }
  }

  private void handleAjaxCancel(Map<String, Object> respMap, int execid,
                                String user) throws ServletException {
    if (user == null) {
      respMap.put(RESPONSE_ERROR, "user has not been set");
      return;
    }

    try {
      flowRunnerManager.cancelFlow(execid, user);
      respMap.put(STATUS_PARAM, RESPONSE_SUCCESS);
    } catch (Exception e) {
      logger.error("error", e);
      respMap.put(RESPONSE_ERROR, e.getMessage());
    }
  }

  public boolean hasParam(HttpServletRequest request, String param) {
    return request.getParameter(param) != null;
  }

  public String getParam(HttpServletRequest request, String name)
          throws ServletException {
    String p = request.getParameter(name);
    if (p == null)
      throw new ServletException("Missing required parameter '" + name + "'.");
    else
      return p;
  }

  public String getParam(HttpServletRequest request, String name,
                         String defaultVal) {
    String p = request.getParameter(name);
    if (p == null) {
      return defaultVal;
    }

    return p;
  }
}
