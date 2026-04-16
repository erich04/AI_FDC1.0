package com.smartarchive.countryregion.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CountryRegionResponse {
    private String regionLevel;
    private String regionCode;
    private String regionName;
    private String shortName;
    private String parentRegionCode;
}
