package com.smartarchive.security.service;

import com.smartarchive.security.domain.Role;
import com.smartarchive.security.domain.User;
import com.smartarchive.security.dto.UserDutyProfileResponse;
import com.smartarchive.security.dto.UserRoleConfigResponse;
import com.smartarchive.security.dto.UserRoleSaveCommand;
import java.util.List;

public interface SecurityService {
    List<User> listUsers();
    List<Role> listRoles();
    UserRoleConfigResponse getUserRoleConfig(Long userId);
    void saveUserRoleConfig(UserRoleSaveCommand command);

    /** 按登录名精确匹配（忽略首尾空格），用于创建页带出部门与工作国家 */
    UserDutyProfileResponse findDutyProfileByUserName(String userName);
}
