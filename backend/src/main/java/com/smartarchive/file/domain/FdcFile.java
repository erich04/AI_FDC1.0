package com.smartarchive.file.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("fdc_file_t")
public class FdcFile {
    @TableId(value = "file_id", type = IdType.AUTO)
    private Long fileId;
    private String fileName;
    private String filePath;
    private Long fileSize;
    private String fileType;
    private String sourceSystem;
    private String storagePlatform;
    private String fileMd5;
    private String enableFlag;
    private String deleteFlag;
    private Long createdBy;
    private LocalDateTime creationDate;
}
