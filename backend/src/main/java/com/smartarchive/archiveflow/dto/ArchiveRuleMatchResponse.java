package com.smartarchive.archiveflow.dto;

import lombok.Builder;
import lombok.Data;

/**
 * 归档规则匹配结果：默认规则且已启用的归档流向规则。
 */
@Data
@Builder
public class ArchiveRuleMatchResponse {
    /** 是否命中至少一条符合条件的规则（含自定义条件评分后最优） */
    private boolean matched;
    private String companyProjectCode;
    private String companyName;
    private String busiModuleCode;
    private String busiModuleName;
    /** 命中规则上的自定义匹配条件（可能为空） */
    private String customRule;
    private String archiveDestination;
    private String archiveDestinationName;
    private String documentOrganizationCode;
    private String documentOrganizationName;
    private Integer retentionPeriodYears;
    /** 与规则表 external_display_flag 一致：Y/N */
    private String visibleFlag;
    /** 是否可见的中文展示：是/否 */
    private String visibilityLabel;
}
