package com.smartarchive.archivemanage.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PendingDocumentRowResponse {
    private String docId;
    private String businessCode;
    private String companyEntity;
    private String businessModule;
    private String startPeriod;
    private String endPeriod;
    private String archivePlace;
    private String originPlace;
    private String docOrganization;
    private String docStatus;
    private String documentName;
    private String docGenerationDate;
    private String owner;
    private String responsibleDept;
    private String carrierType;
    private String visibility;
    private String sourceSystem;
    /** 字典编码 */
    private String securityLevelCode;
    /** 中文展示名 */
    private String securityLevelName;
    /** 与 securityLevelName 相同，兼容旧列字段 */
    private String securityLevel;
    private String description;
    private String creationTime;
    private String createdBy;
    private String updatedBy;
    private String updatedAt;
}
