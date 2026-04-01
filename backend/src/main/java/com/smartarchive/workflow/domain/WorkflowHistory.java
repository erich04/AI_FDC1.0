package com.smartarchive.workflow.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("fdc_workflow_history_t")
public class WorkflowHistory {
    @TableId(value = "workflow_history_id", type = IdType.AUTO)
    private Long id;
    private String historyId;
    private String processInstanceId;
    private String taskId;
    private String activityId;
    private String activityName;
    private String type;
    private String assignee;
    private String duration;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String variables;
    private String businessKey;
    @TableLogic(value = "N", delval = "Y")
    private String deleteFlag;
    private Long createdBy;
    private LocalDateTime creationDate;
    private Long lastUpdatedBy;
    private LocalDateTime lastUpdateDate;
}