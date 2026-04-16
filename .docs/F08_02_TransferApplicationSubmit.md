# 移交申请提交功能设计方案

## 1. 功能概述

移交申请提交功能是档案管理系统中用于创建和提交档案移交申请的核心功能。该功能允许用户填写移交申请信息，添加待归档文档，并将申请提交给审批人进行审批。

## 2. 页面逻辑

### 2.1 页面结构

页面采用折叠面板布局，分为两个主要部分：

#### 2.1.1 申请头面板

包含以下表单字段：
- **申请人**：当前登录用户，不可编辑
- **申请人部门**：当前登录用户所属部门，不可编辑
- **申请日期**：当前日期，不可编辑
- **业务模块**：下拉选择，可选择文档类型树中的节点
- **移交方式**：下拉选择，支持"直接移交"和"邮寄"
- **邮寄方式**：下拉选择，仅当移交方式为"邮寄"时显示
- **邮寄单号**：文本输入，仅当移交方式为"邮寄"时显示
- **文档接收人**：下拉选择，可选择系统中的用户
- **移交形式**：下拉选择，仅当业务模块为"会计凭证补充资料"时显示
- **申请描述**：多行文本输入

#### 2.1.2 申请行面板

包含一个可编辑的表格，用于添加待移交的文档：
- **操作列**：支持上传附件和删除行
- **公司**：下拉选择
- **文档业务编码**：文本输入
- **文档名称**：文本输入
- **业务模块**：下拉选择
- **归档地**：文本输入
- **开始档期**：日期选择器，格式为YYYY-MM
- **结束档期**：日期选择器，格式为YYYY-MM
- **文档生成日期**：日期选择器，格式为YYYY-MM-DD
- **载体类型**：下拉选择
- **扩展字段**：根据业务模块动态显示
- **份数**：数字输入，最小值为1
- **备注**：文本输入
- **描述**：多行文本输入

#### 2.1.3 底部操作栏

包含三个按钮：
- **取消**：返回查询页面
- **保存**：保存为草稿
- **提交**：提交申请并启动审批流程

### 2.2 页面交互

#### 2.2.1 业务模块选择

- 选择业务模块后，会动态加载该模块的扩展字段
- 扩展字段会显示在申请行表格中
- 如果选择的是"会计凭证补充资料"，会显示"移交形式"字段

#### 2.2.2 移交方式选择

- 当选择"邮寄"时，会显示"邮寄方式"和"邮寄单号"字段
- 当选择"直接移交"时，会隐藏"邮寄方式"和"邮寄单号"字段

#### 2.2.3 申请行操作

- **新增行**：点击"新增"按钮添加新的申请行
- **删除行**：点击"删除"按钮删除当前行
- **上传附件**：点击"上传附件"按钮打开附件上传对话框
- **添加待归档数据**：点击"添加待归档数据"按钮打开待归档数据选择对话框

#### 2.2.4 待归档数据选择

- 可以根据文档名称、业务编码和公司进行筛选
- 选择后点击"添加"按钮将选中的数据添加到申请行
- 已添加的数据不会重复显示

## 3. 交互逻辑

### 3.1 按钮交互

| 按钮 | 交互逻辑 | 调用接口 |
|------|---------|---------|
| **保存** | 验证表单字段，保存为草稿 | `POST /api/archive-management/transfer-applications` 或 `PUT /api/archive-management/transfer-applications/{applicationId}` |
| **提交** | 验证表单字段，提交申请并启动审批流程 | `POST /api/archive-management/transfer-applications` 或 `PUT /api/archive-management/transfer-applications/{applicationId}` |
| **取消** | 返回查询页面 | 无（路由跳转） |
| **新增** | 添加新的申请行 | 无 |
| **删除** | 删除当前申请行 | 无 |
| **上传附件** | 打开附件上传对话框 | 无 |
| **添加待归档数据** | 打开待归档数据选择对话框 | 无 |
| **查询**（待归档数据） | 查询待归档数据 | `POST /api/archive-management/archives/query` |
| **添加**（待归档数据） | 将选中的数据添加到申请行 | 无 |
| **下载附件** | 下载指定附件 | `GET /api/archive-management/transfer-applications/{applicationId}/details/{detailId}/attachments/{attachmentId}/download` |

### 3.2 表单验证

#### 3.2.1 申请头验证

- **业务模块**：必填
- **移交方式**：必填
- **文档接收人**：必填
- **邮寄方式**：当移交方式为"邮寄"时必填
- **邮寄单号**：当移交方式为"邮寄"时必填

#### 3.2.2 申请行验证

- **公司**：必填
- **文档业务编码**：必填
- **文档名称**：必填
- **业务模块**：必填
- **归档地**：必填
- **开始档期**：必填
- **结束档期**：必填
- **载体类型**：必填
- **份数**：必填，最小值为1
- **扩展字段**：根据配置可能必填

### 3.3 待归档数据查询逻辑

#### 3.3.1 查询条件

- **业务模块**：与申请头选择的业务模块一致
- **文档名称**：模糊查询
- **业务编码**：模糊查询
- **公司**：精确查询
- **排除已提交**：排除已提交的待归档数据

#### 3.3.2 查询接口

- **URL**：`POST /api/archive-management/archives/query`
- **请求体**：
  ```json
  {
    "busiModuleCode": "MODULE001",
    "documentName": "合同",
    "businessCode": "BUS001",
    "companyProjectCode": "PROJ001",
    "excludeSubmittedTransferApplied": true
  }
  ```

#### 3.3.3 查询结果

返回待归档数据列表，包含以下字段：
- **businessCode**：文档业务编码
- **documentName**：文档名称
- **companyProjectCode**：公司项目编码
- **archiveTypeCode**：业务模块编码
- **busiModuleCode**：业务模块编码
- **carrierTypeCode**：载体类型编码
- **archiveDestination**：归档地
- **beginPeriod**：开始档期
- **endPeriod**：结束档期
- **documentDate**：文档生成日期
- **remark**：备注
- **extValues**：扩展字段值

### 3.4 待归档数据添加逻辑

#### 3.4.1 数据转换

将待归档数据转换为申请行数据：

| 待归档数据字段 | 申请行字段 | 转换规则 |
|--------------|---------|---------|
| businessCode | docBusiNo | 直接赋值 |
| documentName | docName | 直接赋值 |
| companyProjectCode | companyProjectCode | 直接赋值 |
| busiModuleCode | busiModuleCode | 直接赋值 |
| carrierTypeCode | carrierType | 直接赋值，默认取第一个载体类型 |
| archiveDestination | archPlaceAlpha2Code | 直接赋值 |
| beginPeriod | startArchPeriod | 直接赋值 |
| endPeriod | endArchPeriod | 直接赋值 |
| documentDate | docGenerationDate | 直接赋值 |
| remark | remark | 直接赋值 |
| extValues | extValues | 映射到对应的扩展字段 |

#### 3.4.2 不可编辑字段

通过待归档数据添加的申请行，以下字段不可编辑：

- **公司**：只读
- **文档业务编码**：只读
- **文档名称**：只读
- **业务模块**：只读
- **开始档期**：只读
- **结束档期**：只读
- **文档生成日期**：只读
- **载体类型**：只读
- **扩展字段**：只读
- **份数**：只读（默认1份）
- **备注**：只读

#### 3.4.3 可编辑字段

以下字段仍可编辑：

- **归档地**：可编辑
- **描述**：可编辑

### 3.5 状态管理

#### 3.5.1 草稿状态

- 保存为草稿时，申请状态为"DRAFT"
- 草稿可以随时编辑
- 草稿不会启动审批流程

#### 3.5.2 提交状态

- 提交时，申请状态为"SUBMITTED"
- 提交后会启动审批流程
- 提交后不能编辑，除非被驳回

## 4. 接口逻辑

### 4.1 接口定义

#### 4.1.1 创建移交申请

- **URL**：`POST /api/archive-management/transfer-applications`
- **请求体**：`TransferApplicationCreateCommand`
- **响应**：`TransferApplicationResponse`

#### 4.1.2 更新移交申请

- **URL**：`PUT /api/archive-management/transfer-applications/{applicationId}`
- **请求体**：`TransferApplicationCreateCommand`
- **响应**：`TransferApplicationResponse`

#### 4.1.3 查询待归档数据

- **URL**：`POST /api/archive-management/archives/query`
- **请求体**：`ArchiveQueryCommand`
- **响应**：`ArchiveQueryResponse`

#### 4.1.4 上传附件

- **URL**：`POST /api/archive-management/transfer-applications/{applicationId}/details/{detailId}/attachments`
- **请求体**：`MultipartFile`
- **响应**：`TransferApplicationDetailAttachmentResponse`

#### 4.1.5 下载附件

- **URL**：`GET /api/archive-management/transfer-applications/{applicationId}/details/{detailId}/attachments/{attachmentId}/download`
- **响应**：`Resource`

### 4.2 接口处理逻辑

#### 4.2.1 创建/更新移交申请

**处理步骤**：
1. 验证请求参数
2. 检查申请单号是否唯一
3. 保存申请头信息到`fdc_application_t`表
4. 删除原有的申请行、附件和扩展字段
5. 保存新的申请行到`fdc_application_detail_t`表
6. 保存扩展字段到`fdc_application_ext_t`表
7. 如果是提交操作，启动审批流程

**查询条件**：
- 申请单号
- 租户ID

**涉及表**：
- `fdc_application_t`：申请头信息
- `fdc_application_detail_t`：申请行信息
- `fdc_application_ext_t`：扩展字段信息
- `fdc_archive_attachment_t`：附件信息

**查询字段**：
- 申请头表：所有字段
- 申请行表：所有字段
- 扩展字段表：attr1-attr300
- 附件表：文件名、存储路径、文件大小等

#### 4.2.2 启动审批流程

**处理步骤**：
1. 检查是否已存在运行中的流程
2. 准备流程变量（审批人、申请ID、申请人等）
3. 启动流程
4. 保存流程实例到`fdc_workflow_instance_t`表

**查询条件**：
- 业务类型：TRANSFER_APPLICATION
- 业务键：TRN-APP-{applicationId}
- 状态：RUNNING

**涉及表**：
- `fdc_workflow_instance_t`：流程实例信息

#### 4.2.3 扩展字段处理

**查询逻辑**：
1. 根据业务模块查询扩展字段配置
2. 从`fdc_application_ext_t`表中查询扩展字段值
3. 将扩展字段值映射到对应的字段编码

**存储结构**：
- `attr1-attr100`：存储字符串类型扩展字段
- `attr101-attr200`：存储数值类型扩展字段
- `attr201-attr300`：存储日期类型扩展字段

**数据类型转换**：
- 字符串类型：直接存储
- 数值类型：转换为BigDecimal
- 日期类型：转换为LocalDate

## 5. 业务规则

### 5.1 数据验证规则

1. **必填字段验证**：所有必填字段必须填写
2. **数据格式验证**：日期、数字等字段必须符合格式要求
3. **唯一性验证**：申请单号必须唯一
4. **权限验证**：只有申请人和管理员可以编辑申请

### 5.2 状态流转规则

| 状态 | 可执行操作 |
|------|-----------|
| DRAFT | 编辑、提交 |
| SUBMITTED | 查看 |
| RUNNING | 查看 |
| APPROVED | 查看 |
| REJECTED | 编辑、重新提交 |

### 5.3 权限规则

1. **查看权限**：所有档案管理用户可查看移交申请
2. **编辑权限**：仅申请人和管理员可编辑草稿或驳回状态的申请
3. **提交权限**：仅申请人可提交申请
4. **审批权限**：仅接收人可审批申请

## 6. 实现要点

### 6.1 前端实现

- 使用Vue 3 + TypeScript构建
- 使用Element Plus组件库
- 实现响应式布局，适配不同屏幕尺寸
- 动态加载扩展字段
- 实现表单验证
- 处理文件上传和下载

### 6.2 后端实现

- 使用Spring Boot + MyBatis Plus
- 实现RESTful API
- 采用分层架构（控制器、服务层、数据访问层）
- 实现事务管理和异常处理
- 集成工作流引擎

### 6.3 数据库实现

- 使用关系型数据库（如MySQL）
- 合理设计表结构和索引
- 实现数据隔离和安全访问

## 7. 总结

移交申请提交功能是档案管理系统中的重要组成部分，通过本设计方案，实现了一个功能完整、性能良好、用户体验优秀的提交系统。该功能支持多种移交方式，动态加载扩展字段，并与工作流系统集成，为档案管理工作提供了有力的支持。