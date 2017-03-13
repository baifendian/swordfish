package com.baifendian.swordfish.execserver.job.shell;

import com.baifendian.swordfish.common.job.BaseParam;
import com.baifendian.swordfish.execserver.job.ResourceInfo;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

import static com.baifendian.swordfish.common.utils.http.HttpUtil.PATTERN_RESOURCE_RULE_MATCHES;

/**
 * author: smile8
 * date:   15/12/2016
 * desc:
 */
public class ShellParam extends BaseParam {

  /**
   * LOGGER
   */
  private final Logger LOGGER = LoggerFactory.getLogger(getClass());

  /**
   * 原始 shell 语句
   */
  private String script;

  private List<ResourceInfo> resources;

  @Override
  public boolean checkValid(){
    return script != null && !script.isEmpty();
  }

  public String getScript() {
    return script;
  }

  public void setScript(String script) {
    this.script = script;
  }

  public List<ResourceInfo> getResources() {
    return resources;
  }

  public void setResources(List<ResourceInfo> resources) {
    this.resources = resources;
  }

  @Override
  public List<String> getResourceFiles(){
    if(resources != null) {
      return resources.stream().map(p -> p.getRes()).collect(Collectors.toList());
    } else {
      return null;
    }
  }
}
