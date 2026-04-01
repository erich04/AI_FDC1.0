package com.smartarchive.archiveflow.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface ArchiveFlowLookupMapper {
    @Select("""
        SELECT document_organization_code
        FROM fdc_document_organization_t
        WHERE enable_flag = 'Y'
          AND delete_flag IN ('N', 'Y')
        ORDER BY document_organization_code
        """)
    List<String> selectEnabledDocumentOrganizationCodes();

    @Select("""
        SELECT COUNT(1)
        FROM fdc_document_organization_t
        WHERE document_organization_code = #{code}
          AND enable_flag = 'Y'
          AND delete_flag IN ('N', 'Y')
        """)
    Integer countEnabledDocumentOrganizationCode(@Param("code") String code);
}
