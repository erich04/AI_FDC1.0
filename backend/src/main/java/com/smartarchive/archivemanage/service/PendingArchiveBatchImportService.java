package com.smartarchive.archivemanage.service;

import com.smartarchive.archivemanage.dto.PendingAuditAttachmentRef;
import com.smartarchive.workspace.dto.WorkspaceIoJobSummaryResponse;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface PendingArchiveBatchImportService {

    /**
     * 提交应归档批量导入（仅正式创建）。立即返回 RUNNING 任务，后台异步处理；结果在「我的导入」下载 xlsx。
     *
     * @param auditAttachments 已上传的补充说明附件，将随每条成功创建的文档写入操作审计（与单条创建一致）
     */
    WorkspaceIoJobSummaryResponse submit(
        MultipartFile file,
        String documentTypeCode,
        String operationRemark,
        List<PendingAuditAttachmentRef> auditAttachments,
        long operatorUserId
    );
}
