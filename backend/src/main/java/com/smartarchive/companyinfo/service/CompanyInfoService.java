package com.smartarchive.companyinfo.service;

import com.smartarchive.companyinfo.dto.CompanyInfoCommand;
import com.smartarchive.companyinfo.dto.CompanyInfoResponse;
import com.smartarchive.companyinfo.dto.CompanyInfoUpdateCommand;
import com.smartarchive.companyinfo.dto.CompanyTagCommand;
import com.smartarchive.companyinfo.dto.CompanyTagResponse;
import java.util.List;

public interface CompanyInfoService {
    List<CompanyInfoResponse> list(List<String> companyCodes, String region, String representativeOffice, String country, String enabledFlag, List<String> tags);
    CompanyInfoResponse create(CompanyInfoCommand command);
    CompanyInfoResponse update(String companyCode, CompanyInfoUpdateCommand command);
    void delete(String companyCode);
    List<CompanyTagResponse> listTags(boolean enabledOnly);
    CompanyTagResponse createTag(CompanyTagCommand command);
    CompanyTagResponse updateTag(Long tagId, CompanyTagCommand command);
    void deleteTag(Long tagId);
}
