package com.smartarchive.security.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smartarchive.security.domain.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
