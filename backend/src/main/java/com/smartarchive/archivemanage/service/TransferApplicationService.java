package com.smartarchive.archivemanage.service;

import com.smartarchive.archivemanage.dto.TransferApplicationCreateCommand;
import com.smartarchive.archivemanage.dto.TransferApplicationDetailAttachmentResponse;
import com.smartarchive.archivemanage.dto.TransferApplicationRecordPageCommand;
import com.smartarchive.archivemanage.dto.TransferApplicationRecordPageResponse;
import com.smartarchive.archivemanage.dto.TransferApplicationResponse;
import java.util.List;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface TransferApplicationService {
    List<TransferApplicationResponse> list(Long tenantid);

    TransferApplicationResponse detail(Long applicationId);

    TransferApplicationResponse create(TransferApplicationCreateCommand command);

    TransferApplicationResponse update(Long applicationId, TransferApplicationCreateCommand command);

    void delete(Long applicationId);

    /** 移交记录分页查询（fdc_application_t + 明细条件 EXISTS） */
    TransferApplicationRecordPageResponse searchPage(TransferApplicationRecordPageCommand command);

    TransferApplicationDetailAttachmentResponse uploadDetailAttachment(Long applicationId,
                                                                      Long detailId,
                                                                      String remark,
                                                                      MultipartFile file);

    List<TransferApplicationDetailAttachmentResponse> listDetailAttachments(Long applicationId, Long detailId);

    Resource downloadDetailAttachment(Long applicationId, Long detailId, Long attachmentId);
}
