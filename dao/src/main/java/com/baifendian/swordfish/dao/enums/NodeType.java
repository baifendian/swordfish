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

/**
 * 节点类型
 * <p>
 * 
 * @author : dsfan
 * @date : 2016年8月27日
 */
public enum NodeType {

    /** 短任务：0.数据接入 1.数据导出 2.mr任务 3.sql任务 4.spark短任务 5.虚拟节点 6.SHELL节点*/
    IMPORT(0), EXPORT(1), MR(2), SQL(3), SPARK_BATCH(4), VIRTUAL(5), SHELL(6),

    /** 长任务 */
    SPARK_STREAMING(100),

    /** ETL */
    AGGREGATOR(200), DEDUPLICATION(201), EXPRESSION(202), FILTER(203), INPUT(204), JOINER(205), LOOKUP(206), OUTPUT(207), RANK(208), ROUTER(209), SORTER(210), UNION(211), /*
                                                                                                                                                                            * EXIST
                                                                                                                                                                            * (
                                                                                                                                                                            * 212)
                                                                                                                                                                            */VIRTUAL_INPUT(213), LIMIT(214), /*
                                                                                                                                                                                                              * ETL_EXPORT
                                                                                                                                                                                                              * (
                                                                                                                                                                                                              * 215
                                                                                                                                                                                                              * )
                                                                                                                                                                                                              * ,
                                                                                                                                                                                                              */

    /** 即席查询 */
    ADHOC_SQL(300),

    /** 数据质量 */
    DQ_SQL(400),

    /** ETL 运行 */
    ETL_SQL(500),

    /** 文件导入 */
    FILE_IMPORT_SQL(600);

    /** 语句类型值 */
    private Integer type;

    /**
     * private constructor
     */
    private NodeType(Integer type) {
        this.type = type;
    }

    /**
     * getter method
     * 
     * @see NodeType#type
     * @return the type
     */
    public Integer getType() {
        return type;
    }

    /**
     * 通过 name 获取枚举对象
     * <p>
     *
     * @param name
     * @return {@link NodeType}
     * @throws IllegalArgumentException
     */
    @JsonCreator
    public static NodeType valueOfName(String name) throws IllegalArgumentException {
        if (StringUtils.isEmpty(name)) {
            return null;
        }
        return NodeType.valueOf(name);
    }

    /**
     * 通过 type 获取枚举对象
     * <p>
     *
     * @param type
     * @return {@link NodeType}
     * @throws IllegalArgumentException
     */
    // @JsonCreator
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
     * 判断是否长任务节点
     * <p>
     *
     * @return 是否长任务节点
     */
    public boolean typeIsLong() {
        return this == NodeType.SPARK_STREAMING;
    }
}
