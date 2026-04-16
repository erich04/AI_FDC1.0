package com.smartarchive.security.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smartarchive.security.domain.Role;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RoleMapper extends BaseMapper<Role> {
}
