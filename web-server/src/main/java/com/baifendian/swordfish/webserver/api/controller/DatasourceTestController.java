package com.baifendian.swordfish.webserver.api.controller;

import com.baifendian.swordfish.dao.enums.DbType;
import com.baifendian.swordfish.dao.model.User;
import com.baifendian.swordfish.webserver.api.dto.BaseResponse;
import com.baifendian.swordfish.webserver.api.service.DatasourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;


@RestController
@RequestMapping("/datasources")
public class DatasourceTestController {

  @Autowired
  private DatasourceService datasourceService;

  /**
   * 测试一个数据源
   * @param operator
   * @param type
   * @param parameter
   * @param response
   * @return
   */
  @GetMapping(value="/test")
  public BaseResponse testDataSource(@RequestAttribute(value = "session.user") User operator,
                                     @RequestParam(value = "type") String type,
                                     @RequestParam(value = "parameter") String parameter,
                                     HttpServletResponse response){
    return datasourceService.testDataSource(DbType.valueOf(type),parameter,response);
  }
}
