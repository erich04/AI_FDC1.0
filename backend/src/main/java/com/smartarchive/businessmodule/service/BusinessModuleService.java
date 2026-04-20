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

    /**
     * 按应用功能筛选扩展字段（如「移交」），{@code application_functions} 为逗号分隔。
     * @param fieldScope 可空；非空时与 {@link #listFields} 一致按 BASIC/ATTACHMENT 过滤
     */
    List<BusinessModuleExtFieldResponse> listFieldsByApplicationFunction(String moduleCode, String applicationFunction, String fieldScope);
    BusinessModuleExtFieldResponse createField(String moduleCode, BusinessModuleExtFieldCommand command);
    BusinessModuleExtFieldResponse updateField(String moduleCode, String fieldCode, BusinessModuleExtFieldCommand command);
    void deleteField(String moduleCode, String fieldCode);
}
