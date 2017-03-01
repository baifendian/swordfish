package com.baifendian.swordfish.dao.mysql.mapper;

import com.baifendian.swordfish.dao.mysql.enums.DbType;
import com.baifendian.swordfish.dao.mysql.model.DataSource;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.EnumOrdinalTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.mybatis.spring.annotation.MapperScan;

import java.sql.Timestamp;
import java.util.List;

/**
 * @auth: ronghua.yu
 * @time: 16/8/9
 * @desc:
 */
@MapperScan
public interface DataSourceMapper {
  /**
   * @param dataSource
   * @return
   */
  @InsertProvider(type = DataSourceMapperProvider.class, method = "insert")
  @SelectKey(statement = "SELECT LAST_INSERT_ID() as id", keyProperty = "dataSource.id", before = false, resultType = Integer.class)
  Integer insert(@Param("dataSource") DataSource dataSource);

  /**
   * @param dataSource
   */
  @UpdateProvider(type = DataSourceMapperProvider.class, method = "update")
  void update(@Param("dataSource") DataSource dataSource);

  /**
   * @param sourceId
   */
  @DeleteProvider(type = DataSourceMapperProvider.class, method = "deleteFromId")
  void deleteFromId(@Param("sourceId") Integer sourceId);

  /**
   * @param id
   * @return
   */
  @SelectProvider(type = DataSourceMapperProvider.class, method = "getById")
  @Results(value = {
      @Result(property = "id", column = "id", id = true, javaType = Integer.class, jdbcType = JdbcType.INTEGER),
      @Result(property = "ownerId", column = "owner_id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
      @Result(property = "projectId", column = "project_id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
      @Result(property = "name", column = "name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
      @Result(property = "desc", column = "desc", javaType = String.class, jdbcType = JdbcType.VARCHAR),
      @Result(property = "type", column = "type", typeHandler = EnumOrdinalTypeHandler.class, javaType = DbType.class, jdbcType = JdbcType.INTEGER),
      // @Result(property = "dbId", column = "db_id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
      @Result(property = "params", column = "params", javaType = String.class, jdbcType = JdbcType.VARCHAR),
      @Result(property = "createTime", column = "create_time", javaType = Timestamp.class, jdbcType = JdbcType.DATE),
      @Result(property = "modifyTime", column = "modify_time", javaType = Timestamp.class, jdbcType = JdbcType.DATE),
  })
  DataSource getById(@Param("id") Integer id);

  /**
   * @param projectId
   * @param name
   * @return
   */
  @SelectProvider(type = DataSourceMapperProvider.class, method = "getByName")
  @Results(value = {
      @Result(property = "id", column = "id", id = true, javaType = Integer.class, jdbcType = JdbcType.INTEGER),
      @Result(property = "ownerId", column = "owner_id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
      @Result(property = "projectId", column = "project_id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
      @Result(property = "name", column = "name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
      @Result(property = "desc", column = "desc", javaType = String.class, jdbcType = JdbcType.VARCHAR),
      @Result(property = "type", column = "type", typeHandler = EnumOrdinalTypeHandler.class, javaType = DbType.class, jdbcType = JdbcType.INTEGER),
      //@Result(property = "dbId", column = "db_id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
      @Result(property = "params", column = "params", javaType = String.class, jdbcType = JdbcType.VARCHAR),
      @Result(property = "createTime", column = "create_time", javaType = Timestamp.class, jdbcType = JdbcType.DATE),
      @Result(property = "modifyTime", column = "modify_time", javaType = Timestamp.class, jdbcType = JdbcType.DATE),
  })
  DataSource getByName(@Param("projectId") Integer projectId, @Param("name") String name);

  /**
   * @param projectId
   * @return
   */
  @SelectProvider(type = DataSourceMapperProvider.class, method = "getByProjectId")
  @Results(value = {
      @Result(property = "id", column = "id", id = true, javaType = Integer.class, jdbcType = JdbcType.INTEGER),
      @Result(property = "ownerId", column = "owner_id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
      @Result(property = "ownerName", column = "owner_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
      @Result(property = "projectId", column = "project_id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
      @Result(property = "name", column = "name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
      @Result(property = "desc", column = "desc", javaType = String.class, jdbcType = JdbcType.VARCHAR),
      @Result(property = "type", column = "type", typeHandler = EnumOrdinalTypeHandler.class, javaType = DbType.class, jdbcType = JdbcType.INTEGER),
      // @Result(property = "dbId", column = "db_id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
      @Result(property = "params", column = "params", javaType = String.class, jdbcType = JdbcType.VARCHAR),
      @Result(property = "createTime", column = "create_time", javaType = Timestamp.class, jdbcType = JdbcType.DATE),
      @Result(property = "modifyTime", column = "modify_time", javaType = Timestamp.class, jdbcType = JdbcType.DATE),
  })
  List<DataSource> getByProjectId(@Param("projectId") Integer projectId);

  /**
   * @param projectId
   * @param type
   * @return
   */
  @SelectProvider(type = DataSourceMapperProvider.class, method = "getByProjectIdAndType")
  @Results(value = {
      @Result(property = "id", column = "id", id = true, javaType = Integer.class, jdbcType = JdbcType.INTEGER),
      @Result(property = "ownerId", column = "owner_id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
      @Result(property = "ownerName", column = "owner_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
      @Result(property = "projectId", column = "project_id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
      @Result(property = "name", column = "name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
      @Result(property = "desc", column = "desc", javaType = String.class, jdbcType = JdbcType.VARCHAR),
      @Result(property = "type", column = "type", typeHandler = EnumOrdinalTypeHandler.class, javaType = DbType.class, jdbcType = JdbcType.INTEGER),
      // @Result(property = "dbId", column = "db_id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
      @Result(property = "params", column = "params", javaType = String.class, jdbcType = JdbcType.VARCHAR),
      @Result(property = "createTime", column = "create_time", javaType = Timestamp.class, jdbcType = JdbcType.DATE),
      @Result(property = "modifyTime", column = "modify_time", javaType = Timestamp.class, jdbcType = JdbcType.DATE),
  })
  List<DataSource> getByProjectIdAndType(@Param("projectId") Integer projectId, @Param("type") Integer type);

  /**
   * 根据项目删除数据源
   *
   * @param projectId
   * @return
   */
  @DeleteProvider(type = DataSourceMapperProvider.class, method = "deleteByProjectId")
  int deleteByProjectId(@Param("projectId") int projectId);
}
