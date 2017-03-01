package com.baifendian.swordfish.dao.mysql.enums;

/**
 * Created by shuanghu
 * date: 17-2-6.
 */
public enum UserStatusType {
    NORMAL(0),
    LOCKED(1),
    DELETED(2);

    private int status;

    UserStatusType(int val){
        this.status = val;
    }

    public Integer getType() {
        return status;
    }

    public static UserStatusType valueOfType(Integer type) throws IllegalArgumentException {
        if (type == null) {
            return null;
        }
        for (UserStatusType statusType : UserStatusType.values()) {
            if (statusType.getType().equals(type)) {
                return statusType;
            }
        }
        throw new IllegalArgumentException("Cannot convert " + type + " to " + UserStatusType.class.getSimpleName() + " .");
    }
}
