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
package com.baifendian.swordfish.dao;

import com.baifendian.swordfish.dao.datasource.ConnectionFactory;
import com.baifendian.swordfish.dao.enums.FlowStatus;
import com.baifendian.swordfish.dao.mapper.AdHocMapper;
import com.baifendian.swordfish.dao.mapper.AdHocResultMapper;
import com.baifendian.swordfish.dao.model.AdHoc;
import com.baifendian.swordfish.dao.model.AdHocResult;
import java.util.Date;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

public class AdHocDao extends BaseDao {
  @Autowired
  private AdHocMapper adHocMapper;

  @Autowired
  private AdHocResultMapper adHocResultMapper;

  @Override
  protected void init() {
    adHocMapper = ConnectionFactory.getSqlSession().getMapper(AdHocMapper.class);
    adHocResultMapper = ConnectionFactory.getSqlSession().getMapper(AdHocResultMapper.class);
  }

  /**
   * 更新即席信息
   *
   * @param adHoc
   * @return
   */
  public boolean updateAdHoc(AdHoc adHoc) {
    return adHocMapper.update(adHoc) > 0;
  }

  /**
   * 更新即席查询的状态
   *
   * @param adHoc
   * @return
   */
  public boolean updateAdHocStatus(AdHoc adHoc) {
    return adHocMapper.updateStatus(adHoc) > 0;
  }

  /**
   * 查询信息
   *
   * @param id
   * @return
   */
  public AdHoc getAdHoc(int id) {
    return adHocMapper.selectById(id);
  }

  /**
   * 更新结果
   *
   * @param adHocResult
   * @return
   */
  public boolean updateAdHocResult(AdHocResult adHocResult) {
    return adHocResultMapper.update(adHocResult) > 0;
  }

  /**
   * 初始化结果
   *
   * @param execId
   * @param execSqls
   */
  @Transactional(value = "TransactionManager")
  public void initAdHocResult(int execId, List<String> execSqls) {
    if (CollectionUtils.isNotEmpty(execSqls)) {
      adHocResultMapper.delete(execId);

      int index = 0;

      for (String stm : execSqls) {
        AdHocResult adHocResult = new AdHocResult();
        adHocResult.setExecId(execId);
        adHocResult.setStm(stm);
        adHocResult.setIndex(index);
        adHocResult.setStatus(FlowStatus.INIT);
        adHocResult.setCreateTime(new Date());

        ++index;

        adHocResultMapper.insert(adHocResult);
      }
    }
  }
}
