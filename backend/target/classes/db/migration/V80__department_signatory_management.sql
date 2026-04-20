CREATE TABLE IF NOT EXISTS fdc_department_signatory_t (
    department_signatory_id BIGSERIAL PRIMARY KEY,
    first_level_department VARCHAR(100) NOT NULL,
    second_level_department VARCHAR(100),
    third_level_department VARCHAR(100),
    fourth_level_department VARCHAR(100),
    signatories VARCHAR(1000) NOT NULL,
    delete_flag CHAR(1) NOT NULL DEFAULT 'N',
    created_by BIGINT NOT NULL DEFAULT 1,
    creation_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_updated_by BIGINT NOT NULL DEFAULT 1,
    last_update_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT ck_fdc_department_signatory_t_delete CHECK (delete_flag IN ('N', 'Y'))
);

CREATE INDEX IF NOT EXISTS idx_fdc_department_signatory_tn1
    ON fdc_department_signatory_t (delete_flag, first_level_department, second_level_department, third_level_department, fourth_level_department);

INSERT INTO fdc_department_signatory_t (
    first_level_department,
    second_level_department,
    third_level_department,
    fourth_level_department,
    signatories
)
SELECT '集团总部', '法务合规部', '合同管理组', NULL, '郑审核,郭审批'
WHERE NOT EXISTS (
    SELECT 1 FROM fdc_department_signatory_t
    WHERE first_level_department = '集团总部'
      AND second_level_department = '法务合规部'
      AND third_level_department = '合同管理组'
      AND delete_flag = 'N'
);

INSERT INTO fdc_department_signatory_t (
    first_level_department,
    second_level_department,
    third_level_department,
    fourth_level_department,
    signatories
)
SELECT '集团总部', '风险管理部', '投后风控组', NULL, '陈借出审批,王借出审批'
WHERE NOT EXISTS (
    SELECT 1 FROM fdc_department_signatory_t
    WHERE first_level_department = '集团总部'
      AND second_level_department = '风险管理部'
      AND third_level_department = '投后风控组'
      AND delete_flag = 'N'
);
