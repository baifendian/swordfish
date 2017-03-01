package com.baifendian.swordfish.dao.mysql.enums;

public enum DbType {
	 /**
     * 0 hive, 1 mysql, 2 mongodb, 3 hbase11, 4 redis
     */
    HIVE,MYSQL,MONGODB,HBASE11,REDIS;

  /**
   *
   * @return
   */
  public Integer getType() {
        return ordinal();
    }

    /**
     * 通过 type 获取枚举对象
     * <p>
     *
     * @param type
     * @return {@link DbType}
     * @throws IllegalArgumentException
     */
    public static DbType valueOfType(Integer type) throws IllegalArgumentException {
        if (type == null) {
            return null;
        }
        try {
            return DbType.values()[type];
        } catch (Exception ex) {
            throw new IllegalArgumentException("Cannot convert " + type + " to " + DbType.class.getSimpleName() + " .", ex);
        }
    }

    /**
     * 判断一个类型是否属于Enum
     * @param type
     * @return
     */
    public static boolean isInEnum(String type) {
        for (DbType dbType: values()) {
            if (dbType.name().equals(type)) {
                return true;
            }
        }

        return false;
    }
}
