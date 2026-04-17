package com.smartarchive.archive.command;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;
import lombok.Data;

@Data
public class CreateBorrowOrderDetailCommand {
    private String businessCode;
    private String documentName;
    @NotBlank
    private String company;
    private String documentType;
    private String description;
    private String demandType;
    private Boolean needReturn;
    private LocalDate expectedReturnDate;
    private String lendingApprover;
    private String lendingRemark;
    private String handler;
    private String handlerRemark;
}
