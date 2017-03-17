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

package com.baifendian.swordfish.execserver.job.upload;

/**
 * @author : liujin
 * @date : 2017-03-17 11:39
 */
public class MappingRelation {
  private int originFieldIndex;

  private String targetField;

  public int getOriginFieldIndex() {
    return originFieldIndex;
  }

  public void setOriginFieldIndex(int originFieldIndex) {
    this.originFieldIndex = originFieldIndex;
  }

  public String getTargetField() {
    return targetField;
  }

  public void setTargetField(String targetField) {
    this.targetField = targetField;
  }
}
