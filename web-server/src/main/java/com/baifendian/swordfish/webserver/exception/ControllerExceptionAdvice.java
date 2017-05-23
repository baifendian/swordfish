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
package com.baifendian.swordfish.webserver.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import static org.springframework.http.HttpStatus.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 异常的处理方式, 抛出的任何异常会进行截获, 并返回相应的提示信息
 */
@ControllerAdvice
public class ControllerExceptionAdvice extends ResponseEntityExceptionHandler {

  /**
   * 处理 controller 异常
   *
   * @param request
   * @param ex
   * @return
   */
  //@ExceptionHandler(NotModifiedException.class)
  @ResponseBody
  ResponseEntity<?> handleControllerNotModifiedException (HttpServletRequest request, Throwable ex) {
    logger.error("controller catch some error", ex);
    return new ResponseEntity<Object>(new CustomErrorType(NOT_MODIFIED, ex.getMessage()), NOT_MODIFIED);
  }


  /**
   * 处理 controller 异常
   *
   * @param request
   * @param ex
   * @return
   */
  @ExceptionHandler(Exception.class)
  @ResponseBody
  ResponseEntity<?> handleControllerException (HttpServletRequest request, Throwable ex) {
    HttpStatus status = getStatus(request);
    logger.error("controller catch some error", ex);
    return new ResponseEntity<Object>(new CustomErrorType(NOT_MODIFIED, ex.getMessage()), NOT_MODIFIED);
  }


  /**
   * 返回状态
   *
   * @param request
   * @return
   */
  private HttpStatus getStatus(HttpServletRequest request) {
    Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
    if (statusCode == null) {
      return INTERNAL_SERVER_ERROR;
    }

    return valueOf(statusCode);
  }
}
