package com.smartarchive.documenttypeconfig.controller;

import com.smartarchive.common.api.ApiResponse;
import com.smartarchive.documenttypeconfig.dto.DocumentTypeConfigQueryCommand;
import com.smartarchive.documenttypeconfig.dto.DocumentTypeConfigResponse;
import com.smartarchive.documenttypeconfig.dto.DocumentTypeConfigSaveCommand;
import com.smartarchive.documenttypeconfig.service.DocumentTypeConfigService;
import jakarta.validation.Valid;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
@RequestMapping({"/api/base-data/document-type-configs", "/api/base-data/document-type-configs."})
@RequiredArgsConstructor
public class DocumentTypeConfigController {
    private final DocumentTypeConfigService service;

    @GetMapping
    public ApiResponse<List<DocumentTypeConfigResponse>> list(@RequestParam(required = false) String docTypeCode,
                                                              @RequestParam(required = false) String docTypeDescription,
                                                              @RequestParam(required = false, defaultValue = "Y") String enableFlag,
                                                              @RequestParam(required = false) Long tenantid) {
        DocumentTypeConfigQueryCommand command = new DocumentTypeConfigQueryCommand();
        command.setDocTypeCode(docTypeCode);
        command.setDocTypeDescription(docTypeDescription);
        command.setEnableFlag(enableFlag);
        command.setTenantid(tenantid);
        return ApiResponse.success(service.list(command));
    }

    @PostMapping
    public ApiResponse<DocumentTypeConfigResponse> create(@Valid @RequestBody DocumentTypeConfigSaveCommand command) {
        return ApiResponse.success(service.create(command));
    }

    @PutMapping("/{documentTypeId:\\d+}")
    public ApiResponse<DocumentTypeConfigResponse> update(@PathVariable Long documentTypeId,
                                                          @Valid @RequestBody DocumentTypeConfigSaveCommand command) {
        return ApiResponse.success(service.update(documentTypeId, command));
    }

    @GetMapping("/export/csv")
    public ResponseEntity<byte[]> exportCsv(@RequestParam(required = false) String docTypeCode,
                                            @RequestParam(required = false) String docTypeDescription,
                                            @RequestParam(required = false, defaultValue = "Y") String enableFlag,
                                            @RequestParam(required = false) Long tenantid) {
        DocumentTypeConfigQueryCommand command = new DocumentTypeConfigQueryCommand();
        command.setDocTypeCode(docTypeCode);
        command.setDocTypeDescription(docTypeDescription);
        command.setEnableFlag(enableFlag);
        command.setTenantid(tenantid);
        byte[] file = service.exportCsv(command);
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"document-type-configs.csv\"")
            .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
            .body(file);
    }

    @GetMapping("/export/excel")
    public ResponseEntity<byte[]> exportExcel(@RequestParam(required = false) String docTypeCode,
                                              @RequestParam(required = false) String docTypeDescription,
                                              @RequestParam(required = false, defaultValue = "Y") String enableFlag,
                                              @RequestParam(required = false) Long tenantid) {
        DocumentTypeConfigQueryCommand command = new DocumentTypeConfigQueryCommand();
        command.setDocTypeCode(docTypeCode);
        command.setDocTypeDescription(docTypeDescription);
        command.setEnableFlag(enableFlag);
        command.setTenantid(tenantid);
        byte[] file = service.exportExcel(command);
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"document-type-configs.xlsx\"")
            .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
            .body(file);
    }

    @PostMapping("/import/csv")
    public ApiResponse<Integer> importCsv(@RequestParam("file") MultipartFile file,
                                          @RequestParam(required = false) Long tenantid) throws IOException {
        return ApiResponse.success(service.importCsv(file.getInputStream(), tenantid));
    }

    @PostMapping("/import/excel")
    public ApiResponse<Integer> importExcel(@RequestParam("file") MultipartFile file,
                                            @RequestParam(required = false) Long tenantid) throws IOException {
        return ApiResponse.success(service.importExcel(file.getInputStream(), tenantid));
    }
}
