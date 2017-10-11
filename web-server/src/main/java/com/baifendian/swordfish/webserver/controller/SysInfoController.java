package com.baifendian.swordfish.webserver.controller;

import com.baifendian.swordfish.common.config.BaseConfig;
import com.baifendian.swordfish.dao.model.User;
import com.baifendian.swordfish.webserver.dto.ExecutorIdDto;
import com.baifendian.swordfish.webserver.dto.IsProhibitDto;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("")
public class SysInfoController {

  private static Logger logger = LoggerFactory.getLogger(SysInfoController.class.getName());

  @GetMapping(value = "/prohibitProxyUsers")
  public Set<String> prohibitProxyUsers(@RequestAttribute(value = "session.user") User operator) {
    logger.info(
        "Operator user {}, prohibitProxyUsers",
        operator.getName());

    return BaseConfig.getProhibitUserSet();
  }

  @GetMapping(value = "/prohibitProxyUsers/isProhibit")
  public IsProhibitDto prohibitProxyUsers(@RequestAttribute(value = "session.user") User operator,
      @RequestParam("proxyUser") String proxyUser) {
    logger.info(
        "Operator user {}, prohibitProxyUsers",
        operator.getName());

    boolean res = BaseConfig.isProhibitUser(proxyUser);

    return new IsProhibitDto(res ? 1 : 0);
  }
}
