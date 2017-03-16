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
package com.baifendian.swordfish.common.job.logger;

import org.omg.CORBA.OBJ_ADAPTER;
import org.slf4j.Logger;
import org.slf4j.Marker;

/**
 * @author : liujin
 * @date : 2017-03-03 15:33
 */
public class JobLogger implements Logger {

  private Logger logger;
  private String jobId;

  public JobLogger(String jobId, Logger logger) {
    this.jobId = jobId;
    this.logger = logger;
  }

  private String addJobId(String msg) {
    return String.format("%s %s", jobId, msg);
  }

  @Override
  public String getName() {
    return logger.getName();
  }

  @Override
  public boolean isTraceEnabled() {
    return logger.isTraceEnabled();
  }

  @Override
  public void trace(String msg) {
    logger.trace(addJobId(msg));
  }

  public void trace(String format, Object arg) {
    logger.trace(addJobId(format), arg);
  }

  public void trace(String format, Object arg1, Object arg2) {
    logger.trace(addJobId(format), arg1, arg2);
  }

  public void trace(String format, Object... arguments) {
    logger.trace(addJobId(format), arguments);
  }

  public void trace(String msg, Throwable t) {
    logger.trace(addJobId(msg), t);
  }

  public boolean isTraceEnabled(Marker marker) {
    return logger.isTraceEnabled(marker);
  }

  public void trace(Marker marker, String msg) {
    logger.trace(marker, addJobId(msg));
  }

  public void trace(Marker marker, String format, Object arg) {
    logger.trace(marker, addJobId(format), arg);
  }

  public void trace(Marker marker, String format, Object arg1, Object arg2) {
    logger.trace(marker, addJobId(format), arg1, arg2);
  }

  public void trace(Marker marker, String format, Object... argArray) {
    logger.trace(marker, addJobId(format), argArray);
  }

  public void trace(Marker marker, String msg, Throwable t) {
    logger.trace(marker, addJobId(msg), t);
  }

  public boolean isDebugEnabled() {
    return logger.isDebugEnabled();
  }

  public void debug(String msg) {
    logger.debug(addJobId(msg));
  }

  public void debug(String format, Object arg) {
    logger.debug(addJobId(format), arg);
  }

  public void debug(String format, Object arg1, Object arg2) {
    logger.debug(addJobId(format), arg1, arg2);
  }

  public void debug(String format, Object... arguments) {
    logger.debug(addJobId(format), arguments);
  }

  public void debug(String msg, Throwable t) {
    logger.debug(addJobId(msg), t);
  }

  public boolean isDebugEnabled(Marker marker) {
    return logger.isDebugEnabled();
  }

  public void debug(Marker marker, String msg) {
    logger.debug(marker, addJobId(msg));
  }

  public void debug(Marker marker, String format, Object arg) {
    logger.debug(marker, addJobId(format), arg);
  }

  public void debug(Marker marker, String format, Object arg1, Object arg2) {
    logger.debug(marker, addJobId(format), arg1, arg2);
  }

  public void debug(Marker marker, String format, Object... arguments) {
    logger.debug(marker, addJobId(format), arguments);
  }

  public void debug(Marker marker, String msg, Throwable t) {
    logger.debug(marker, addJobId(msg), t);
  }

  public boolean isInfoEnabled() {
    return logger.isInfoEnabled();
  }

  public void info(String msg) {
    logger.info(addJobId(msg));
  }

  public void info(String format, Object arg) {
    logger.info(addJobId(format), arg);
  }

  public void info(String format, Object arg1, Object arg2) {
    logger.info(addJobId(format), arg1, arg2);
  }

  public void info(String format, Object... arguments) {
    logger.info(addJobId(format), arguments);
  }

  public void info(String msg, Throwable t) {
    logger.info(addJobId(msg), t);
  }

  public boolean isInfoEnabled(Marker marker) {
    return logger.isInfoEnabled();
  }

  public void info(Marker marker, String msg) {
    logger.info(marker, addJobId(msg));
  }

  public void info(Marker marker, String format, Object arg) {
    logger.info(marker, addJobId(format), arg);
  }

  public void info(Marker marker, String format, Object arg1, Object arg2) {
    logger.info(marker, addJobId(format), arg1, arg2);
  }

  public void info(Marker marker, String format, Object... arguments) {
    logger.info(marker, addJobId(format), arguments);
  }

  public void info(Marker marker, String msg, Throwable t) {
    logger.info(marker, addJobId(msg), t);
  }

  public boolean isWarnEnabled() {
    return logger.isWarnEnabled();
  }

  public void warn(String msg) {
    logger.warn(addJobId(msg));
  }

  public void warn(String format, Object arg) {
    logger.warn(addJobId(format), arg);
  }

  public void warn(String format, Object arg1, Object arg2) {
    logger.warn(addJobId(format), arg1, arg2);
  }

  public void warn(String format, Object... arguments) {
    logger.warn(addJobId(format), arguments);
  }

  public void warn(String msg, Throwable t) {
    logger.warn(addJobId(msg), t);
  }

  public boolean isWarnEnabled(Marker marker) {
    return logger.isWarnEnabled();
  }

  public void warn(Marker marker, String msg) {
    logger.warn(marker, addJobId(msg));
  }

  public void warn(Marker marker, String format, Object arg) {
    logger.warn(marker, addJobId(format), arg);
  }

  public void warn(Marker marker, String format, Object arg1, Object arg2) {
    logger.warn(marker, addJobId(format), arg1, arg2);
  }

  public void warn(Marker marker, String format, Object... arguments) {
    logger.warn(marker, addJobId(format), arguments);
  }

  public void warn(Marker marker, String msg, Throwable t) {
    logger.warn(marker, addJobId(msg), t);
  }

  public boolean isErrorEnabled() {
    return logger.isErrorEnabled();
  }

  public void error(String msg) {
    logger.error(addJobId(msg));
  }

  public void error(String format, Object arg) {
    logger.error(addJobId(format), arg);
  }

  public void error(String format, Object arg1, Object arg2) {
    logger.error(addJobId(format), arg1, arg2);
  }

  public void error(String format, Object... arguments) {
    logger.error(addJobId(format), arguments);
  }

  public void error(String msg, Throwable t) {
    logger.error(addJobId(msg), t);
  }

  public boolean isErrorEnabled(Marker marker) {
    return logger.isErrorEnabled();
  }

  public void error(Marker marker, String msg) {
    logger.error(marker, addJobId(msg));
  }

  public void error(Marker marker, String format, Object arg) {
    logger.error(marker, addJobId(format), arg);
  }

  public void error(Marker marker, String format, Object arg1, Object arg2) {
    logger.error(marker, addJobId(format), arg1, arg2);
  }

  public void error(Marker marker, String format, Object... arguments) {
    logger.error(marker, addJobId(format), arguments);
  }

  public void error(Marker marker, String msg, Throwable t) {
    logger.error(marker, addJobId(msg), t);
  }
}
