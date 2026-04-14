package com.smartarchive.archivemanage.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ArchiveCreateSessionResponse {
    private Long sessionId;
    private String sessionCode;
    private String createMode;
    private String sessionStatus;
    private String busiModuleCodeGuess;
    private String carrierTypeCodeGuess;
    private String parseStatus;
    private String aiSummarySnapshot;
    private LocalDateTime expireTime;
    private List<ArchiveAttachmentResponse> attachments;
    private ArchiveAiParseResult aiParseResult;
}
