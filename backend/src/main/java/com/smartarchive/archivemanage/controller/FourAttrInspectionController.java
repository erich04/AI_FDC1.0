package com.smartarchive.archivemanage.controller;

import com.smartarchive.archivemanage.dto.FourAttrInspectionDetailBatchSaveCommand;
import com.smartarchive.archivemanage.dto.FourAttrInspectionQueryCommand;
import com.smartarchive.archivemanage.dto.FourAttrInspectionResponse;
import com.smartarchive.archivemanage.dto.FourAttrInspectionSaveCommand;
import com.smartarchive.archivemanage.service.FourAttrInspectionService;
import com.smartarchive.common.api.ApiResponse;
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
@RequestMapping({"/api/security/four-properties/configs", "/api/security/four-properties/configs."})
@RequiredArgsConstructor
public class FourAttrInspectionController {
    private final FourAttrInspectionService fourAttrInspectionService;

    @GetMapping
    public ApiResponse<List<FourAttrInspectionResponse>> list(@RequestParam(required = false) String inspectionName,
                                                              @RequestParam(required = false) String inspectionStage,
                                                              @RequestParam(required = false, defaultValue = "Y") String enableFlag,
                                                              @RequestParam(required = false) Long tenantid) {
        FourAttrInspectionQueryCommand command = new FourAttrInspectionQueryCommand();
        command.setInspectionName(inspectionName);
        command.setInspectionStage(inspectionStage);
        command.setEnableFlag(enableFlag);
        command.setTenantid(tenantid);
        return ApiResponse.success(fourAttrInspectionService.list(command));
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> export(@RequestParam(required = false) String inspectionName,
                                         @RequestParam(required = false) String inspectionStage,
                                         @RequestParam(required = false, defaultValue = "Y") String enableFlag,
                                         @RequestParam(required = false) Long tenantid) {
        FourAttrInspectionQueryCommand command = new FourAttrInspectionQueryCommand();
        command.setInspectionName(inspectionName);
        command.setInspectionStage(inspectionStage);
        command.setEnableFlag(enableFlag);
        command.setTenantid(tenantid);
        byte[] file = fourAttrInspectionService.exportCsv(command);
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"four-attr-inspections.csv\"")
            .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
            .body(file);
    }

    @PostMapping("/import")
    public ApiResponse<Integer> importCsv(@RequestParam("file") MultipartFile file,
                                          @RequestParam(required = false) Long tenantid) throws IOException {
        return ApiResponse.success(fourAttrInspectionService.importCsv(file.getInputStream(), tenantid));
    }

    @GetMapping("/{inspectionId:\\d+}")
    public ApiResponse<FourAttrInspectionResponse> detail(@PathVariable Long inspectionId,
                                                          @RequestParam(required = false) Long tenantid) {
        return ApiResponse.success(fourAttrInspectionService.detail(inspectionId, tenantid));
    }

    @PostMapping
    public ApiResponse<FourAttrInspectionResponse> create(@Valid @RequestBody FourAttrInspectionSaveCommand command) {
        return ApiResponse.success(fourAttrInspectionService.create(command));
    }

    @PutMapping("/{inspectionId:\\d+}")
    public ApiResponse<FourAttrInspectionResponse> update(@PathVariable Long inspectionId,
                                                          @Valid @RequestBody FourAttrInspectionSaveCommand command) {
        return ApiResponse.success(fourAttrInspectionService.update(inspectionId, command));
    }

    @PutMapping("/{inspectionId:\\d+}/details")
    public ApiResponse<FourAttrInspectionResponse> saveDetails(@PathVariable Long inspectionId,
                                                               @Valid @RequestBody FourAttrInspectionDetailBatchSaveCommand command) {
        command.setInspectionId(inspectionId);
        return ApiResponse.success(fourAttrInspectionService.saveDetails(command));
    }
}
