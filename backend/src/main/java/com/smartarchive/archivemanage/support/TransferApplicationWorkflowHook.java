package com.smartarchive.archivemanage.support;

import com.smartarchive.archivemanage.service.TransferApplicationArchiveMaterializationService;
import com.smartarchive.workflow.domain.WorkflowInstance;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TransferApplicationWorkflowHook {

    private final TransferApplicationArchiveMaterializationService transferApplicationArchiveMaterializationService;
    private static final String TRANSFER_BUSINESS_KEY_PREFIX = "TRN-APP-";

    public void onProcessFinished(WorkflowInstance instance, String finalStatus) {
        if (instance == null) {
            return;
        }
        if (!"TRANSFER_APPLICATION".equals(instance.getBusinessType())) {
            return;
        }
        Long applicationId = extractApplicationId(instance.getBusinessKey());
        if (applicationId == null) {
            return;
        }
        if ("APPROVED".equals(finalStatus)) {
            transferApplicationArchiveMaterializationService.materializeAfterApproval(applicationId);
        } else if ("REJECTED".equals(finalStatus)) {
            transferApplicationArchiveMaterializationService.markRejected(applicationId);
        }
    }

    private Long extractApplicationId(String businessKey) {
        if (businessKey == null || !businessKey.startsWith(TRANSFER_BUSINESS_KEY_PREFIX)) {
            return null;
        }
        try {
            return Long.valueOf(businessKey.substring(TRANSFER_BUSINESS_KEY_PREFIX.length()));
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}
