package com.smartarchive.archivemanage.dto;

import java.util.List;
import lombok.Data;

@Data
public class PendingDocumentExportCommand {
    private List<Long> docIds;
    private String exportFileFormat;
    /** DOCUMENT_QUERY | PENDING_ARCHIVE */
    private String exportScope;
}
