package com.smartarchive.companyinfo.controller;

import com.smartarchive.common.api.ApiResponse;
import com.smartarchive.companyinfo.dto.CompanyInfoCommand;
import com.smartarchive.companyinfo.dto.CompanyInfoResponse;
import com.smartarchive.companyinfo.dto.CompanyInfoUpdateCommand;
import com.smartarchive.companyinfo.dto.CompanyTagCommand;
import com.smartarchive.companyinfo.dto.CompanyTagResponse;
import com.smartarchive.companyinfo.service.CompanyInfoService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/base-data/company-infos")
@RequiredArgsConstructor
public class CompanyInfoController {
    private final CompanyInfoService companyInfoService;

    @GetMapping
    public ApiResponse<List<CompanyInfoResponse>> list(@RequestParam(required = false) List<String> companyCodes,
                                                       @RequestParam(required = false) String companyName,
                                                       @RequestParam(required = false) String region,
                                                       @RequestParam(required = false) String representativeOffice,
                                                       @RequestParam(required = false) String country,
                                                       @RequestParam(required = false) String enabledFlag,
                                                       @RequestParam(required = false) List<String> tags) {
        return ApiResponse.success(companyInfoService.list(companyCodes, companyName, region, representativeOffice, country, enabledFlag, tags));
    }

    @PostMapping
    public ApiResponse<CompanyInfoResponse> create(@Valid @RequestBody CompanyInfoCommand command) {
        return ApiResponse.success(companyInfoService.create(command));
    }

    @PutMapping("/{companyCode}")
    public ApiResponse<CompanyInfoResponse> update(@PathVariable String companyCode, @Valid @RequestBody CompanyInfoUpdateCommand command) {
        return ApiResponse.success(companyInfoService.update(companyCode, command));
    }

    @DeleteMapping("/{companyCode}")
    public ApiResponse<Void> delete(@PathVariable String companyCode) {
        companyInfoService.delete(companyCode);
        return ApiResponse.success(null);
    }

    @GetMapping("/tags")
    public ApiResponse<List<CompanyTagResponse>> tags(@RequestParam(defaultValue = "false") boolean enabledOnly) {
        return ApiResponse.success(companyInfoService.listTags(enabledOnly));
    }

    @PostMapping("/tags")
    public ApiResponse<CompanyTagResponse> createTag(@Valid @RequestBody CompanyTagCommand command) {
        return ApiResponse.success(companyInfoService.createTag(command));
    }

    @PutMapping("/tags/{tagId}")
    public ApiResponse<CompanyTagResponse> updateTag(@PathVariable Long tagId, @Valid @RequestBody CompanyTagCommand command) {
        return ApiResponse.success(companyInfoService.updateTag(tagId, command));
    }

    @DeleteMapping("/tags/{tagId}")
    public ApiResponse<Void> deleteTag(@PathVariable Long tagId) {
        companyInfoService.deleteTag(tagId);
        return ApiResponse.success(null);
    }
}
