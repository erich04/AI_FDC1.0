package com.smartarchive.departmentsignatory.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Data
public class DepartmentSignatoryResponse {
    private Long departmentSignatoryId;
    private String firstLevelDepartment;
    private String secondLevelDepartment;
    private String thirdLevelDepartment;
    private String fourthLevelDepartment;
    private List<String> signatories;
    private LocalDateTime lastUpdateDate;
}
