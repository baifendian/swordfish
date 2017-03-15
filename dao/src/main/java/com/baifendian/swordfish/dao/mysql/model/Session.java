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

package com.baifendian.swordfish.dao.mysql.model;

import com.baifendian.swordfish.common.utils.json.DateSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.Date;

/**
 * Created by caojingwei on 16/8/19.
 */
public class Session/* implements Serializable */ {
    private String id;

    private String ip;

    private Date startTime;

    private Date endTime;

    private boolean isRemember;

    private User user;

    public Session() {
    }

    public Session(String id, String ip, Date startTime, Date endTime, boolean isRemember, User user) {
        this.id = id;
        this.ip = ip;
        this.startTime = startTime;
        this.endTime = endTime;
        this.isRemember = isRemember;
        this.user = user;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getUserId() {
        return user.getId();
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    @JsonSerialize(using = DateSerializer.class)
    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    @JsonSerialize(using = DateSerializer.class)
    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public boolean isRemember() {
        return isRemember;
    }

    public void setRemember(boolean remember) {
        isRemember = remember;
    }

    public User getUser() {
        return user;
    }

    public Integer getTenantId(){
        return user.getTenantId();
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Session [id=" + id + ", ip=" + ip + ", startTime=" + startTime + ", endTime=" + endTime + ", isRemember=" + isRemember + ", user=" + user
               + "]";
    }

}
