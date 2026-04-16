package com.smartarchive.countryregion.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("fdc_country_region_t")
public class CountryRegion {
    @TableId(value = "region_id", type = IdType.AUTO)
    private Long regionId;
    private String isoAlpha2;
    private String isoAlpha3;
    private String isoNumeric;
    private String countryNameZh;
    private String countryNameEn;
    private String regionLevel;
    private String regionCode;
    private String regionName;
    private String shortName;
    private String parentRegionCode;
    private Integer sortOrder;
    @TableField("enable_flag")
    private String enabledFlag;
    @TableLogic(value = "N", delval = "Y")
    private String deleteFlag;
    private Long createdBy;
    private LocalDateTime creationDate;
    private Long lastUpdatedBy;
    private LocalDateTime lastUpdateDate;
    private Long tenantid;
}
