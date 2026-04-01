package com.smartarchive.archiveflow.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("fdc_security_level_t")
public class SecurityLevelDictionary {
    @TableId(value = "security_level_id", type = IdType.AUTO)
    private Long id;
    private String securityLevelCode;
    private String securityLevelName;
    private Integer sortOrder;
    @TableField("enable_flag")
    private String enabledFlag;
    @TableLogic(value = "N", delval = "Y")
    private String deleteFlag;
    private Long createdBy;
    private LocalDateTime creationDate;
    private Long lastUpdatedBy;
    private LocalDateTime lastUpdateDate;
}
