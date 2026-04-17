package com.smartarchive.departmentsignatory.controller;

import com.smartarchive.common.api.ApiResponse;
import com.smartarchive.departmentsignatory.dto.DepartmentSignatoryCommand;
import com.smartarchive.departmentsignatory.dto.DepartmentSignatoryResponse;
import com.smartarchive.departmentsignatory.service.DepartmentSignatoryService;
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
@RequestMapping("/api/base-data/department-signatories")
@RequiredArgsConstructor
public class DepartmentSignatoryController {
    private final DepartmentSignatoryService departmentSignatoryService;

    @GetMapping
    public ApiResponse<List<DepartmentSignatoryResponse>> list(@RequestParam(required = false) String departmentName,
                                                               @RequestParam(required = false) List<String> signatories) {
        return ApiResponse.success(departmentSignatoryService.list(departmentName, signatories));
    }

    @PostMapping
    public ApiResponse<DepartmentSignatoryResponse> create(@Valid @RequestBody DepartmentSignatoryCommand command) {
        return ApiResponse.success(departmentSignatoryService.create(command));
    }

    @PutMapping("/{id}")
    public ApiResponse<DepartmentSignatoryResponse> update(@PathVariable Long id, @Valid @RequestBody DepartmentSignatoryCommand command) {
        return ApiResponse.success(departmentSignatoryService.update(id, command));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        departmentSignatoryService.delete(id);
        return ApiResponse.success(null);
    }
}
