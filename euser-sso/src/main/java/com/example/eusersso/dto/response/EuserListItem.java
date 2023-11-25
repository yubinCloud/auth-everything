package com.example.eusersso.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class EuserListItem {

    private String username;

    private String screenName;

    private String mobile;

    private String createdBy;

    private boolean checked;

    private long createTime;

    private String note;

    private List<SimpleRole> avueRoles;

    @Data
    public static class SimpleRole {
        private int roleId;
        private String roleName;

        public static SimpleRole of(int roleId, String roleName) {
            var simpleRole = new SimpleRole();
            simpleRole.setRoleId(roleId);
            simpleRole.setRoleName(roleName);
            return simpleRole;
        }
    }
}
