package com.baifendian.swordfish.dao.mysql.enums;

/**
 * Created by shuanghu
 * date: 17-2-6.
 */
public enum UserRoleType {
    SUPER_USER(1),
    ADMIN_USER(2),
    GENERAL_USER(3);

    private int role;
    UserRoleType(int val){
        this.role = val;
    }

    public Integer getType() {
        return role;
    }

    public static UserRoleType valueOfType(Integer type) throws IllegalArgumentException {
        if (type == null) {
            return null;
        }
        for (UserRoleType roleType : UserRoleType.values()) {
            if (roleType.getType().equals(type)) {
                return roleType;
            }
        }
        throw new IllegalArgumentException("Cannot convert " + type + " to " + UserRoleType.class.getSimpleName() + " .");
    }
}
