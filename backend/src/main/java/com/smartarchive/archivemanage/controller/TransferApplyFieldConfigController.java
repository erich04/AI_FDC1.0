package com.smartarchive.archivemanage.controller;

import com.smartarchive.archivemanage.dto.TransferApplyFieldConfigResponse;
import com.smartarchive.archivemanage.dto.TransferApplyFieldConfigSaveCommand;
import com.smartarchive.archivemanage.service.TransferApplyFieldConfigService;
import com.smartarchive.common.api.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/api/archive-management/transfer-apply-field-configs", "/api/archive-manage/transfer-apply-field-configs"})
@RequiredArgsConstructor
public class TransferApplyFieldConfigController {
    private final TransferApplyFieldConfigService transferApplyFieldConfigService;

    @GetMapping("/{documentTypeCode}")
    public ApiResponse<TransferApplyFieldConfigResponse> getByDocumentTypeCode(@PathVariable String documentTypeCode,
                                                                                @RequestParam(required = false) Long tenantid) {
        return ApiResponse.success(transferApplyFieldConfigService.getByDocumentTypeCode(documentTypeCode, tenantid));
    }

    @PutMapping("/{documentTypeCode}")
    public ApiResponse<TransferApplyFieldConfigResponse> saveByDocumentTypeCode(@PathVariable String documentTypeCode,
                                                                                 @Valid @RequestBody TransferApplyFieldConfigSaveCommand command) {
        return ApiResponse.success(transferApplyFieldConfigService.saveByDocumentTypeCode(documentTypeCode, command));
    }

    @PostMapping("/{documentTypeCode}")
    public ApiResponse<TransferApplyFieldConfigResponse> saveByDocumentTypeCodeByPost(@PathVariable String documentTypeCode,
                                                                                       @Valid @RequestBody TransferApplyFieldConfigSaveCommand command) {
        return ApiResponse.success(transferApplyFieldConfigService.saveByDocumentTypeCode(documentTypeCode, command));
    }
}
