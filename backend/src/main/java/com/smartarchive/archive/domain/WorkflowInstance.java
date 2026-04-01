package com.smartarchive.archive.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("fdc_workflow_instance_t")
public class WorkflowInstance {
    @TableId(value = "workflow_instance_id", type = IdType.AUTO)
    private Long id;
    private String instanceCode;
    private String definitionCode;
    private String businessKey;
    private String businessType;
    private String currentNode;
    private String status;
    private LocalDateTime startedAt;
}
