package com.smartarchive.security.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("fdc_role_t")
public class Role {
    @TableId(value = "role_id", type = IdType.AUTO)
    private Long roleId;
    private String roleCode;
    private String roleName;
    private String roleNameEn;
    private String description;
    @TableField("enable_flag")
    private String enableFlag;
    
    private Long createdBy;
    private LocalDateTime creationDate;
    private Long lastUpdatedBy;
    private LocalDateTime lastUpdateDate;
}
