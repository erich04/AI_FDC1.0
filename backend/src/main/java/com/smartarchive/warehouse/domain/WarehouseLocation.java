package com.smartarchive.warehouse.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("fdc_warehouse_location_t")
public class WarehouseLocation {
    @TableId(value = "warehouse_location_id", type = IdType.AUTO)
    private Long id;
    private String warehouseCode;
    private String warehouseName;
    private String areaCode;
    private String shelfCode;
    private String layerCode;
    private String locationCode;
    private String locationName;
    private String status;
    private Integer capacity;
    private Integer occupiedCount;
    private Integer x;
    private Integer y;
    private Integer width;
    private Integer height;
    private BigDecimal utilizationRate;
    @TableField("last_update_date")
    private LocalDateTime lastUpdateDate;

    @TableLogic
    @TableField("delete_flag")
    private String deleteFlag;
}
