package com.smartarchive.security.dto;

import lombok.Data;

@Data
public class UserDutyProfileResponse {
    private String userName;
    private String dutyDepartment;
    private String workCountryCode;
}
