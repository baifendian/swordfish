package com.baifendian.swordfish.execserver.job.shell;

import com.baifendian.swordfish.common.job.BaseParam;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;

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
  private String value;

  /**
   * @return
   */
  public String getValue() {
    return value;
  }

  /**
   * @param value
   */
  public void setValue(String value) {
    this.value = value;
  }

  public List<String> getResourceNames() {
    if (StringUtils.isEmpty(value)) {
      return Collections.emptyList();
    }

    Matcher matcher = PATTERN_RESOURCE_RULE_MATCHES.matcher(value);

    List<String> resourceNames = new ArrayList<>();

    while (matcher.find()) {
      resourceNames.add(matcher.group(1));
    }

    return resourceNames;
  }

  public static void main(String[] args) {
    ShellParam shellParam = new ShellParam();

    shellParam.setValue("This order was placed for # --@resource_reference{sss.jbb} QT3000! OK? --@resource_reference{bbb.jbb}");

    for (String resource : shellParam.getResourceNames()) {
      System.out.println("resource is: " + resource);
    }

    shellParam.setValue("ls -l\npwd");

    for (String resource : shellParam.getResourceNames()) {
      System.out.println("resource is: " + resource);
    }
  }
}
