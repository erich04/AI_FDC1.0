package com.smartarchive.archivemanage.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TransferApplicationDetailResponse {
    private Long applicationDetailId;
    private Long applicationId;
    private String docBusiNo;
    private String docName;
    private String busiModuleCode;
    private String companyProjectCode;
    private String archPlaceAlpha2Code;
    private String documentOrganizationCode;
    private String endArchPeriod;
    private String startArchPeriod;
    private String archTypeCode;
    private String carrierType;
    private LocalDate docGenerationDate;
    private BigDecimal archCopies;
    private String remark;
    private String description;
    private String catalogVolumeNo;
    private String busiVolumeNo;
    private List<TransferApplicationExtValueResponse> extValues;
    private List<TransferApplicationDetailAttachmentResponse> attachments;
}
