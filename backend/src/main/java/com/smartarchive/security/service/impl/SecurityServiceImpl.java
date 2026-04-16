package com.smartarchive.security.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smartarchive.security.domain.Role;
import com.smartarchive.security.domain.User;
import com.smartarchive.security.domain.UserRoleScope;
import com.smartarchive.security.dto.UserDutyProfileResponse;
import com.smartarchive.security.dto.UserRoleConfigResponse;
import com.smartarchive.security.dto.UserRoleSaveCommand;
import com.smartarchive.security.mapper.RoleMapper;
import com.smartarchive.security.mapper.UserMapper;
import com.smartarchive.security.mapper.UserRoleScopeMapper;
import com.smartarchive.security.service.SecurityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SecurityServiceImpl implements SecurityService {

    private final UserMapper userMapper;
    private final RoleMapper roleMapper;
    private final UserRoleScopeMapper userRoleScopeMapper;

    @Override
    public List<User> listUsers() {
        return userMapper.selectList(new LambdaQueryWrapper<User>().orderByAsc(User::getUserName));
    }

    @Override
    public UserDutyProfileResponse findDutyProfileByUserName(String userName) {
        if (!StringUtils.hasText(userName)) {
            return null;
        }
        String trimmed = userName.trim();
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
            .eq(User::getUserName, trimmed)
            .eq(User::getDeleteFlag, "N")
            .last("LIMIT 1"));
        if (user == null) {
            return null;
        }
        UserDutyProfileResponse response = new UserDutyProfileResponse();
        response.setUserName(user.getUserName());
        response.setDutyDepartment(user.getDutyDepartment());
        response.setWorkCountryCode(user.getWorkCountryCode());
        return response;
    }

    @Override
    public List<Role> listRoles() {
        return roleMapper.selectList(new LambdaQueryWrapper<Role>().eq(Role::getEnableFlag, "Y").orderByAsc(Role::getRoleCode));
    }

    @Override
    public UserRoleConfigResponse getUserRoleConfig(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            return null;
        }

        UserRoleConfigResponse response = new UserRoleConfigResponse();
        response.setUserId(user.getUserId());
        response.setUsername(user.getUserName());
        response.setRealName(user.getUserName());

        List<UserRoleScope> scopes = userRoleScopeMapper.selectList(
                new LambdaQueryWrapper<UserRoleScope>().eq(UserRoleScope::getUserId, userId));

        Map<String, List<UserRoleScope>> roleGroupedScopes = scopes.stream()
                .collect(Collectors.groupingBy(UserRoleScope::getRoleCode));

        List<UserRoleConfigResponse.RoleConfig> roleConfigs = new ArrayList<>();
        List<Role> allRoles = listRoles();

        for (Role role : allRoles) {
            List<UserRoleScope> roleScopes = roleGroupedScopes.get(role.getRoleCode());
            if (roleScopes != null) {
                UserRoleConfigResponse.RoleConfig roleConfig = new UserRoleConfigResponse.RoleConfig();
                roleConfig.setRoleCode(role.getRoleCode());
                roleConfig.setRoleName(role.getRoleName());

                Map<String, List<UserRoleScope>> dimensionGrouped = roleScopes.stream()
                        .filter(s -> s.getDimensionCode() != null)
                        .collect(Collectors.groupingBy(UserRoleScope::getDimensionCode));

                List<UserRoleConfigResponse.ScopeConfig> scopeConfigs = new ArrayList<>();
                dimensionGrouped.forEach((dimCode, dimScopes) -> {
                    UserRoleConfigResponse.ScopeConfig scopeConfig = new UserRoleConfigResponse.ScopeConfig();
                    scopeConfig.setDimensionCode(dimCode);
                    scopeConfig.setValues(dimScopes.stream().map(UserRoleScope::getDimensionValue).collect(Collectors.toList()));
                    scopeConfigs.add(scopeConfig);
                });
                roleConfig.setScopes(scopeConfigs);
                roleConfigs.add(roleConfig);
            }
        }
        response.setRoles(roleConfigs);

        return response;
    }

    @Override
    @Transactional
    public void saveUserRoleConfig(UserRoleSaveCommand command) {
        // 1. 删除旧的配置
        userRoleScopeMapper.delete(new LambdaQueryWrapper<UserRoleScope>().eq(UserRoleScope::getUserId, command.getUserId()));

        // 2. 插入新配置
        if (command.getRoles() != null) {
            for (UserRoleSaveCommand.RoleSaveItem roleItem : command.getRoles()) {
                if (roleItem.getScopes() == null || roleItem.getScopes().isEmpty()) {
                    // 仅角色，无具体维度（全量或不分维度）
                    UserRoleScope scope = new UserRoleScope();
                    scope.setUserId(command.getUserId());
                    scope.setRoleCode(roleItem.getRoleCode());
                    userRoleScopeMapper.insert(scope);
                } else {
                    for (UserRoleSaveCommand.ScopeSaveItem scopeItem : roleItem.getScopes()) {
                        if (scopeItem.getValues() == null || scopeItem.getValues().isEmpty()) {
                            UserRoleScope scope = new UserRoleScope();
                            scope.setUserId(command.getUserId());
                            scope.setRoleCode(roleItem.getRoleCode());
                            scope.setDimensionCode(scopeItem.getDimensionCode());
                            userRoleScopeMapper.insert(scope);
                        } else {
                            for (String val : scopeItem.getValues()) {
                                UserRoleScope scope = new UserRoleScope();
                                scope.setUserId(command.getUserId());
                                scope.setRoleCode(roleItem.getRoleCode());
                                scope.setDimensionCode(scopeItem.getDimensionCode());
                                scope.setDimensionValue(val);
                                userRoleScopeMapper.insert(scope);
                            }
                        }
                    }
                }
            }
        }
    }
}
