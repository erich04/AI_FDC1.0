package com.smartarchive.workspace.controller;

import com.smartarchive.common.api.ApiResponse;
import com.smartarchive.workspace.dto.WorkspaceExportArtifactResult;
import com.smartarchive.workspace.dto.WorkspaceImportQueryResultRecordResponse;
import com.smartarchive.workspace.dto.WorkspaceIoJobCreateCommand;
import com.smartarchive.workspace.dto.WorkspaceIoJobPageResponse;
import com.smartarchive.workspace.dto.WorkspaceIoJobQueryCommand;
import com.smartarchive.workspace.dto.WorkspaceIoJobSummaryResponse;
import com.smartarchive.workspace.service.WorkspaceIoJobService;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/workspace/io-jobs")
@RequiredArgsConstructor
public class WorkspaceIoJobController {
    private final WorkspaceIoJobService workspaceIoJobService;

    private long operatorUserId(Long headerVal) {
        return headerVal != null && headerVal > 0 ? headerVal : 1L;
    }

    @PostMapping
    public ApiResponse<WorkspaceIoJobSummaryResponse> create(
        @RequestBody WorkspaceIoJobCreateCommand command,
        @RequestHeader(value = "X-User-Id", required = false) Long userId
    ) {
        return ApiResponse.success(workspaceIoJobService.create(command, operatorUserId(userId)));
    }

    @PostMapping("/query")
    public ApiResponse<WorkspaceIoJobPageResponse> query(
        @RequestBody WorkspaceIoJobQueryCommand command,
        @RequestHeader(value = "X-User-Id", required = false) Long userId
    ) {
        return ApiResponse.success(workspaceIoJobService.query(command, operatorUserId(userId)));
    }

    @GetMapping("/{jobId}")
    public ApiResponse<WorkspaceIoJobSummaryResponse> get(
        @PathVariable Long jobId,
        @RequestHeader(value = "X-User-Id", required = false) Long userId
    ) {
        return ApiResponse.success(workspaceIoJobService.get(jobId, operatorUserId(userId)));
    }

    @DeleteMapping("/{jobId}")
    public ApiResponse<Void> delete(
        @PathVariable Long jobId,
        @RequestHeader(value = "X-User-Id", required = false) Long userId
    ) {
        workspaceIoJobService.delete(jobId, operatorUserId(userId));
        return ApiResponse.success(null);
    }

    @GetMapping("/{jobId}/failed-file")
    public ResponseEntity<byte[]> downloadFailedFile(
        @PathVariable Long jobId,
        @RequestHeader(value = "X-User-Id", required = false) Long userId
    ) {
        String csv = workspaceIoJobService.getFailedFileCsv(jobId, operatorUserId(userId));
        byte[] body = csv.getBytes(StandardCharsets.UTF_8);
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"failed.csv\"")
            .contentType(MediaType.parseMediaType("text/csv; charset=utf-8"))
            .body(body);
    }

    @GetMapping("/{jobId}/export-file")
    public ResponseEntity<byte[]> downloadExportFile(
        @PathVariable Long jobId,
        @RequestHeader(value = "X-User-Id", required = false) Long userId
    ) {
        WorkspaceExportArtifactResult r = workspaceIoJobService.downloadExportArtifact(jobId, operatorUserId(userId));
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + r.fileName() + "\"")
            .contentType(MediaType.parseMediaType(r.contentType()))
            .body(r.body());
    }

    @GetMapping("/{jobId}/import-result")
    public ResponseEntity<byte[]> downloadImportResultFile(
        @PathVariable Long jobId,
        @RequestHeader(value = "X-User-Id", required = false) Long userId
    ) {
        WorkspaceExportArtifactResult r = workspaceIoJobService.downloadImportResultArtifact(jobId, operatorUserId(userId));
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + r.fileName() + "\"")
            .contentType(MediaType.parseMediaType(r.contentType()))
            .body(r.body());
    }

    @GetMapping("/{jobId}/import-query-results")
    public ApiResponse<List<WorkspaceImportQueryResultRecordResponse>> listImportQueryResults(
        @PathVariable Long jobId,
        @RequestHeader(value = "X-User-Id", required = false) Long userId
    ) {
        return ApiResponse.success(workspaceIoJobService.listImportQueryResults(jobId, operatorUserId(userId)));
    }
}
