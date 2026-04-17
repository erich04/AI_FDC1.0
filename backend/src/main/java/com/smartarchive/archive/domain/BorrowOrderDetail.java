package com.smartarchive.archive.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("fdc_borrow_order_detail_t")
public class BorrowOrderDetail {
    @TableId(value = "borrow_order_detail_id", type = IdType.AUTO)
    private Long id;
    private Long borrowOrderId;
    private String businessCode;
    private String documentName;
    private String company;
    private String documentType;
    private String detailDescription;
    private String demandType;
    private String needReturn;
    private LocalDate expectedReturnDate;
    private String lendingApprover;
    private String lendingRemark;
    private String handler;
    private String handlerRemark;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
