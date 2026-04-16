package com.smartarchive.countryregion.service;

import com.smartarchive.countryregion.dto.CountryRegionResponse;
import java.util.List;

public interface CountryRegionService {
    List<CountryRegionResponse> listByLevel(String regionLevel, String parentRegionCode);
}
