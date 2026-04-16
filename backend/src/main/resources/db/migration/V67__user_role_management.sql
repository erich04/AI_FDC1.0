-- 用户角色与数据维度管理

-- 1. 系统用户表
CREATE TABLE IF NOT EXISTS fdc_user_t (
    user_id BIGSERIAL PRIMARY KEY,
    username VARCHAR(64) NOT NULL UNIQUE,
    real_name VARCHAR(128) NOT NULL,
    email VARCHAR(128),
    phone VARCHAR(32),
    status VARCHAR(20) DEFAULT 'ACTIVE' NOT NULL, -- ACTIVE, INACTIVE
    created_by BIGINT DEFAULT 1 NOT NULL,
    creation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    last_updated_by BIGINT DEFAULT 1 NOT NULL,
    last_update_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    delete_flag CHAR(1) DEFAULT 'N' NOT NULL
);

-- 2. 系统角色表
CREATE TABLE IF NOT EXISTS fdc_role_t (
    role_id BIGSERIAL PRIMARY KEY,
    role_code VARCHAR(64) NOT NULL UNIQUE,
    role_name VARCHAR(128) NOT NULL,
    role_name_en VARCHAR(128),
    description TEXT,
    enable_flag CHAR(1) DEFAULT 'Y' NOT NULL,
    created_by BIGINT DEFAULT 1 NOT NULL,
    creation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    last_updated_by BIGINT DEFAULT 1 NOT NULL,
    last_update_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

-- 3. 用户-角色-维度映射表 (RBAC + Data Scope)
CREATE TABLE IF NOT EXISTS fdc_user_role_scope_t (
    user_role_scope_id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    role_code VARCHAR(64) NOT NULL,
    dimension_code VARCHAR(64), -- ARCHIVED_ENTITY, BUSINESS_MODULE, E-FLOW_TYPE, START_PERIOD
    dimension_value VARCHAR(255), -- 具体维度的取值，如某个主体的 ID
    created_by BIGINT DEFAULT 1 NOT NULL,
    creation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    last_updated_by BIGINT DEFAULT 1 NOT NULL,
    last_update_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT fk_user_role_scope_user FOREIGN KEY (user_id) REFERENCES fdc_user_t(user_id)
);

CREATE INDEX idx_fdc_user_role_scope_n1 ON fdc_user_role_scope_t(user_id, role_code);

-- 4. 插入预置角色数据 (源自 03_Security.md)
INSERT INTO fdc_role_t (role_code, role_name, role_name_en, description) VALUES
('FDC_ROLE_FIN_DOC_DATA_MAINTAINER', '应归档数据维护员', 'Archived Data Maintainer', '应归档数据创建、调整；文档查询、导出'),
('FDC_ROLE_DOC_LEDGER_CLERK', '账管员', 'Account Administrator', '台账调整'),
('FDC_ROLE_DOC_QUERY', '文档查询人员', 'Document Query User', '查询档案详细信息；在线预览附件、下载附件'),
('FDC_ROLE_DOC_QUERY_EXPORT_STAFF', '文档查询人员(导出)', 'Document Query & Export Staff', '查询并导出文档详细信息'),
('FDC_ROLE_BORROW_LIAISON', '文档借阅接口人', 'Document Borrowing Liaison', '借阅电子流总接口人；异常电子流干预'),
('FDC_ROLE_WORKFLOW_QUERY', '电子流查询人员', 'E-Flow Query User', '电子流查询'),
('FDC_ROLE_DOC_ADMIN', '文档管理员', 'Document Administrator', '文档接收核销、成册、提交入库；转库、出库等'),
('FDC_ROLE_DOC_MANAGER', '文档经理', 'Document Manager', '审批转库、搬迁、盘点报告、台账调整等'),
('FDC_ROLE_REPORT_ANALYST', '报表分析员', 'Report Analyst', 'KPI、明细导出、文档量统计、库房台账审视'),
('FDC_ROLE_IT_SUPPORT', 'IT Support', 'IT Support', '日常查异常数据；lookup 配置等'),
('FDC_ROLE_SYS_ADMIN', '系统管理员', 'System Administrator', '维护组织、库房库位信息等'),
('FDC_ROLE_END_USER', '普通用户', 'End User', '查档案头信息；各项申请等'),
('FDC_ROLE_BIZ_ADMIN', '业务管理员', 'Business Administrator', '维护营业执照和投标文档')
ON CONFLICT (role_code) DO NOTHING;

-- 5. 插入测试用户
INSERT INTO fdc_user_t (username, real_name, email) VALUES
('admin', '系统管理员', 'admin@fdc.com'),
('zhangsan', '张三', 'zhangsan@fdc.com'),
('lisi', '李四', 'lisi@fdc.com')
ON CONFLICT (username) DO NOTHING;

-- 6. 为 admin 赋予系统管理员角色
INSERT INTO fdc_user_role_scope_t (user_id, role_code)
SELECT user_id, 'FDC_ROLE_SYS_ADMIN' FROM fdc_user_t WHERE username = 'admin'
ON CONFLICT DO NOTHING;
