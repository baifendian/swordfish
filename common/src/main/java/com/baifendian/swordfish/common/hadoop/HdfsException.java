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

package com.baifendian.swordfish.common.hadoop;

/**
 * Hdfs 异常
 * <p>
 * 
 * @author : dsfan
 * @date : 2016年9月12日
 */
public class HdfsException extends RuntimeException {

    /** Serial version UID */
    private static final long serialVersionUID = -3271763024261592214L;

    /**
     * @param msg
     */
    public HdfsException(String msg) {
        super(msg);
    }

    /**
     * @param msg
     * @param th
     */
    public HdfsException(String msg, Throwable th) {
        super(msg, th);
    }

    /**
     * @param th
     */
    public HdfsException(Throwable th) {
        super(th);
    }
}
