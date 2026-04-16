package com.smartarchive.countryregion.controller;

import com.smartarchive.common.api.ApiResponse;
import com.smartarchive.countryregion.dto.CountryRegionResponse;
import com.smartarchive.countryregion.service.CountryRegionService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/base-data/country-regions")
@RequiredArgsConstructor
public class CountryRegionController {
    private final CountryRegionService countryRegionService;

    @GetMapping
    public ApiResponse<List<CountryRegionResponse>> listByLevel(@RequestParam String regionLevel,
                                                                @RequestParam(required = false) String parentRegionCode) {
        return ApiResponse.success(countryRegionService.listByLevel(regionLevel, parentRegionCode));
    }
}
