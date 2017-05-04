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
package com.baifendian.swordfish.dao.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import org.apache.commons.lang3.StringUtils;

@Deprecated
public enum NodeType {

  /**
   * 短任务：0.数据接入 1.数据导出 2.mr任务 3.sql任务 4.spark短任务 5.虚拟节点 6.SHELL节点
   */
  IMPORT("IMPORT"), EXPORT("EXPORT"), MR("MR"), SQL("SQL"), SPARK_BATCH("SPARK_BATCH"), VIRTUAL("VIRTUAL"), SHELL("SHELL"),

  /**
   * 长任务
   */
  SPARK_STREAMING("SPARK_STREAMING"),

  /**
   * ETL
   */
  AGGREGATOR("AGGREGATOR"), DEDUPLICATION("DEDUPLICATION"), EXPRESSION("EXPRESSION"), FILTER("FILTER"), INPUT("INPUT"), JOINER("JOINER"), LOOKUP("LOOKUP"), OUTPUT("OUTPUT"), RANK("RANK"), ROUTER("ROUTER"), SORTER("SORTER"), UNION("UNION"), /*
                                                                                                                                                                            * EXIST
                                                                                                                                                                            * (
                                                                                                                                                                            * 212)
                                                                                                                                                                            */VIRTUAL_INPUT("VIRTUAL_INPUT"), LIMIT("LIMIT"), /*
                                                                                                                                                                                                              * ETL_EXPORT
                                                                                                                                                                                                              * (
                                                                                                                                                                                                              * 215
                                                                                                                                                                                                              * )
                                                                                                                                                                                                              * ,
                                                                                                                                                                                                              */

  /**
   * 即席查询
   */
  ADHOC_SQL("ADHOC_SQL"),

  /**
   * 数据质量
   */
  DQ_SQL("DQ_SQL"),

  /**
   * ETL 运行
   */
  ETL_SQL("ETL_SQL"),

  /**
   * 文件导入
   */
  FILE_IMPORT_SQL("FILE_IMPORT_SQL");

  /**
   * 语句类型值
   */
  private String type;

  /**
   * private constructor
   */
  private NodeType(String type) {
    this.type = type;
  }

  /**
   * getter method
   *
   * @return the type
   * @see NodeType#type
   */
  public String getType() {
    return type;
  }

  /**
   * 通过 name 获取枚举对象 <p>
   *
   * @return {@link NodeType}
   */
  @JsonCreator
  public static NodeType valueOfName(String name) throws IllegalArgumentException {
    if (StringUtils.isEmpty(name)) {
      return null;
    }
    return NodeType.valueOf(name);
  }

  /**
   * 通过 type 获取枚举对象 <p>
   *
   * @return {@link NodeType}
   */
  public static NodeType valueOfType(Integer type) throws IllegalArgumentException {
    if (type == null) {
      return null;
    }
    for (NodeType nodeType : NodeType.values()) {
      if (nodeType.getType().equals(type)) {
        return nodeType;
      }
    }
    throw new IllegalArgumentException("Cannot convert " + type + " to " + NodeType.class.getSimpleName() + " .");
  }

  /**
   * 判断是否长任务节点 <p>
   *
   * @return 是否长任务节点
   */
  public boolean typeIsLong() {
    return this == NodeType.SPARK_STREAMING;
  }
}
