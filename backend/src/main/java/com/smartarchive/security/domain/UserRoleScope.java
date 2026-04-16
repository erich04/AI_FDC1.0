package com.smartarchive.security.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("fdc_user_role_scope_t")
public class UserRoleScope {
    @TableId(value = "user_role_scope_id", type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String roleCode;
    private String dimensionCode;
    private String dimensionValue;
    
    private Long createdBy;
    private LocalDateTime creationDate;
    private Long lastUpdatedBy;
    private LocalDateTime lastUpdateDate;
}
