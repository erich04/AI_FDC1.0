package com.smartarchive.departmentsignatory.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.smartarchive.common.exception.BusinessException;
import com.smartarchive.departmentsignatory.domain.DepartmentSignatory;
import com.smartarchive.departmentsignatory.dto.DepartmentSignatoryCommand;
import com.smartarchive.departmentsignatory.dto.DepartmentSignatoryResponse;
import com.smartarchive.departmentsignatory.mapper.DepartmentSignatoryMapper;
import com.smartarchive.departmentsignatory.service.DepartmentSignatoryService;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class DepartmentSignatoryServiceImpl implements DepartmentSignatoryService {
    private static final Long SYSTEM_OPERATOR_ID = 1L;

    private final DepartmentSignatoryMapper departmentSignatoryMapper;

    @Override
    public List<DepartmentSignatoryResponse> list(String departmentName, List<String> signatories) {
        LambdaQueryWrapper<DepartmentSignatory> wrapper = new LambdaQueryWrapper<DepartmentSignatory>()
                .eq(DepartmentSignatory::getDeleteFlag, "N")
                .and(StringUtils.hasText(departmentName), query -> query
                        .like(DepartmentSignatory::getFirstLevelDepartment, trim(departmentName))
                        .or()
                        .like(DepartmentSignatory::getSecondLevelDepartment, trim(departmentName))
                        .or()
                        .like(DepartmentSignatory::getThirdLevelDepartment, trim(departmentName))
                        .or()
                        .like(DepartmentSignatory::getFourthLevelDepartment, trim(departmentName)))
                .orderByAsc(DepartmentSignatory::getFirstLevelDepartment)
                .orderByAsc(DepartmentSignatory::getSecondLevelDepartment)
                .orderByAsc(DepartmentSignatory::getThirdLevelDepartment)
                .orderByAsc(DepartmentSignatory::getFourthLevelDepartment);
        List<DepartmentSignatoryResponse> result = departmentSignatoryMapper.selectList(wrapper).stream().map(this::toResponse).toList();
        List<String> selectedSignatories = normalizeSignatories(signatories);
        if (selectedSignatories.isEmpty()) {
            return result;
        }
        return result.stream()
                .filter(item -> item.getSignatories().stream().anyMatch(selectedSignatories::contains))
                .toList();
    }

    @Override
    @Transactional
    public DepartmentSignatoryResponse create(DepartmentSignatoryCommand command) {
        DepartmentSignatory entity = new DepartmentSignatory();
        apply(entity, command);
        entity.setDeleteFlag("N");
        entity.setCreatedBy(SYSTEM_OPERATOR_ID);
        entity.setCreationDate(LocalDateTime.now());
        entity.setLastUpdatedBy(SYSTEM_OPERATOR_ID);
        entity.setLastUpdateDate(LocalDateTime.now());
        departmentSignatoryMapper.insert(entity);
        return toResponse(entity);
    }

    @Override
    @Transactional
    public DepartmentSignatoryResponse update(Long id, DepartmentSignatoryCommand command) {
        DepartmentSignatory entity = requireSignatory(id);
        apply(entity, command);
        entity.setLastUpdatedBy(SYSTEM_OPERATOR_ID);
        entity.setLastUpdateDate(LocalDateTime.now());
        departmentSignatoryMapper.updateById(entity);
        return toResponse(entity);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        requireSignatory(id);
        departmentSignatoryMapper.update(null, new LambdaUpdateWrapper<DepartmentSignatory>()
                .eq(DepartmentSignatory::getDepartmentSignatoryId, id)
                .set(DepartmentSignatory::getDeleteFlag, "Y")
                .set(DepartmentSignatory::getLastUpdatedBy, SYSTEM_OPERATOR_ID)
                .set(DepartmentSignatory::getLastUpdateDate, LocalDateTime.now()));
    }

    private void apply(DepartmentSignatory entity, DepartmentSignatoryCommand command) {
        List<String> signatories = normalizeSignatories(command.getSignatories());
        if (signatories.isEmpty()) {
            throw new BusinessException("请至少选择一名部门权签人");
        }
        entity.setFirstLevelDepartment(requireText(command.getFirstLevelDepartment(), "一级部门不能为空"));
        entity.setSecondLevelDepartment(trimToNull(command.getSecondLevelDepartment()));
        entity.setThirdLevelDepartment(trimToNull(command.getThirdLevelDepartment()));
        entity.setFourthLevelDepartment(trimToNull(command.getFourthLevelDepartment()));
        entity.setSignatories(String.join(",", signatories));
    }

    private DepartmentSignatory requireSignatory(Long id) {
        DepartmentSignatory entity = departmentSignatoryMapper.selectOne(new LambdaQueryWrapper<DepartmentSignatory>()
                .eq(DepartmentSignatory::getDepartmentSignatoryId, id)
                .eq(DepartmentSignatory::getDeleteFlag, "N")
                .last("limit 1"));
        if (entity == null) {
            throw new BusinessException("权签人维护记录不存在");
        }
        return entity;
    }

    private DepartmentSignatoryResponse toResponse(DepartmentSignatory entity) {
        DepartmentSignatoryResponse response = new DepartmentSignatoryResponse();
        response.setDepartmentSignatoryId(entity.getDepartmentSignatoryId());
        response.setFirstLevelDepartment(entity.getFirstLevelDepartment());
        response.setSecondLevelDepartment(entity.getSecondLevelDepartment());
        response.setThirdLevelDepartment(entity.getThirdLevelDepartment());
        response.setFourthLevelDepartment(entity.getFourthLevelDepartment());
        response.setSignatories(parseSignatories(entity.getSignatories()));
        response.setLastUpdateDate(entity.getLastUpdateDate());
        return response;
    }

    private List<String> normalizeSignatories(List<String> signatories) {
        if (signatories == null) return List.of();
        return signatories.stream().filter(StringUtils::hasText).map(String::trim).distinct().toList();
    }

    private List<String> parseSignatories(String signatories) {
        if (!StringUtils.hasText(signatories)) return List.of();
        return Arrays.stream(signatories.split(",")).filter(StringUtils::hasText).map(String::trim).toList();
    }

    private String requireText(String value, String message) {
        if (!StringUtils.hasText(value)) throw new BusinessException(message);
        return value.trim();
    }

    private String trimToNull(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }
}
