package com.baifendian.swordfish.dao;

import com.baifendian.swordfish.dao.datasource.ConnectionFactory;
import com.baifendian.swordfish.dao.mapper.DataSourceMapper;
import com.baifendian.swordfish.dao.model.DataSource;

/**
 * 资源 DAO
 */
public class DatasourceDao extends BaseDao {
  private DataSourceMapper dataSourceMapper;

  @Override
  protected void init() {
    dataSourceMapper = ConnectionFactory.getSqlSession().getMapper(DataSourceMapper.class);
  }

  /**
   * 根据projectId 和 资源name查询一个数据源
   * @param projectId
   * @param name
   * @return
   */
  public DataSource queryResource(int projectId, String name) {
    return dataSourceMapper.getByName(projectId, name);
  }
}
