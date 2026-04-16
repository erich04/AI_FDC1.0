package com.smartarchive.security.dto;

import lombok.Data;
import java.util.List;

@Data
public class UserRoleSaveCommand {
    private Long userId;
    private List<RoleSaveItem> roles;

    @Data
    public static class RoleSaveItem {
        private String roleCode;
        private List<ScopeSaveItem> scopes;
    }

    @Data
    public static class ScopeSaveItem {
        private String dimensionCode;
        private List<String> values;
    }
}
