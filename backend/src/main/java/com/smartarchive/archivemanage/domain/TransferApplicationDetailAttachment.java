package com.smartarchive.archivemanage.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("fdc_application_detail_attachment_t")
public class TransferApplicationDetailAttachment {
    @TableId(value = "attachment_id", type = IdType.AUTO)
    private Long attachmentId;
    private Long applicationId;
    private Long applicationDetailId;
    private String fileName;
    private String storagePath;
    private String mimeType;
    private Long fileSize;
    private String remark;
    private String deleteFlag;
    private Long createdBy;
    private LocalDateTime creationDate;
    private Long lastUpdatedBy;
    private LocalDateTime lastUpdateDate;
}
