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

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.PRECONDITION_FAILED;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.HttpStatus.valueOf;

import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * 异常的处理方式, 抛出的任何异常会进行截获, 并返回相应的提示信息
 */
@ControllerAdvice
public class ControllerExceptionAdvice extends ResponseEntityExceptionHandler {

  /**
   * 重载对于 repuest param 取不到值得异常处理
   */
  @Override
  protected ResponseEntity<Object> handleMissingServletRequestParameter(
      MissingServletRequestParameterException ex, HttpHeaders headers,
      HttpStatus status, WebRequest request) {
    return new ResponseEntity<>(new CustomErrorType(BAD_REQUEST, ex.getMessage()), BAD_REQUEST);
  }

  /**
   * 处理类型不匹配的异常
   */
  @Override
  protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException ex, HttpHeaders headers,
      HttpStatus status, WebRequest request) {
    return new ResponseEntity<>(new CustomErrorType(BAD_REQUEST, ex.getMessage()), BAD_REQUEST);
  }

  /**
   * 处理 controller 异常
   */
  @ExceptionHandler(PreFailedException.class)
  @ResponseBody
  ResponseEntity<?> handleControllerPreFailedException(HttpServletRequest request, Throwable ex) {
    return new ResponseEntity<Object>(new CustomErrorType(PRECONDITION_FAILED, ex.getMessage()),
        PRECONDITION_FAILED);
  }

  /**
   * 处理 controller 异常
   */
  @ExceptionHandler(UnAuthorizedException.class)
  @ResponseBody
  ResponseEntity<?> handleControllerUnAuthorizedException(HttpServletRequest request,
      Throwable ex) {
    return new ResponseEntity<Object>(new CustomErrorType(UNAUTHORIZED, ex.getMessage()),
        UNAUTHORIZED);
  }

  /**
   * 处理 controller 异常
   */
  @ExceptionHandler(BadRequestException.class)
  @ResponseBody
  ResponseEntity<?> handleControllerBadRequestException(HttpServletRequest request, Throwable ex) {
    return new ResponseEntity<Object>(new CustomErrorType(BAD_REQUEST, ex.getMessage()),
        BAD_REQUEST);
  }

  /**
   * 处理 controller 异常
   */
  @ExceptionHandler(NotFoundException.class)
  @ResponseBody
  ResponseEntity<?> handleControllerNotFoundException(HttpServletRequest request, Throwable ex) {
    return new ResponseEntity<Object>(new CustomErrorType(NOT_FOUND, ex.getMessage()), NOT_FOUND);
  }

  /**
   * 处理 controller 异常
   */
  @ExceptionHandler(Exception.class)
  @ResponseBody
  ResponseEntity<?> handleControllerException(HttpServletRequest request, Throwable ex) {
    HttpStatus status = getStatus(request);
    logger.error("controller catch some error", ex);
    return new ResponseEntity<Object>(new CustomErrorType(status, ex.getMessage()), status);
  }

  /**
   * 返回状态
   */
  private HttpStatus getStatus(HttpServletRequest request) {
    Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");

    if (statusCode == null) {
      return INTERNAL_SERVER_ERROR;
    }

    return valueOf(statusCode);
  }
}
