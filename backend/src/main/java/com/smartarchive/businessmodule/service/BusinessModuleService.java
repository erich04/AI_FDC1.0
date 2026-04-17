package com.smartarchive.businessmodule.service;

import com.smartarchive.businessmodule.dto.BusinessModuleCommand;
import com.smartarchive.businessmodule.dto.BusinessModuleExtFieldCommand;
import com.smartarchive.businessmodule.dto.BusinessModuleExtFieldResponse;
import com.smartarchive.businessmodule.dto.BusinessModuleNodeResponse;
import com.smartarchive.businessmodule.dto.BusinessModuleParentOptionResponse;
import com.smartarchive.businessmodule.dto.BusinessModuleUpdateCommand;
import java.util.List;

public interface BusinessModuleService {
    List<BusinessModuleNodeResponse> listTree();
    List<BusinessModuleParentOptionResponse> listParentOptions();
    BusinessModuleNodeResponse create(BusinessModuleCommand command);
    BusinessModuleNodeResponse update(String moduleCode, BusinessModuleUpdateCommand command);
    void delete(String moduleCode);
    List<BusinessModuleExtFieldResponse> listFields(String moduleCode, String fieldScope);
    BusinessModuleExtFieldResponse createField(String moduleCode, BusinessModuleExtFieldCommand command);
    BusinessModuleExtFieldResponse updateField(String moduleCode, String fieldCode, BusinessModuleExtFieldCommand command);
    void deleteField(String moduleCode, String fieldCode);
}
