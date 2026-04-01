package com.smartarchive.archive.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDate;
import lombok.Data;

@Data
@TableName("fdc_inventory_task_t")
public class InventoryTask {
    @TableId(value = "inventory_task_id", type = IdType.AUTO)
    private Long id;
    private String taskCode;
    private String warehouseCode;
    private String inventoryScope;
    private String taskStatus;
    private Integer abnormalCount;
    private String owner;
    private LocalDate dueDate;
}