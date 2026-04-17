package com.smartarchive.companyinfo.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("fdc_company_info_t")
public class CompanyInfo {
    @TableId(value = "company_id", type = IdType.AUTO)
    private Long companyId;
    private String companyCode;
    private String companyName;
    private String region;
    private String representativeOffice;
    private String country;
    private String description;
    private String tags;
    private String enabledFlag;
    @TableLogic(value = "N", delval = "Y")
    private String deleteFlag;
    private Long createdBy;
    private LocalDateTime creationDate;
    private Long lastUpdatedBy;
    private LocalDateTime lastUpdateDate;
}
