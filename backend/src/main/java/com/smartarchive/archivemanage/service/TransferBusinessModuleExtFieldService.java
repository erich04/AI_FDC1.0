package com.smartarchive.archivemanage.service;

import com.smartarchive.archivemanage.dto.DocumentTypeExtFieldResponse;
import java.util.List;
import java.util.Map;

/**
 * 移交申请明细扩展：来自 {@code fdc_business_module_ext_field_t} 且 {@code application_functions} 含「移交」的字段，
 * 映射为与 {@link com.smartarchive.archivemanage.service.DocumentTypeExtFieldService} 一致的配置结构，供写入/读取 {@code fdc_application_ext_t}。
 */
public interface TransferBusinessModuleExtFieldService {

    String APPLICATION_FUNCTION_TRANSFER = "移交";

    List<DocumentTypeExtFieldResponse> listEffectiveForTransfer(String moduleCode);

    Map<String, DocumentTypeExtFieldResponse> asConfigMap(String moduleCode);

    /** 小写 attr 列名（如 attr2）→ fieldCode */
    Map<String, String> columnToFieldCodeMap(String moduleCode);
}
