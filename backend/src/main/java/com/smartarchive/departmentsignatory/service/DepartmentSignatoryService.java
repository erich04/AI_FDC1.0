package com.smartarchive.departmentsignatory.service;

import com.smartarchive.departmentsignatory.dto.DepartmentSignatoryCommand;
import com.smartarchive.departmentsignatory.dto.DepartmentSignatoryResponse;
import java.util.List;

public interface DepartmentSignatoryService {
    List<DepartmentSignatoryResponse> list(String departmentName, List<String> signatories);
    DepartmentSignatoryResponse create(DepartmentSignatoryCommand command);
    DepartmentSignatoryResponse update(Long id, DepartmentSignatoryCommand command);
    void delete(Long id);
}
