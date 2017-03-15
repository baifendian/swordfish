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

package com.baifendian.swordfish.common.hive;

import com.google.common.base.Objects;

/**
 * Created by wenting on 9/8/16.
 */
public class ConnectionInfo {

    private String user;

    private String password;

    private String uri;

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof ConnectionInfo) {
            ConnectionInfo that = (ConnectionInfo) other;
            return Objects.equal(this.user, that.user) && this.password == that.password && Objects.equal(this.uri, that.uri);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(user, password, uri);
    }


    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}
