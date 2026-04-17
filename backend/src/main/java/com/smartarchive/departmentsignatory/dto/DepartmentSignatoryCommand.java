package com.smartarchive.departmentsignatory.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.Data;

@Data
public class DepartmentSignatoryCommand {
    @NotBlank
    private String firstLevelDepartment;
    private String secondLevelDepartment;
    private String thirdLevelDepartment;
    private String fourthLevelDepartment;
    @NotEmpty
    private List<String> signatories;
}
