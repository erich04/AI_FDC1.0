package com.smartarchive.archivemanage.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("fdc_arch_paper_t")
public class ArchivePaper {
    @TableId(type = IdType.AUTO)
    private Long paperId;
    private Long archiveId;
    private Integer plannedCopyCount;
    private Integer actualCopyCount;
    private String remark;
    private Long createdBy;
    private LocalDateTime creationDate;
    private Long lastUpdatedBy;
    private LocalDateTime lastUpdateDate;
}
