package com.smartarchive.security.controller;

import com.smartarchive.common.api.ApiResponse;
import com.smartarchive.security.domain.Role;
import com.smartarchive.security.domain.User;
import com.smartarchive.security.dto.UserDutyProfileResponse;
import com.smartarchive.security.dto.UserRoleConfigResponse;
import com.smartarchive.security.dto.UserRoleSaveCommand;
import com.smartarchive.security.service.SecurityService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/security/user-roles")
@RequiredArgsConstructor
public class UserRoleController {

    private final SecurityService securityService;

    @GetMapping("/users")
    public ApiResponse<List<User>> listUsers() {
        return ApiResponse.success(securityService.listUsers());
    }

    @GetMapping("/users/duty-profile")
    public ApiResponse<UserDutyProfileResponse> dutyProfile(@RequestParam String userName) {
        return ApiResponse.success(securityService.findDutyProfileByUserName(userName));
    }

    @GetMapping("/roles")
    public ApiResponse<List<Role>> listRoles() {
        return ApiResponse.success(securityService.listRoles());
    }

    @GetMapping("/config/{userId}")
    public ApiResponse<UserRoleConfigResponse> getUserRoleConfig(@PathVariable Long userId) {
        return ApiResponse.success(securityService.getUserRoleConfig(userId));
    }

    @PostMapping("/config")
    public ApiResponse<Void> saveUserRoleConfig(@RequestBody UserRoleSaveCommand command) {
        securityService.saveUserRoleConfig(command);
        return ApiResponse.success(null);
    }
}
