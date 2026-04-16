package com.smartarchive.security.dto;

import lombok.Data;
import java.util.List;

@Data
public class UserRoleConfigResponse {
    private Long userId;
    private String username;
    private String realName;
    private List<RoleConfig> roles;

    @Data
    public static class RoleConfig {
        private String roleCode;
        private String roleName;
        private List<ScopeConfig> scopes;
    }

    @Data
    public static class ScopeConfig {
        private String dimensionCode;
        private List<String> values;
    }
}
