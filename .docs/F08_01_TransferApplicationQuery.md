# 移交申请查询功能设计方案

## 1. 功能概述

移交申请查询功能是档案管理系统中的重要组成部分，用于查询、筛选和管理档案移交申请记录。该功能允许用户通过多种条件组合查询移交申请，并查看详细信息，支持档案管理流程的透明化和可追溯性。

## 2. 核心流程

### 2.1 查询流程

1. **用户输入查询条件**：用户在查询页面填写各种筛选条件
2. **提交查询请求**：用户点击查询按钮，前端将筛选条件发送至后端
3. **后端处理**：后端根据条件执行数据库查询，支持分页
4. **返回结果**：后端返回查询结果，前端展示在表格中
5. **查看详情**：用户可点击查看按钮，查看具体移交申请的详细信息

### 2.2 数据流

```
前端页面 → API调用 → 后端控制器 → 服务层 → 数据访问层 → 数据库
```

## 3. 界面设计

### 3.1 查询页面

#### 3.1.1 筛选区域

- **基本筛选条件**：
  - 文档业务编码（模糊查询）
  - 公司/项目（下拉选择）
  - 业务模块（下拉选择）
  - 档期范围（日期选择器）
  - 申请人（下拉选择）
  - 申请单号（模糊查询）
  - 申请日期范围（日期选择器）
  - 申请状态（下拉选择）
  - 载体类型（下拉选择）
  - 差异原因（下拉选择）

- **高级筛选条件**（展开/收起）：
  - 移交方式（下拉选择）
  - 邮寄方式（下拉选择）
  - 邮寄单号（模糊查询）
  - 接收人（下拉选择）
  - 册号（模糊查询）

#### 3.1.2 操作区域

- 发起移交按钮
- 导出按钮（预留功能）
- 查询/重置按钮

#### 3.1.3 结果展示区域

- 数据表格，包含以下列：
  - 序号
  - 申请单号
  - 文档类型
  - 申请人
  - 申请日期
  - 申请状态（标签形式）
  - 接收人
  - 邮寄方式
  - 邮寄单号
  - 操作（查看、编辑）

- 分页控件

#### 3.1.4 详情弹窗

- 基本信息（申请单号、申请状态、申请人、申请日期等）
- 申请明细表格（文档业务编码、文档名称、公司/项目、业务模块、档期等）

### 3.2 界面交互

- 支持筛选条件的展开/收起
- 支持表格列的排序
- 支持分页切换
- 查看详情时弹出模态框
- 草稿和驳回状态的申请可编辑

## 4. 数据模型

### 4.1 核心实体

#### 4.1.1 移交申请表（fdc_application_t）

| 字段名 | 数据类型 | 描述 |
|-------|---------|------|
| application_id | BIGINT | 申请ID（主键） |
| application_number | VARCHAR | 申请单号 |
| applicant | BIGINT | 申请人ID |
| application_date | DATETIME | 申请日期 |
| department | VARCHAR | 部门 |
| document_type_code | VARCHAR | 文档类型编码 |
| apply_method | VARCHAR | 申请方式 |
| express_type | VARCHAR | 快递类型 |
| express_number | VARCHAR | 快递单号 |
| document_recipient | BIGINT | 文档接收人ID |
| handover_form | VARCHAR | 交接表单 |
| carrier_type | VARCHAR | 载体类型 |
| application_status | VARCHAR | 申请状态 |
| status | VARCHAR | 状态（草稿/已提交） |
| diff_reason_code | VARCHAR | 差异原因编码 |
| application_description | VARCHAR | 申请描述 |
| enable_flag | VARCHAR | 启用标志 |
| delete_flag | VARCHAR | 删除标志 |
| tenantid | BIGINT | 租户ID |

#### 4.1.2 移交申请详情表（fdc_application_detail_t）

| 字段名 | 数据类型 | 描述 |
|-------|---------|------|
| application_detail_id | BIGINT | 详情ID（主键） |
| application_id | BIGINT | 申请ID（外键） |
| doc_busi_no | VARCHAR | 文档业务编码 |
| doc_name | VARCHAR | 文档名称 |
| busi_module_code | VARCHAR | 业务模块编码 |
| company_project_code | VARCHAR | 公司项目编码 |
| arch_place_alpha2_code | VARCHAR | 归档地点编码 |
| start_arch_period | VARCHAR | 开始归档周期 |
| end_arch_period | VARCHAR | 结束归档周期 |
| arch_type_code | VARCHAR | 归档类型编码 |
| carrier_type | VARCHAR | 载体类型 |
| doc_generation_date | DATE | 文档生成日期 |
| arch_copies | DECIMAL | 归档份数 |
| remark | VARCHAR | 备注 |
| description | VARCHAR | 描述 |
| catalog_volume_no | VARCHAR | 目录卷号 |
| tenantid | BIGINT | 租户ID |

#### 4.1.3 移交申请扩展表（fdc_application_ext_t）

| 字段名 | 数据类型 | 描述 |
|-------|---------|------|
| ext_id | BIGINT | 扩展ID（主键） |
| object_id | BIGINT | 对象ID（详情ID） |
| master_id | BIGINT | 主ID（申请ID） |
| attr1-attr300 | VARCHAR/DECIMAL/DATE | 扩展属性 |
| tenantid | BIGINT | 租户ID |

#### 4.1.4 归档附件表（fdc_archive_attachment_t）

| 字段名 | 数据类型 | 描述 |
|-------|---------|------|
| attachment_id | BIGINT | 附件ID（主键） |
| biz_domain | VARCHAR | 业务域 |
| application_id | BIGINT | 申请ID |
| application_detail_id | BIGINT | 申请详情ID |
| attachment_role | VARCHAR | 附件角色 |
| attachment_type_code | VARCHAR | 附件类型编码 |
| file_name | VARCHAR | 文件名 |
| storage_path | VARCHAR | 存储路径 |
| file_size | BIGINT | 文件大小 |
| tenantid | BIGINT | 租户ID |

### 4.2 数据传输对象

#### 4.2.1 查询相关DTO

- **TransferApplicationRecordQuery**：查询条件
- **TransferApplicationRecordPageCommand**：分页查询命令
- **TransferApplicationRecordPageResponse**：分页查询响应
- **TransferApplicationRecordRowResponse**：查询结果行

#### 4.2.2 详情相关DTO

- **TransferApplicationResponse**：移交申请响应
- **TransferApplicationDetailResponse**：移交申请详情响应
- **TransferApplicationDetailAttachmentResponse**：详情附件响应
- **TransferApplicationExtValueResponse**：扩展值响应

## 5. 接口设计

### 5.1 核心接口

#### 5.1.1 移交申请查询接口

- **URL**：`POST /api/archive-management/transfer-applications/search-page`
- **请求体**：
  ```json
  {
    "filter": {
      "docBusiNo": "string",
      "companyProjectCode": "string",
      "busiModuleCode": "string",
      "archPeriodRange": ["2023-01-01", "2023-12-31"],
      "applicant": 1,
      "applicationNumber": "string",
      "applicationDateRange": ["2023-01-01", "2023-12-31"],
      "applicationStatus": "string",
      "carrierType": "string",
      "diffReasonCode": "string",
      "applyMethod": "string",
      "expressType": "string",
      "expressNumber": "string",
      "documentRecipient": 1,
      "catalogVolumeNo": "string"
    },
    "page": 1,
    "pageSize": 20,
    "tenantid": 1
  }
  ```
- **响应**：
  ```json
  {
    "code": 200,
    "message": "success",
    "data": {
      "records": [
        {
          "applicationId": 1,
          "applicationNumber": "TRN-2023-001",
          "documentTypeCode": "DOC001",
          "documentTypeName": "合同文档",
          "applicant": 1,
          "applicantName": "张三",
          "applicationDate": "2023-06-01T10:00:00",
          "applicationStatus": "APPROVED",
          "documentRecipient": 2,
          "documentRecipientName": "李四",
          "expressType": "SF",
          "expressNumber": "SF1234567890"
        }
      ],
      "total": 100,
      "pages": 5,
      "page": 1,
      "pageSize": 20
    }
  }
  ```

#### 5.1.2 移交申请详情接口

- **URL**：`GET /api/archive-management/transfer-applications/{applicationId}`
- **响应**：
  ```json
  {
    "code": 200,
    "message": "success",
    "data": {
      "applicationId": 1,
      "applicationNumber": "TRN-2023-001",
      "applicant": 1,
      "applicationDate": "2023-06-01T10:00:00",
      "department": "法务部",
      "documentTypeCode": "DOC001",
      "applyMethod": "MAIL",
      "expressType": "SF",
      "expressNumber": "SF1234567890",
      "documentRecipient": 2,
      "applicationStatus": "APPROVED",
      "details": [
        {
          "applicationDetailId": 1,
          "applicationId": 1,
          "docBusiNo": "BUS001",
          "docName": "合同A",
          "busiModuleCode": "MODULE001",
          "companyProjectCode": "PROJ001",
          "startArchPeriod": "2023-01",
          "endArchPeriod": "2023-06",
          "catalogVolumeNo": "VOL001",
          "attachments": []
        }
      ]
    }
  }
  ```

### 5.2 辅助接口

- **附件上传接口**：`POST /api/archive-management/transfer-applications/{applicationId}/details/{detailId}/attachments`
- **附件列表接口**：`GET /api/archive-management/transfer-applications/{applicationId}/details/{detailId}/attachments`
- **附件下载接口**：`GET /api/archive-management/transfer-applications/{applicationId}/details/{detailId}/attachments/{attachmentId}/download`

## 6. 处理逻辑详解

### 6.1 按钮交互与接口调用关系

| 按钮操作 | 触发事件 | 调用接口 | 说明 |
|---------|---------|---------|------|
| 查询按钮 | 点击事件 | `POST /api/archive-management/transfer-applications/search-page` | 根据筛选条件分页查询移交申请列表 |
| 重置按钮 | 点击事件 | `POST /api/archive-management/transfer-applications/search-page` | 清空筛选条件后重新查询 |
| 查看按钮 | 点击事件 | `GET /api/archive-management/transfer-applications/{applicationId}` | 查询单条移交申请详情 |
| 编辑按钮 | 点击事件 | 路由跳转至编辑页面 | 跳转至移交申请编辑页面，URL带applicationId参数 |
| 发起移交按钮 | 点击事件 | 路由跳转至创建页面 | 跳转至移交申请创建页面 |
| 导出按钮 | 点击事件 | 预留功能 | 待开发 |
| 分页切换 | 页码变更 | `POST /api/archive-management/transfer-applications/search-page` | 更新页码参数后重新查询 |
| 每页条数变更 | 条数变更 | `POST /api/archive-management/transfer-applications/search-page` | 更新pageSize参数后重新查询 |

### 6.2 查询接口处理逻辑

#### 6.2.1 接口入口

**控制器方法**：`TransferApplicationController.searchPage()`

**请求路径**：`POST /api/archive-management/transfer-applications/search-page`

**处理流程**：
1. 接收前端传入的 `TransferApplicationRecordPageCommand` 对象
2. 调用 `TransferApplicationService.searchPage()` 方法执行查询
3. 返回 `TransferApplicationRecordPageResponse` 分页结果

#### 6.2.2 服务层处理逻辑

**服务方法**：`TransferApplicationServiceImpl.searchPage()`

**处理步骤**：

1. **参数预处理**：
   - 提取筛选条件对象 `TransferApplicationRecordQuery`
   - 获取租户ID（默认为1）
   - 计算分页偏移量：`offset = (page - 1) * pageSize`

2. **日期范围条件标准化**：
   - **申请日期范围**（applicationDateRange）：
     - 起始日期：转换为当天 00:00:00
     - 结束日期：转换为当天 23:59:59
   - **档期范围**（archPeriodRange）：
     - 格式化为 yyyy-MM 格式

3. **执行查询**：
   - 调用 `countTransferApplicationRecords()` 统计总数
   - 调用 `selectTransferApplicationRecordPage()` 查询分页数据

4. **结果组装**：
   - 查询文档类型名称映射
   - 转换为响应对象列表
   - 计算总页数

#### 6.2.3 数据访问层查询逻辑

**Mapper方法**：`TransferApplicationMapper.selectTransferApplicationRecordPage()`

**查询主表**：`fdc_application_t`（别名 a）

**查询字段**：
- `a.*`（所有字段）

**基础过滤条件**：
- `a.delete_flag = 'N'`（未删除）
- `a.enable_flag = 'Y'`（已启用）
- `a.tenantid = #{tenantid}`（租户隔离）

**动态筛选条件映射**：

| 前端参数 | 查询方式 | 涉及表 | 查询字段 | 条件说明 |
|---------|---------|-------|---------|---------|
| companyProjectCode | EXISTS子查询 | fdc_application_detail_t | company_project_code | 精确匹配公司项目编码 |
| applicant | 直接条件 | fdc_application_t | applicant | 精确匹配申请人ID |
| applicationNumber | 直接条件 | fdc_application_t | application_number | LIKE模糊查询 |
| applicationDateStart | 直接条件 | fdc_application_t | application_date | 大于等于起始日期 |
| applicationDateEnd | 直接条件 | fdc_application_t | application_date | 小于等于结束日期 |
| applicationStatus | 直接条件 | fdc_application_t | application_status | 精确匹配申请状态 |
| carrierType | 直接条件 | fdc_application_t | carrier_type | 精确匹配载体类型 |
| diffReasonCode | 直接条件 | fdc_application_t | diff_reason_code | 精确匹配差异原因 |
| applyMethod | 直接条件 | fdc_application_t | apply_method | 精确匹配移交方式 |
| expressType | 直接条件 | fdc_application_t | express_type | 精确匹配邮寄方式 |
| expressNumber | 直接条件 | fdc_application_t | express_number | LIKE模糊查询 |
| documentRecipient | 直接条件 | fdc_application_t | document_recipient | 精确匹配接收人ID |
| docBusiNo | EXISTS子查询 | fdc_application_detail_t | doc_busi_no | LIKE模糊查询 |
| busiModuleCode | EXISTS子查询 | fdc_application_detail_t | busi_module_code | 精确匹配业务模块 |
| archPeriodStart/End | EXISTS子查询 | fdc_application_detail_t | start_arch_period, end_arch_period | 档期范围交集判断 |
| catalogVolumeNo | EXISTS子查询 | fdc_application_detail_t | catalog_volume_no | LIKE模糊查询 |

**排序规则**：
- 主排序：`application_date DESC NULLS LAST`（申请日期降序，空值排后）
- 次排序：`application_id DESC`（申请ID降序）

**分页方式**：
- 使用 `LIMIT #{limit} OFFSET #{offset}` 实现分页

#### 6.2.4 EXISTS子查询逻辑说明

对于涉及详情表字段的查询条件，使用EXISTS子查询实现：

```sql
EXISTS (
    SELECT 1 FROM fdc_application_detail_t d
    WHERE d.application_id = a.application_id
      AND d.delete_flag = 'N' 
      AND d.enable_flag = 'Y' 
      AND d.tenantid = #{tenantid}
      AND d.{字段名} {条件操作符} #{条件值}
)
```

**档期范围查询的特殊逻辑**：
```sql
AND (d.start_arch_period IS NULL OR d.start_arch_period <= #{archPeriodEnd})
AND (d.end_arch_period IS NULL OR d.end_arch_period >= #{archPeriodStart})
```
- 判断档期范围是否有交集
- 允许档期为空值

### 6.3 详情接口处理逻辑

#### 6.3.1 接口入口

**控制器方法**：`TransferApplicationController.detail()`

**请求路径**：`GET /api/archive-management/transfer-applications/{applicationId}`

#### 6.3.2 服务层处理逻辑

**服务方法**：`TransferApplicationServiceImpl.detail()`

**处理步骤**：

1. **查询主表数据**：
   - 表：`fdc_application_t`
   - 条件：`application_id = #{applicationId} AND delete_flag = 'N'`
   - 若不存在则抛出业务异常

2. **加载详情列表**：
   - 表：`fdc_application_detail_t`
   - 条件：`application_id = #{applicationId} AND delete_flag = 'N'`
   - 排序：`application_detail_id ASC`

3. **加载扩展字段值**：
   - 表：`fdc_application_ext_t`
   - 条件：`master_id = #{applicationId} AND tenantid = #{tenantid} AND delete_flag = 'N'`
   - 关联扩展字段配置表获取字段元信息

4. **加载附件列表**：
   - 表：`fdc_archive_attachment_t`
   - 条件：`biz_domain = 'TRANSFER_APPLICATION' AND application_id = #{applicationId} AND delete_flag = 'N'`
   - 排序：`creation_date DESC`

5. **组装响应对象**：
   - 将详情、扩展字段、附件关联到主对象

### 6.4 扩展字段查询逻辑详解

#### 6.4.1 扩展字段存储结构

扩展字段采用动态列存储方式，存储在 `fdc_application_ext_t` 表中：

| 字段范围 | 数据类型 | 用途 |
|---------|---------|------|
| attr1 ~ attr100 | VARCHAR | 存储字符串类型扩展字段 |
| attr101 ~ attr200 | DECIMAL | 存储数值类型扩展字段 |
| attr201 ~ attr300 | DATE | 存储日期类型扩展字段 |

#### 6.4.2 扩展字段配置获取

**查询表**：`fdc_archive_ext_field_config_t`

**查询条件**：
- `document_type_code = #{documentTypeCode}`（按文档类型过滤）
- `delete_flag = 'N'`（未删除）
- `enabled_flag = 'Y'`（已启用）

**配置字段**：
- `field_code`：字段编码
- `field_name`：字段名称
- `dict_category_code`：对应数据库列名（如 attr1, attr101 等）
- `required_flag`：是否必填

#### 6.4.3 扩展字段值查询流程

1. **查询扩展数据行**：
   ```sql
   SELECT * FROM fdc_application_ext_t
   WHERE master_id = #{applicationId}
     AND tenantid = #{tenantid}
     AND delete_flag = 'N'
   ORDER BY ext_id ASC
   ```

2. **建立列名到字段编码的映射**：
   - 从扩展字段配置中提取 `dict_category_code` 和 `field_code`
   - 验证列名格式符合 `attr[1-300]` 规则

3. **解析扩展值**：
   - 遍历每条扩展数据行
   - 根据 `object_id`（详情ID）分组
   - 提取非空的 attr 列值
   - 转换为 `{fieldCode: value}` 格式

4. **返回结果结构**：
   ```json
   {
     "detailId1": [
       { "fieldCode": "FIELD001", "value": "值1" },
       { "fieldCode": "FIELD002", "value": "值2" }
     ],
     "detailId2": [
       { "fieldCode": "FIELD001", "value": "值3" }
     ]
   }
   ```

#### 6.4.4 扩展字段数据类型转换规则

| 列名范围 | 存储类型 | 转换规则 |
|---------|---------|---------|
| attr1 ~ attr100 | VARCHAR | 直接存储字符串 |
| attr101 ~ attr200 | DECIMAL | 转换为 BigDecimal，失败则报错 |
| attr201 ~ attr300 | DATE | 解析为 yyyy-MM-dd 格式日期，失败则报错 |

### 6.5 查询条件前端处理逻辑

#### 6.5.1 条件构建

前端在提交查询前，会对筛选条件进行清洗：

```typescript
function buildFilterPayload(): TransferApplicationRecordQuery {
  const f: TransferApplicationRecordQuery = {}
  // 仅包含非空条件
  if (searchForm.docBusiNo) f.docBusiNo = searchForm.docBusiNo.trim()
  if (searchForm.companyProjectCode) f.companyProjectCode = searchForm.companyProjectCode
  if (searchForm.busiModuleCode) f.busiModuleCode = searchForm.busiModuleCode
  // 日期范围需长度为2
  if (searchForm.archPeriodRange?.length === 2) f.archPeriodRange = [...searchForm.archPeriodRange]
  // ... 其他条件
  return f
}
```

#### 6.5.2 字典数据加载

页面初始化时，并行加载多个字典数据：

| 字典编码 | 用途 |
|---------|------|
| FUNCTION_MODULE | 业务模块选项 |
| TRANSFER_APPLICATION_STATUS | 申请状态选项 |
| TRANSFER_DIFF_REASON | 差异原因选项 |
| TRANSFER_APPLY_METHOD | 移交方式选项 |
| TRANSFER_EXPRESS_TYPE | 邮寄方式选项 |

#### 6.5.3 默认值处理

- 租户ID默认为 1
- 页码默认为 1
- 每页条数默认为 20
- 可选每页条数：20, 50, 100, 200

## 7. 业务规则

### 7.1 查询规则

1. **租户隔离**：查询结果仅限于当前租户的移交申请
2. **删除标记**：只查询未删除（delete_flag='N'）的记录
3. **启用标记**：只查询已启用（enable_flag='Y'）的记录
4. **模糊查询**：申请单号、快递单号、文档业务编码、册号支持模糊查询
5. **范围查询**：申请日期、档期支持范围查询
6. **关联查询**：支持通过详情表字段进行查询（如公司项目、业务模块等）

### 7.2 状态流转规则

| 状态 | 描述 | 可执行操作 |
|------|------|-----------|
| DRAFT | 草稿 | 编辑、提交 |
| SUBMITTED | 已提交 | 查看 |
| RUNNING | 处理中 | 查看 |
| APPROVED | 已通过 | 查看 |
| REJECTED | 已驳回 | 编辑、重新提交 |

### 7.3 权限规则

1. **查看权限**：所有档案管理用户可查看移交申请
2. **编辑权限**：仅申请人和管理员可编辑草稿或驳回状态的申请
3. **提交权限**：仅申请人可提交申请
4. **审批权限**：仅接收人可审批申请

## 8. 性能考虑

1. **索引优化**：对常用查询字段（如申请单号、申请日期、申请人等）建立索引
2. **分页查询**：使用LIMIT/OFFSET进行分页，避免一次性加载大量数据
3. **查询条件优化**：合理使用EXISTS子查询，避免复杂JOIN操作
4. **缓存机制**：对字典数据（如状态、类型等）进行缓存，减少数据库查询
5. **异步处理**：对附件上传等耗时操作采用异步处理

## 9. 扩展性考虑

1. **自定义字段**：支持通过扩展表（fdc_application_ext_t）添加自定义字段
2. **多租户支持**：通过tenantid字段支持多租户架构
3. **工作流集成**：与工作流系统集成，支持审批流程
4. **API版本控制**：预留API版本控制机制，便于后续功能扩展
5. **国际化支持**：前端界面支持多语言切换

## 10. 实现要点

1. **前端实现**：
   - 使用Vue 3 + TypeScript构建
   - 使用Element Plus组件库
   - 实现响应式布局，适配不同屏幕尺寸
   - 优化用户交互体验，如加载状态、错误提示等

2. **后端实现**：
   - 使用Spring Boot + MyBatis Plus
   - 实现RESTful API
   - 采用分层架构（控制器、服务层、数据访问层）
   - 实现事务管理和异常处理

3. **数据库实现**：
   - 使用关系型数据库（如MySQL）
   - 合理设计表结构和索引
   - 实现数据隔离和安全访问

## 11. 总结

移交申请查询功能是档案管理系统中的重要组成部分，通过本设计方案，实现了一个功能完整、性能良好、用户体验优秀的查询系统。该功能支持多种查询条件组合，提供详细的申请信息展示，并与工作流系统集成，为档案管理工作提供了有力的支持。

后续可考虑添加导出功能、高级搜索、数据可视化等功能，进一步提升系统的实用性和用户体验。