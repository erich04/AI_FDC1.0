package com.smartarchive.archive.command;

import java.time.LocalDate;
import lombok.Data;

@Data
public class CreateBorrowRenewDetailCommand {
    private Long sourceDetailId;
    private String businessCode;
    private String documentName;
    private String company;
    private String borrowType;
    private LocalDate borrowTime;
    private LocalDate currentExpireTime;
    private LocalDate renewExpireTime;
    private String renewReason;
}
