package com.smartarchive.archive.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDate;
import lombok.Data;

@Data
@TableName("fdc_catalog_task_t")
public class CatalogTask {
    @TableId(value = "catalog_task_id", type = IdType.AUTO)
    private Long id;
    private String taskCode;
    private String archiveCode;
    private String archiveTitle;
    private String taskStatus;
    private String assignee;
    private LocalDate dueDate;
}
