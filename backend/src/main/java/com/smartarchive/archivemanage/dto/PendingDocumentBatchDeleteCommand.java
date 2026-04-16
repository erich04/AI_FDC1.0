package com.smartarchive.archivemanage.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class PendingDocumentBatchDeleteCommand {
    private List<Long> docIds = new ArrayList<>();
}
