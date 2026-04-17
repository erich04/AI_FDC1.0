package com.smartarchive.companyinfo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.smartarchive.common.exception.BusinessException;
import com.smartarchive.companyinfo.domain.CompanyInfo;
import com.smartarchive.companyinfo.domain.CompanyTag;
import com.smartarchive.companyinfo.dto.CompanyInfoCommand;
import com.smartarchive.companyinfo.dto.CompanyInfoResponse;
import com.smartarchive.companyinfo.dto.CompanyInfoUpdateCommand;
import com.smartarchive.companyinfo.dto.CompanyTagCommand;
import com.smartarchive.companyinfo.dto.CompanyTagResponse;
import com.smartarchive.companyinfo.mapper.CompanyInfoMapper;
import com.smartarchive.companyinfo.mapper.CompanyTagMapper;
import com.smartarchive.companyinfo.service.CompanyInfoService;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class CompanyInfoServiceImpl implements CompanyInfoService {
    private static final Long SYSTEM_OPERATOR_ID = 1L;

    private final CompanyInfoMapper companyInfoMapper;
    private final CompanyTagMapper companyTagMapper;

    @Override
    public List<CompanyInfoResponse> list(List<String> companyCodes, String region, String representativeOffice, String country, String enabledFlag, List<String> tags) {
        LambdaQueryWrapper<CompanyInfo> wrapper = new LambdaQueryWrapper<CompanyInfo>()
                .eq(CompanyInfo::getDeleteFlag, "N")
                .in(companyCodes != null && !companyCodes.isEmpty(), CompanyInfo::getCompanyCode, companyCodes)
                .eq(StringUtils.hasText(region), CompanyInfo::getRegion, trimToNull(region))
                .eq(StringUtils.hasText(representativeOffice), CompanyInfo::getRepresentativeOffice, trimToNull(representativeOffice))
                .eq(StringUtils.hasText(country), CompanyInfo::getCountry, trimToNull(country))
                .eq(StringUtils.hasText(enabledFlag), CompanyInfo::getEnabledFlag, normalizeFlag(enabledFlag, "Y"))
                .orderByAsc(CompanyInfo::getCompanyCode);
        List<CompanyInfoResponse> result = companyInfoMapper.selectList(wrapper).stream().map(this::toResponse).toList();
        if (tags == null || tags.isEmpty()) {
            return result;
        }
        return result.stream()
                .filter(item -> item.getTags().containsAll(tags.stream().filter(StringUtils::hasText).map(String::trim).toList()))
                .toList();
    }

    @Override
    @Transactional
    public CompanyInfoResponse create(CompanyInfoCommand command) {
        if (exists(command.getCompanyCode())) {
            throw new BusinessException("公司编码已存在");
        }
        CompanyInfo entity = new CompanyInfo();
        entity.setCompanyCode(command.getCompanyCode().trim());
        apply(entity, command.getCompanyName(), command.getRegion(), command.getRepresentativeOffice(), command.getCountry(), command.getDescription(), command.getTags(), command.getEnabledFlag());
        entity.setDeleteFlag("N");
        entity.setCreatedBy(SYSTEM_OPERATOR_ID);
        entity.setCreationDate(LocalDateTime.now());
        entity.setLastUpdatedBy(SYSTEM_OPERATOR_ID);
        entity.setLastUpdateDate(LocalDateTime.now());
        companyInfoMapper.insert(entity);
        return toResponse(entity);
    }

    @Override
    @Transactional
    public CompanyInfoResponse update(String companyCode, CompanyInfoUpdateCommand command) {
        CompanyInfo entity = requireCompany(companyCode);
        apply(entity, command.getCompanyName(), command.getRegion(), command.getRepresentativeOffice(), command.getCountry(), command.getDescription(), command.getTags(), command.getEnabledFlag());
        entity.setLastUpdatedBy(SYSTEM_OPERATOR_ID);
        entity.setLastUpdateDate(LocalDateTime.now());
        companyInfoMapper.updateById(entity);
        return toResponse(entity);
    }

    @Override
    @Transactional
    public void delete(String companyCode) {
        CompanyInfo entity = requireCompany(companyCode);
        companyInfoMapper.update(null, new LambdaUpdateWrapper<CompanyInfo>()
                .eq(CompanyInfo::getCompanyId, entity.getCompanyId())
                .set(CompanyInfo::getDeleteFlag, "Y")
                .set(CompanyInfo::getLastUpdateDate, LocalDateTime.now()));
    }

    @Override
    public List<CompanyTagResponse> listTags(boolean enabledOnly) {
        return companyTagMapper.selectList(new LambdaQueryWrapper<CompanyTag>()
                        .eq(CompanyTag::getDeleteFlag, "N")
                        .eq(enabledOnly, CompanyTag::getEnabledFlag, "Y")
                        .orderByAsc(CompanyTag::getTagValue))
                .stream().map(this::toTagResponse).toList();
    }

    @Override
    @Transactional
    public CompanyTagResponse createTag(CompanyTagCommand command) {
        String value = command.getTagValue().trim();
        Long count = companyTagMapper.selectCount(new LambdaQueryWrapper<CompanyTag>()
                .eq(CompanyTag::getTagValue, value)
                .eq(CompanyTag::getDeleteFlag, "N"));
        if (count > 0) {
            throw new BusinessException("标签已存在");
        }
        CompanyTag tag = new CompanyTag();
        tag.setTagValue(value);
        tag.setEnabledFlag(normalizeFlag(command.getEnabledFlag(), "Y"));
        tag.setDeleteFlag("N");
        tag.setCreatedBy(SYSTEM_OPERATOR_ID);
        tag.setCreationDate(LocalDateTime.now());
        tag.setLastUpdatedBy(SYSTEM_OPERATOR_ID);
        tag.setLastUpdateDate(LocalDateTime.now());
        companyTagMapper.insert(tag);
        return toTagResponse(tag);
    }

    @Override
    @Transactional
    public CompanyTagResponse updateTag(Long tagId, CompanyTagCommand command) {
        CompanyTag tag = requireTag(tagId);
        tag.setTagValue(command.getTagValue().trim());
        tag.setEnabledFlag(normalizeFlag(command.getEnabledFlag(), "Y"));
        tag.setLastUpdatedBy(SYSTEM_OPERATOR_ID);
        tag.setLastUpdateDate(LocalDateTime.now());
        companyTagMapper.updateById(tag);
        return toTagResponse(tag);
    }

    @Override
    @Transactional
    public void deleteTag(Long tagId) {
        requireTag(tagId);
        companyTagMapper.update(null, new LambdaUpdateWrapper<CompanyTag>()
                .eq(CompanyTag::getTagId, tagId)
                .set(CompanyTag::getDeleteFlag, "Y")
                .set(CompanyTag::getLastUpdateDate, LocalDateTime.now()));
    }

    private void apply(CompanyInfo entity, String companyName, String region, String representativeOffice, String country, String description, List<String> tags, String enabledFlag) {
        entity.setCompanyName(companyName.trim());
        entity.setRegion(trimToNull(region));
        entity.setRepresentativeOffice(trimToNull(representativeOffice));
        entity.setCountry(trimToNull(country));
        entity.setDescription(trimToNull(description));
        entity.setTags(joinTags(tags));
        entity.setEnabledFlag(normalizeFlag(enabledFlag, "Y"));
    }

    private boolean exists(String companyCode) {
        return companyInfoMapper.selectCount(new LambdaQueryWrapper<CompanyInfo>()
                .eq(CompanyInfo::getCompanyCode, companyCode.trim())
                .eq(CompanyInfo::getDeleteFlag, "N")) > 0;
    }

    private CompanyInfo requireCompany(String companyCode) {
        CompanyInfo company = companyInfoMapper.selectOne(new LambdaQueryWrapper<CompanyInfo>()
                .eq(CompanyInfo::getCompanyCode, companyCode)
                .eq(CompanyInfo::getDeleteFlag, "N")
                .last("limit 1"));
        if (company == null) {
            throw new BusinessException("公司信息不存在");
        }
        return company;
    }

    private CompanyTag requireTag(Long tagId) {
        CompanyTag tag = companyTagMapper.selectOne(new LambdaQueryWrapper<CompanyTag>()
                .eq(CompanyTag::getTagId, tagId)
                .eq(CompanyTag::getDeleteFlag, "N")
                .last("limit 1"));
        if (tag == null) {
            throw new BusinessException("标签不存在");
        }
        return tag;
    }

    private String joinTags(List<String> tags) {
        if (tags == null || tags.isEmpty()) {
            return null;
        }
        List<String> normalized = tags.stream().filter(StringUtils::hasText).map(String::trim).distinct().toList();
        return normalized.isEmpty() ? null : String.join(",", normalized);
    }

    private List<String> parseTags(String tags) {
        if (!StringUtils.hasText(tags)) {
            return List.of();
        }
        return List.of(tags.split(",")).stream().filter(StringUtils::hasText).map(String::trim).toList();
    }

    private String normalizeFlag(String flag, String defaultValue) {
        String normalized = StringUtils.hasText(flag) ? flag.trim().toUpperCase() : defaultValue;
        if (!List.of("Y", "N").contains(normalized)) {
            throw new BusinessException("启用标志仅支持 Y 或 N");
        }
        return normalized;
    }

    private String trimToNull(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }

    private CompanyInfoResponse toResponse(CompanyInfo entity) {
        CompanyInfoResponse response = new CompanyInfoResponse();
        response.setCompanyId(entity.getCompanyId());
        response.setCompanyCode(entity.getCompanyCode());
        response.setCompanyName(entity.getCompanyName());
        response.setRegion(entity.getRegion());
        response.setRepresentativeOffice(entity.getRepresentativeOffice());
        response.setCountry(entity.getCountry());
        response.setDescription(entity.getDescription());
        response.setTags(parseTags(entity.getTags()));
        response.setEnabledFlag(entity.getEnabledFlag());
        response.setLastUpdateDate(entity.getLastUpdateDate());
        return response;
    }

    private CompanyTagResponse toTagResponse(CompanyTag entity) {
        CompanyTagResponse response = new CompanyTagResponse();
        response.setTagId(entity.getTagId());
        response.setTagValue(entity.getTagValue());
        response.setEnabledFlag(entity.getEnabledFlag());
        return response;
    }
}
