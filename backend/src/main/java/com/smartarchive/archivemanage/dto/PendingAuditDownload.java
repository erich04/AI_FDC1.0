package com.smartarchive.archivemanage.dto;

import org.springframework.core.io.Resource;

public record PendingAuditDownload(Resource resource, String fileName, String contentType) {}
