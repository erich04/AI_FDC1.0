package com.smartarchive.archive.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class RenewableBorrowOrderDto {
    private String orderNo;
    private LocalDateTime applyTime;
    private LocalDate borrowTime;
    private LocalDate expireTime;
    private String currentHandler;
    private String applicantName;
}
