package com.baifendian.swordfish.webserver.dto.response;

import com.baifendian.swordfish.dao.model.ExecutionFlow;

import java.util.List;

/**
 * Created by caojingwei on 2017/4/12.
 */
public class ExecWorkflowsResponse {
  private int total;
  private int length;
  private List<ExecutionFlow> executions;

  public ExecWorkflowsResponse() {
  }

  public ExecWorkflowsResponse(int total, int length, List<ExecutionFlow> executions) {
    this.total = total;
    this.length = length;
    this.executions = executions;
  }

  public int getTotal() {
    return total;
  }

  public void setTotal(int total) {
    this.total = total;
  }

  public int getLength() {
    return length;
  }

  public void setLength(int length) {
    this.length = length;
  }

  public List<ExecutionFlow> getExecutions() {
    return executions;
  }

  public void setExecutions(List<ExecutionFlow> executions) {
    this.executions = executions;
  }
}
