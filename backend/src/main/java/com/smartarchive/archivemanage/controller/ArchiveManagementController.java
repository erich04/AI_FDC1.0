package com.smartarchive.archivemanage.controller;

import com.smartarchive.archivemanage.dto.AiModelConfigResponse;
import com.smartarchive.archivemanage.dto.ArchiveAskCommand;
import com.smartarchive.archivemanage.dto.ArchiveAskResponse;
import com.smartarchive.archivemanage.dto.ArchiveAttachmentResponse;
import com.smartarchive.archivemanage.dto.ArchiveAttachmentUpdateCommand;
import com.smartarchive.archivemanage.dto.ArchiveCreateCommand;
import com.smartarchive.archivemanage.dto.ArchiveCreateOptionsResponse;
import com.smartarchive.archivemanage.dto.ArchiveCreateSessionCommand;
import com.smartarchive.archivemanage.dto.ArchiveCreateSessionResponse;
import com.smartarchive.archivemanage.dto.ArchiveDefaultResolveResponse;
import com.smartarchive.archivemanage.dto.ArchiveQueryCommand;
import com.smartarchive.archivemanage.dto.ArchiveQueryResponse;
import com.smartarchive.archivemanage.dto.ArchiveSummaryResponse;
import com.smartarchive.archivemanage.dto.ArchiveTransferCommand;
import com.smartarchive.archivemanage.dto.ArchiveTransferResponse;
import com.smartarchive.archivemanage.dto.PendingAuditAttachmentRef;
import com.smartarchive.archivemanage.dto.PendingAuditDownload;
import com.smartarchive.archivemanage.dto.PendingDocumentBatchDeleteCommand;
import com.smartarchive.archivemanage.dto.PendingDocumentExportCommand;
import com.smartarchive.archivemanage.dto.PendingDocumentWriteCommand;
import com.smartarchive.archivemanage.dto.BindBatchResponse;
import com.smartarchive.archivemanage.dto.BindCreateCommand;
import com.smartarchive.archivemanage.dto.BindOptionsResponse;
import com.smartarchive.archivemanage.dto.BindPreviewCommand;
import com.smartarchive.archivemanage.dto.BindPreviewResponse;
import com.smartarchive.archivemanage.dto.BindQueryCommand;
import com.smartarchive.archivemanage.dto.StorageBatchResponse;
import com.smartarchive.archivemanage.dto.StorageCreateCommand;
import com.smartarchive.archivemanage.dto.StorageLedgerQueryCommand;
import com.smartarchive.archivemanage.dto.StorageLedgerResponse;
import com.smartarchive.archivemanage.dto.StorageOptionsResponse;
import com.smartarchive.archivemanage.dto.StorageQueryCommand;
import com.smartarchive.archivemanage.dto.StorageQueryResponse;
import com.smartarchive.archivemanage.service.ArchiveManagementService;
import com.smartarchive.common.api.ApiResponse;
import com.smartarchive.workspace.dto.WorkspaceIoJobSummaryResponse;
import jakarta.validation.Valid;
import java.nio.charset.StandardCharsets;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/archive-management")
@RequiredArgsConstructor
public class ArchiveManagementController {
    private final ArchiveManagementService archiveManagementService;

    @GetMapping("/create/options")
    public ApiResponse<ArchiveCreateOptionsResponse> loadCreateOptions() {
        return ApiResponse.success(archiveManagementService.loadCreateOptions());
    }

    @GetMapping("/create/defaults")
    public ApiResponse<ArchiveDefaultResolveResponse> resolveDefaults(@RequestParam String companyProjectCode,
                                                                      @RequestParam String busiModuleCode,
                                                                      @RequestParam(required = false) String customRule,
                                                                      @RequestParam(required = false) String archiveDestination) {
        return ApiResponse.success(archiveManagementService.resolveDefaults(companyProjectCode, busiModuleCode, customRule, archiveDestination));
    }

    @PostMapping("/create/sessions")
    public ApiResponse<ArchiveCreateSessionResponse> createSession(@Valid @RequestBody ArchiveCreateSessionCommand command) {
        return ApiResponse.success(archiveManagementService.createSession(command));
    }

    @GetMapping("/create/sessions/{sessionCode}")
    public ApiResponse<ArchiveCreateSessionResponse> getSession(@PathVariable String sessionCode) {
        return ApiResponse.success(archiveManagementService.getSession(sessionCode));
    }

    @PostMapping("/create/sessions/{sessionCode}/attachments")
    public ApiResponse<ArchiveAttachmentResponse> uploadAttachment(@PathVariable String sessionCode,
                                                                   @RequestParam String attachmentRole,
                                                                   @RequestParam(required = false) String attachmentTypeCode,
                                                                   @RequestParam(required = false) String remark,
                                                                   @RequestParam("file") MultipartFile file) {
        return ApiResponse.success(archiveManagementService.uploadAttachment(sessionCode, attachmentRole, attachmentTypeCode, remark, file));
    }

    @PutMapping("/create/sessions/{sessionCode}/attachments/{attachmentId}")
    public ApiResponse<ArchiveAttachmentResponse> updateAttachment(@PathVariable String sessionCode,
                                                                   @PathVariable Long attachmentId,
                                                                   @RequestBody ArchiveAttachmentUpdateCommand command) {
        return ApiResponse.success(archiveManagementService.updateAttachment(sessionCode, attachmentId, command));
    }

    @PostMapping("/create/archives")
    public ApiResponse<ArchiveSummaryResponse> createArchive(@RequestBody ArchiveCreateCommand command) {
        return ApiResponse.success(archiveManagementService.createArchive(command));
    }

    @PostMapping("/create/query")
    public ApiResponse<ArchiveQueryResponse> queryArchives(@RequestBody ArchiveQueryCommand command) {
        return ApiResponse.success(archiveManagementService.queryArchives(command));
    }

    @PostMapping("/create/ask")
    public ApiResponse<ArchiveAskResponse> ask(@Valid @RequestBody ArchiveAskCommand command) {
        return ApiResponse.success(archiveManagementService.ask(command));
    }

    @GetMapping("/ai-models")
    public ApiResponse<List<AiModelConfigResponse>> listAiModels() {
        return ApiResponse.success(archiveManagementService.listAiModels());
    }

    @GetMapping("/archives/{archiveId}")
    public ApiResponse<ArchiveSummaryResponse> getArchiveDetail(@PathVariable Long archiveId) {
        return ApiResponse.success(archiveManagementService.getArchiveDetail(archiveId));
    }

    @PostMapping("/pending-documents")
    public ApiResponse<ArchiveSummaryResponse> createPendingDocument(@RequestBody PendingDocumentWriteCommand command) {
        return ApiResponse.success(archiveManagementService.createPendingDocument(command));
    }

    @PutMapping("/pending-documents/{docId}")
    public ApiResponse<ArchiveSummaryResponse> updatePendingDocument(@PathVariable Long docId, @RequestBody PendingDocumentWriteCommand command) {
        return ApiResponse.success(archiveManagementService.updatePendingDocument(docId, command));
    }

    @PostMapping("/pending-documents/batch-delete")
    public ApiResponse<Void> batchDeletePendingDocuments(
        @RequestBody PendingDocumentBatchDeleteCommand command,
        @RequestHeader(value = "X-User-Id", required = false) Long userId
    ) {
        long uid = userId != null && userId > 0 ? userId : 1L;
        archiveManagementService.deletePendingDocuments(command, uid);
        return ApiResponse.success(null);
    }

    @PostMapping("/pending-documents/{docId}/duplicate")
    public ApiResponse<ArchiveSummaryResponse> duplicatePendingDocument(
        @PathVariable Long docId,
        @RequestHeader(value = "X-User-Id", required = false) Long userId
    ) {
        long uid = userId != null && userId > 0 ? userId : 1L;
        return ApiResponse.success(archiveManagementService.duplicatePendingDocument(docId, uid));
    }

    @PostMapping("/pending-documents/audit-attachments")
    public ApiResponse<PendingAuditAttachmentRef> uploadPendingAuditAttachment(@RequestParam("file") MultipartFile file) {
        return ApiResponse.success(archiveManagementService.uploadPendingAuditAttachment(file));
    }

    @GetMapping("/pending-documents/audit-attachments/download")
    public ResponseEntity<Resource> downloadPendingAuditAttachment(@RequestParam(required = false) Long fileId,
                                                                   @RequestParam(required = false) String storageKey) {
        PendingAuditDownload d = archiveManagementService.downloadPendingAuditAttachment(fileId, storageKey);
        ContentDisposition disposition = ContentDisposition.attachment()
            .filename(d.fileName(), StandardCharsets.UTF_8)
            .build();
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, disposition.toString())
            .contentType(MediaType.parseMediaType(d.contentType()))
            .body(d.resource());
    }

    @GetMapping("/attachments/{attachmentId}/download")
    public ResponseEntity<Resource> downloadArchiveAttachment(@PathVariable Long attachmentId) {
        PendingAuditDownload d = archiveManagementService.downloadArchiveAttachment(attachmentId);
        ContentDisposition disposition = ContentDisposition.attachment()
            .filename(d.fileName(), StandardCharsets.UTF_8)
            .build();
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, disposition.toString())
            .contentType(MediaType.parseMediaType(d.contentType()))
            .body(d.resource());
    }

    @GetMapping("/attachments/{attachmentId}/preview")
    public ResponseEntity<Resource> previewArchiveAttachment(@PathVariable Long attachmentId) {
        PendingAuditDownload d = archiveManagementService.previewArchiveAttachment(attachmentId);
        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(d.contentType()))
            .body(d.resource());
    }

    @GetMapping("/archives/{archiveId}/attachments/download-all")
    public ResponseEntity<Resource> downloadArchiveAttachmentsZip(@PathVariable Long archiveId) {
        PendingAuditDownload d = archiveManagementService.downloadArchiveAttachmentsZip(archiveId);
        ContentDisposition disposition = ContentDisposition.attachment()
            .filename(d.fileName(), StandardCharsets.UTF_8)
            .build();
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, disposition.toString())
            .contentType(MediaType.parseMediaType(d.contentType()))
            .body(d.resource());
    }

    @PostMapping("/pending-documents/export-jobs")
    public ApiResponse<WorkspaceIoJobSummaryResponse> createPendingDocumentsExportJob(
        @RequestBody PendingDocumentExportCommand command,
        @RequestHeader(value = "X-User-Id", required = false) Long userId
    ) {
        long uid = userId != null && userId > 0 ? userId : 1L;
        return ApiResponse.success(archiveManagementService.createPendingDocumentsExportJob(command.getDocIds(), command.getExportFileFormat(), command.getExportScope(), uid));
    }

    @PostMapping("/archives/import-query-jobs")
    public ApiResponse<WorkspaceIoJobSummaryResponse> submitArchiveImportQueryJob(
        @RequestParam("file") MultipartFile file,
        @RequestParam String documentTypeCode,
        @RequestHeader(value = "X-User-Id", required = false) Long userId
    ) {
        long uid = userId != null && userId > 0 ? userId : 1L;
        return ApiResponse.success(archiveManagementService.submitArchiveImportQueryJob(file, documentTypeCode, uid));
    }

    @PostMapping("/archives/transfer")
    public ApiResponse<ArchiveTransferResponse> transferArchives(@Valid @RequestBody ArchiveTransferCommand command) {
        return ApiResponse.success(archiveManagementService.transferArchives(command));
    }

    @GetMapping("/bind/options")
    public ApiResponse<BindOptionsResponse> loadBindOptions() {
        return ApiResponse.success(archiveManagementService.loadBindOptions());
    }

    @PostMapping("/bind/preview")
    public ApiResponse<BindPreviewResponse> previewBind(@RequestBody BindPreviewCommand command) {
        return ApiResponse.success(archiveManagementService.previewBind(command));
    }

    @PostMapping("/bind/batches")
    public ApiResponse<BindBatchResponse> createBindBatch(@Valid @RequestBody BindCreateCommand command) {
        return ApiResponse.success(archiveManagementService.createBindBatch(command));
    }

    @GetMapping("/bind/batches/{bindBatchCode}")
    public ApiResponse<BindBatchResponse> getBindBatch(@PathVariable String bindBatchCode) {
        return ApiResponse.success(archiveManagementService.getBindBatch(bindBatchCode));
    }

    @PostMapping("/bind/query")
    public ApiResponse<List<BindBatchResponse>> queryBindBatches(@RequestBody BindQueryCommand command) {
        return ApiResponse.success(archiveManagementService.queryBindBatches(command));
    }

    @GetMapping("/storage/options")
    public ApiResponse<StorageOptionsResponse> loadStorageOptions() {
        return ApiResponse.success(archiveManagementService.loadStorageOptions());
    }

    @PostMapping("/storage/query")
    public ApiResponse<StorageQueryResponse> queryStorage(@RequestBody StorageQueryCommand command) {
        return ApiResponse.success(archiveManagementService.queryStorage(command));
    }

    @PostMapping("/storage/batches")
    public ApiResponse<StorageBatchResponse> createStorageBatch(@Valid @RequestBody StorageCreateCommand command) {
        return ApiResponse.success(archiveManagementService.createStorageBatch(command));
    }

    @GetMapping("/storage/batches/{storageBatchCode}")
    public ApiResponse<StorageBatchResponse> getStorageBatch(@PathVariable String storageBatchCode) {
        return ApiResponse.success(archiveManagementService.getStorageBatch(storageBatchCode));
    }

    @PostMapping("/storage/ledger")
    public ApiResponse<List<StorageLedgerResponse>> queryStorageLedger(@RequestBody StorageLedgerQueryCommand command) {
        return ApiResponse.success(archiveManagementService.queryStorageLedger(command));
    }

    @GetMapping("/storage/ledger/{ledgerId}")
    public ApiResponse<StorageLedgerResponse> getStorageLedger(@PathVariable Long ledgerId) {
        return ApiResponse.success(archiveManagementService.getStorageLedger(ledgerId));
    }
}
