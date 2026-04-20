package com.smartarchive.archivemanage.controller;

import com.smartarchive.archivemanage.dto.TransferApplicationCreateCommand;
import com.smartarchive.archivemanage.dto.TransferApplicationDetailAttachmentResponse;
import com.smartarchive.archivemanage.dto.TransferApplicationRecordPageCommand;
import com.smartarchive.archivemanage.dto.TransferApplicationRecordPageResponse;
import com.smartarchive.archivemanage.dto.TransferApplicationResponse;
import com.smartarchive.archivemanage.service.TransferApplyFieldConfigService;
import com.smartarchive.archivemanage.service.TransferApplicationService;
import com.smartarchive.common.api.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/archive-management/transfer-applications")
@RequiredArgsConstructor
public class TransferApplicationController {
    private final TransferApplicationService transferApplicationService;
    private final TransferApplyFieldConfigService transferApplyFieldConfigService;

    @PostMapping("/search-page")
    public ApiResponse<TransferApplicationRecordPageResponse> searchPage(@Valid @RequestBody TransferApplicationRecordPageCommand command) {
        return ApiResponse.success(transferApplicationService.searchPage(command));
    }

    @GetMapping("/field-visibility")
    public ApiResponse<java.util.Map<String, Boolean>> fieldVisibility(@RequestParam String documentTypeCode,
                                                                       @RequestParam(required = false) Long tenantid) {
        return ApiResponse.success(transferApplyFieldConfigService.visibilityMapByDocumentTypeCode(documentTypeCode, tenantid));
    }

    @GetMapping
    public ApiResponse<List<TransferApplicationResponse>> list(@RequestParam(required = false) Long tenantid) {
        return ApiResponse.success(transferApplicationService.list(tenantid));
    }

    /** 仅匹配数字主键，避免与 /search-page 等固定路径冲突 */
    @GetMapping("/{applicationId:\\d+}")
    public ApiResponse<TransferApplicationResponse> detail(@PathVariable Long applicationId) {
        return ApiResponse.success(transferApplicationService.detail(applicationId));
    }

    @PostMapping
    public ApiResponse<TransferApplicationResponse> create(@Valid @RequestBody TransferApplicationCreateCommand command) {
        return ApiResponse.success(transferApplicationService.create(command));
    }

    @PutMapping("/{applicationId:\\d+}")
    public ApiResponse<TransferApplicationResponse> update(@PathVariable Long applicationId,
                                                           @Valid @RequestBody TransferApplicationCreateCommand command) {
        return ApiResponse.success(transferApplicationService.update(applicationId, command));
    }

    @DeleteMapping("/{applicationId:\\d+}")
    public ApiResponse<Void> delete(@PathVariable Long applicationId) {
        transferApplicationService.delete(applicationId);
        return ApiResponse.success(null);
    }

    @PostMapping("/{applicationId:\\d+}/details/{detailId:\\d+}/attachments")
    public ApiResponse<TransferApplicationDetailAttachmentResponse> uploadDetailAttachment(@PathVariable Long applicationId,
                                                                                           @PathVariable Long detailId,
                                                                                           @RequestParam(required = false) String remark,
                                                                                           @RequestParam("file") MultipartFile file) {
        return ApiResponse.success(transferApplicationService.uploadDetailAttachment(applicationId, detailId, remark, file));
    }

    @GetMapping("/{applicationId:\\d+}/details/{detailId:\\d+}/attachments")
    public ApiResponse<List<TransferApplicationDetailAttachmentResponse>> listDetailAttachments(@PathVariable Long applicationId,
                                                                                                 @PathVariable Long detailId) {
        return ApiResponse.success(transferApplicationService.listDetailAttachments(applicationId, detailId));
    }

    @GetMapping("/{applicationId:\\d+}/details/{detailId:\\d+}/attachments/{attachmentId:\\d+}/download")
    public ResponseEntity<Resource> downloadDetailAttachment(@PathVariable Long applicationId,
                                                             @PathVariable Long detailId,
                                                             @PathVariable Long attachmentId) {
        Resource resource = transferApplicationService.downloadDetailAttachment(applicationId, detailId, attachmentId);
        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
            .body(resource);
    }
}
