package com.smartarchive.departmentsignatory.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("fdc_department_signatory_t")
public class DepartmentSignatory {
    @TableId(value = "department_signatory_id", type = IdType.AUTO)
    private Long departmentSignatoryId;
    private String firstLevelDepartment;
    private String secondLevelDepartment;
    private String thirdLevelDepartment;
    private String fourthLevelDepartment;
    private String signatories;
    @TableLogic(value = "N", delval = "Y")
    private String deleteFlag;
    private Long createdBy;
    private LocalDateTime creationDate;
    private Long lastUpdatedBy;
    private LocalDateTime lastUpdateDate;
}
