package com.smartarchive.countryregion.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smartarchive.common.exception.BusinessException;
import com.smartarchive.countryregion.domain.CountryRegion;
import com.smartarchive.countryregion.dto.CountryRegionResponse;
import com.smartarchive.countryregion.mapper.CountryRegionMapper;
import com.smartarchive.countryregion.service.CountryRegionService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class CountryRegionServiceImpl implements CountryRegionService {
    private final CountryRegionMapper countryRegionMapper;

    @Override
    public List<CountryRegionResponse> listByLevel(String regionLevel, String parentRegionCode) {
        String normalizedLevel = normalizeLevel(regionLevel);
        String normalizedParent = StringUtils.hasText(parentRegionCode) ? parentRegionCode.trim() : null;
        return countryRegionMapper.selectList(new LambdaQueryWrapper<CountryRegion>()
                .eq(CountryRegion::getDeleteFlag, "N")
                .eq(CountryRegion::getEnabledFlag, "Y")
                .eq(CountryRegion::getRegionLevel, normalizedLevel)
                .eq(StringUtils.hasText(normalizedParent), CountryRegion::getParentRegionCode, normalizedParent)
                .orderByAsc(CountryRegion::getSortOrder)
                .orderByAsc(CountryRegion::getRegionCode))
            .stream()
            .map(item -> CountryRegionResponse.builder()
                .regionLevel(item.getRegionLevel())
                .regionCode(item.getRegionCode())
                .regionName(item.getRegionName())
                .shortName(item.getShortName())
                .parentRegionCode(item.getParentRegionCode())
                .build())
            .toList();
    }

    private String normalizeLevel(String level) {
        if (!StringUtils.hasText(level)) {
            throw new BusinessException("regionLevel cannot be blank");
        }
        String value = level.trim().toUpperCase();
        if (!List.of("COUNTRY", "PROVINCE", "CITY").contains(value)) {
            throw new BusinessException("regionLevel only supports COUNTRY/PROVINCE/CITY");
        }
        return value;
    }
}
