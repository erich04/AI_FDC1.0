package com.smartarchive.archive.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("fdc_borrow_renew_detail_t")
public class BorrowRenewDetail {
    @TableId(value = "borrow_renew_detail_id", type = IdType.AUTO)
    private Long id;
    private Long borrowRenewOrderId;
    private Long sourceDetailId;
    private String businessCode;
    private String documentName;
    private String company;
    private String borrowType;
    private LocalDate borrowTime;
    private LocalDate currentExpireTime;
    private LocalDate renewExpireTime;
    private String renewReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
