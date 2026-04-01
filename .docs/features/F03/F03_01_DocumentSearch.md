# F03_01 文档查询（列表页）

> 参考：旧规格 `1.1 规格设计-文档查询` + HTML `reference_html/pages/document_search.html`。

## 1. 业务场景与用户旅程

- **S-01 查询并浏览文档列表**  
  - **前置条件**：用户具备文档查询权限；至少选择“文档类型”。  
  - **操作步骤**：填写筛选条件 → 点击“查询” → 查看列表。  
  - **系统响应**：返回匹配文档列表，支持分页、刷新、展开。  
  - **异常与提示**：未选择文档类型则提示“请选择文档类型”。  
  - **产出**：无（只读查询）。
- **S-02 从列表进入文档详情**  
  - **前置条件**：列表存在记录。  
  - **操作步骤**：点击“文档业务编码”链接。  
  - **系统响应**：跳转文档详情页并加载详情数据。
- **S-03 批量导出**  
  - **操作步骤**：勾选0~N条记录 → 点击“批量导出”。  
  - **系统响应**：提交导出任务成功提示；引导前往“我的导出”。
- **S-04 批量导入查询（入口在本页）**  
  - **说明**：入口存在于文档查询页，但属于“应归档数据/导入查询”功能线，详见 `F03_90_ArchiveReceivableData_Reference.md`。

## 2. 页面与交互（UI/UX）

### 2.1 页面分区

- **Header**：返回首页 + 搜索框（原型包含“搜索文档...”）
- **筛选区**：默认筛选 + 更多筛选（展开/收起）
- **按钮区**：重置、查询、批量导入查询、批量导出、展开、刷新、设置
- **列表区**：数据表格（含勾选、链接列、可配置列）

### Header 分区字段（原型）

| 序号 | 字段名称 | 字段名-英文 | 类型 | 字段逻辑说明 | 备注 |
|---:|---|---|---|---|---|
| 1 | 返回首页 | Home | 链接 | 跳转到系统首页 | 原型元素 |
| 2 | 搜索文档 | Search Document | 文本输入框 | 原型包含搜索输入（placeholder：`搜索文档...`） | 原型元素 |
| 3 | 用户信息 | User | 文本显示 | 展示当前登录用户信息（原型示例：管理员） | 原型元素 |

### 2.2 筛选字段（来自旧规格 + 原型校对）

> 字段英文名、类型、口径以 `/.docs/01_DataModel.md` 为准；以下主要沉淀“交互与校验”。


| 序号 | 字段名称 | 字段名-英文 | 类型 | 字段逻辑说明 | 备注 |
|---:|---|---|---|---|---|
| 1 | 文档类型 | Document Type | 下拉选项，单选 | 从表 `fdc_document_type_t` 获取 `name` 展示下拉值，根据权限选择性展示；必填；未选择时禁止展开更多筛选/批量导入查询等并提示“请选择文档类型” | 默认筛选栏 |
| 2 | 归档主体 | Archived Entity | 弹窗组件 | 归档主体弹窗组件，根据权限选择性展示 | 默认筛选栏 |
| 3 | 业务模块 | Business Module | 下拉选项，多选框 | 从表 `fdc_business_module_t` 获取 `name` 及 `description` 展示下拉值，根据权限选择性展示 | 默认筛选栏 |
| 4 | 开始档期 | Start Period | 年月范围 | 时间范围限定在一年内，超出范围弹出提示“选择范围不能超过一年” | 默认筛选栏 |
| 5 | 载体类型 | Carrier Type | 下拉选项，多选框 | 来源 `LOOKUP`：`FDC_CARRIER_TYPE` | 默认筛选栏 |
| 6 | 文档状态 | Doc Status | 下拉选项，多选框 | 来源 `LOOKUP`：`FDC_DOC_STATUS` | 默认筛选栏 |
| 7 | 文档生成时间 | Doc Generation Time | 年月日·起止范围 | 时间范围限定在一年内，超出范围弹出提示“选择范围不能超过一年” | 默认筛选栏 |
| 8 | 文档业务编码 | Doc Business Code | 文本域 | 单条输入支持模糊搜索，多条输入仅限精确查询，数量限定在100条内，超出范围弹出提示“输入条目不能超过100条” | 默认筛选栏 |
| 9 | 文档名称 | Doc Name | 文本 | 支持模糊搜索 | 默认筛选栏 |
| 10 | 文档组织 | Doc Organization | 下拉选项，多选框 |  | 下拉更多筛选栏展示 |
| 11 | 国家 | Country | 下拉选项，多选框 |  | 下拉更多筛选栏展示 |
| 12 | 代表处 | Rep Office | 下拉选项，多选框 |  | 下拉更多筛选栏展示 |
| 13 | 地区部 | Region | 下拉选项，多选框 |  | 下拉更多筛选栏展示 |
| 14 | 文档状态 | Doc Status | 下拉选项，多选框 | 根据文档id在 `fdc_doc_t` 表中获取 `doc_status` 字段；来源 `LOOKUP`：`FDC_DOC_STATUS`，根据权限选择性展示 | 下拉更多筛选栏展示 |
| 15 | 密级 | Security Level | 下拉选项，多选框 | 来源 `LOOKUP`：`FDC_SECURITY_LEVEL`，根据权限选择性展示 | 下拉更多筛选栏展示 |
| 16 | 描述 | Description | 输入框 | 支持模糊搜索 | 下拉更多筛选栏展示 |
| 17 | 是否可见 | Visibility | 下拉选项，多选框 | 是/否，根据权限选择性展示 | 下拉更多筛选栏展示 |
| 18 | 归档地 | Arch Place | 弹窗组件 |  | 下拉更多筛选栏展示 |
| 19 | 产生地 | Originating Place | 弹窗组件 |  | 下拉更多筛选栏展示 |
| 20 | 归档责任人 | Owner | 人员工号 | 自动联想工号，联想失败弹出提示“请输入正确的工号” | 下拉更多筛选栏展示 |
| 21 | 文档责任部门 | Resp Arch Dept | 弹窗组件 |  | 下拉更多筛选栏展示 |
| 22 | 创建人 | Created By | 人员工号 | 自动联想工号，联想失败弹出提示“请输入正确的工号” | 下拉更多筛选栏展示 |
| 23 | 创建时间 | Creation Time | 年月日范围 | 时间范围限定在一年内，超出范围弹出提示“选择范围不能超过一年” | 下拉更多筛选栏展示 |
| 24 | 系统来源 | Source System | 下拉选项，多选框 | 来源 `LOOKUP`：`FDC_SOURCE_SYS` | 下拉更多筛选栏展示 |
| 25 | 文档归档编码 | （未给英文） | 文本域 |  | 下拉更多筛选栏展示 |
| 26 | 条码模块 | Barcode Module | 弹窗组件 |  | 下拉更多筛选栏展示 |
| 27 | 档案条码 | Archive Barcode | 文本域 |  | 下拉更多筛选栏展示 |
| 28 | 册条码 | Volume Barcode | 文本域 |  | 下拉更多筛选栏展示 |
| 29 | 成册人 | Volume Compiler | 人员工号 |  | 下拉更多筛选栏展示 |
| 30 | 文号 | File No. | 文本域 |  | 下拉更多筛选栏展示 |
| 31 | 起止号 | S/E No. | 文本域 |  | 下拉更多筛选栏展示 |
| 32 | 发票号 | Invoice No. | 文本域 |  | 下拉更多筛选栏展示 |
| 33 | 其他归档号 | Other Arch No | 文本域 | 输入数量限定在100条内，超出范围弹出提示“输入条目不能超过100条” | 下拉更多筛选栏展示 |
| 34 | 业务类型 | Business Type | 下拉 |  | 下拉更多筛选栏展示 |
| 35 | 子公司名称 | Subsidiary Name | 下拉 |  | 下拉更多筛选栏展示 |


### 2.3 列表字段（来自旧规格）


| 序号 | 字段名称 | 字段名-英文 | 类型 | 字段逻辑说明 | 备注 |
|---:|---|---|---|---|---|
| 0 | 勾选 | Select | 复选框 | 用于批量导出选择 0~N 条记录 | 与“批量导出”联动 |
| 1 | 文档类型 | Document Type | 文本 | 根据文档id在 `fdc_doc_t` 表中获取 `business_module_id`，作为外键关联 `fdc_business_module_t` 获取 `document_type_id`，作为外键关联 `fdc_document_type_t` 获取 `document_type_name` | 设置中扩展字段可选 |
| 2 | 文档业务编码 | Doc Business No. | 链接 | 根据文档id在 `fdc_doc_t` 表中获取 `doc_busi_no` 字段；默认展示，点击后调用文档详情api，跳转至文档详情页 | 默认展示 |
| 3 | 公司/主体 | Company/Entity | 文本 | 根据文档id在 `fdc_doc_t` 表中获取 `archived_entity_unit_id`，作为外键关联 `fdc_archived_entity_unit_t` 获取 `archived_entity_id`，作为外键关联 `fdc_archived_entity_t` 获取 `archived_entity_name` | 默认展示 |
| 4 | 业务模块 | Business Module | 文本 | 根据文档id在 `fdc_doc_t` 表中获取 `business_module_id`，作为外键关联 `fdc_business_module_t` 获取 `business_module_name` | 默认展示 |
| 5 | 开始档期 | Start Period | 文本 | 根据文档id在 `fdc_doc_t` 表中获取 `start_period` 字段 | 设置中扩展字段可选 |
| 6 | 结束档期 | End Period | 文本 | 根据文档id在 `fdc_doc_t` 表中获取 `end_period` 字段 | 默认展示 |
| 7 | 归档地 | Arch Place | 文本 | 根据文档id在 `fdc_doc_t` 表中获取 `arch_place_alpha2_code` 字段，作为入参（通过 `-` 分割符数量判断行政区域层级决定具体入参）查询政区域获取对应 `AliasChinese` 和 `AliasEnglish` 出参 | 默认展示 |
| 8 | 产生地 | Originating Place | 文本 | 根据文档id在 `fdc_doc_t` 表中获取 `origin_place_alpha2_code` 字段，作为入参（通过 `-` 分割符数量判断行政区域层级决定具体入参）查询政区域获取对应 `AliasChinese` 和 `AliasEnglish` 出参 | 设置中扩展字段可选 |
| 9 | 文档组织 | Doc Organization | 文本 | 根据文档id在 `fdc_doc_t` 表中获取 `doc_organization_code` 字段，关联 `fdc_document_organization_t` 带出 `doc_organization_name` 字段 | 默认展示 |
| 10 | 文档状态 | Doc Status | 文本 | 根据文档id在 `fdc_doc_t` 表中获取 `doc_status` 字段 | 默认展示 |
| 11 | 文档名称 | Doc Name | 文本 | 根据文档id在 `fdc_doc_t` 表中获取 `doc_name` 字段 | 默认展示 |
| 12 | 文档生成日期 | Doc Generation Date | 文本 | 根据文档id在 `fdc_doc_t` 表中获取 `doc_generation_date` 字段 | 默认展示 |
| 13 | 归档责任人 | Owner | 文本 | 根据文档id在 `fdc_doc_t` 表中获取 `owner_id` 字段，作为外键外联【用户表】的 `user_id`，带出 `user_name` 字段展示 | 默认展示 |
| 14 | 文档责任部门 | Responsible Dept. | 文本 | 根据文档id在 `fdc_doc_t` 表中获取 `dept_code` 字段，作为入参查询获取对应 `organizationName` 字段 | 设置中扩展字段可选 |
| 15 | 载体类型 | Carrier Type | 文本 | 根据文档id在 `fdc_doc_t` 表中获取 `carrier_type` 字段，关联Lookup中的Carrier Type值 | 设置中扩展字段可选 |
| 16 | 是否可见 | Visibility | 文本 | 根据文档id在 `fdc_doc_t` 表中匹配归档流向，获取 `visible_flag` | 设置中扩展字段可选 |
| 17 | 系统来源 | Source System | 文本 | 根据文档id在 `fdc_doc_t` 表中获取 `source_system` 字段 | 设置中扩展字段可选 |
| 18 | 密级 | Security Level | 文本 | 根据文档id在 `fdc_doc_t` 表中获取 `business_module_id`，作为外键关联 `fdc_business_module_t` 获取 | 设置中扩展字段可选 |
| 19 | 描述 | Description | 文本 | 根据文档id在 `fdc_doc_t` 表中获取 `description` 字段 | 设置中扩展字段可选 |
| 20 | 文档归档编码 | （未给英文） | 文本 | 根据文档id在 `fdc_doc_t` 表中获取 | 设置中扩展字段可选 |
| 21 | 创建时间 | Creation Time | 文本 | 根据文档id在 `fdc_doc_t` 表中获取 `creation_date` 字段 | 设置中扩展字段可选 |
| 22 | 创建人 | Created By | 文本 | 根据文档id在 `fdc_doc_t` 表中获取 `created_by` 字段，作为外键外联【用户表】的 `user_id`，带出 `user_name` 字段展示 | 设置中扩展字段可选 |
| 23 | 地区部 | Region | 文本 | 根据文档id在 `fdc_doc_t` 表中获取 `archived_entity_unit_id`，作为外键关联 `fdc_archived_entity_unit_t` 获取 `archived_entity_id`，作为外键关联 `fdc_archived_entity_t` 获取 `region_code`，地区部名称根据 `region_code` 调用idata服务获取对应的名称 | 设置中扩展字段可选 |
| 24 | 代表处 | Rep Office | 文本 | 根据文档id在 `fdc_doc_t` 表中获取 `archived_entity_unit_id`，作为外键关联 `fdc_archived_entity_unit_t` 获取 `archived_entity_id`，作为外键关联 `fdc_archived_entity_t` 获取 `rep_office_code`，地区部名称根据 `rep_office_code` 调用idata服务获取对应的名称 | 设置中扩展字段可选 |
| 25 | 国家/地区 | Country | 文本 | 根据文档id在 `fdc_doc_t` 表中获取 `archived_entity_unit_id`，作为外键关联 `fdc_archived_entity_unit_t` 获取 `archived_entity_id`，作为外键关联 `fdc_archived_entity_t` 获取 `country_code`，国家名称根据 `country_code` 调用idata服务获取对应的名称 | 设置中扩展字段可选 |


### 2.4 按钮区按钮（旧规格）

| 编号 | 按钮名称 | 按钮名称-英文 | 显示位置 | 按钮逻辑说明（含调用UI API） | 绑定权限项名称 | 新增权限项说明 |
|---:|---|---|---|---|---|---|
| 1 | 更多筛选条件 | More | 筛选区-右下 | 点击展示更多筛选条件，调用查询更多筛选条件API，未选择文档类型时弹出提示“请选择文档类型” |  |  |
| 2 | 收起 | Hide | 筛选区-右下 | 点击收起更多筛选条件 |  |  |
| 3 | 重置 | Reset | 右下1 | 清空查询条件 |  |  |
| 4 | 查询 | Serch | 右下2 | 调用查询文档API，响应成功后展示文档列表信息。是否添加必须输入一个条件的限制？ |  |  |
| 5 | 批量导入查询 | Batch Import Query | 按钮区-左1 | 点击后弹出导入文件弹窗，未选择文档类型时弹出提示“请选择文档类型” |  |  |
| 6 | 批量导出 | Batch Export | 按钮区-左2 | 点击批量导出，如果没有勾选列表数据，默认导出查询列表数据；如果勾选了列表数据，导出勾选的数据；点击后弹出导出跳转提示弹窗 |  |  |
| 7 | 展开 | / | 按钮区-右1 | 点击展开，全屏展示列表区 |  |  |
| 8 | 刷新 | / | 按钮区-右2 | 点击刷新，根据缓存的筛选条件重新查询展示结果 |  |  |
| 9 | 设置 | / | 按钮区-右3 | 点击设置，弹出Grid 表格-个性化SAAS组件settings弹窗 |  |  |

## 3. 规则与策略（Rules）

- **R-001 文档类型必选**
  - **输入**：用户点击“更多筛选/批量导入查询/查询/批量导出”等动作
  - **处理**：若文档类型为空，则阻止动作
  - **输出**：toast 提示“请选择文档类型”
- **R-002 时间范围不超过一年**
  - **输入**：任意日期/档期范围
  - **处理**：若跨度 > 1 年
  - **输出**：提示“选择范围不能超过一年”
- **R-003 多条输入上限**
  - **输入**：多条文本（如其他归档号/业务编码）
  - **处理**：条目数 > 100
  - **输出**：提示“输入条目不能超过100条”

## 4. 接口规范（API Specs）

> 遵循 `/.docs/05_API_Conventions.md`（小写、中横线、复数资源、扁平路径、`/page` / `/search-page` / `/export` 等）。`{id}` 为数值型主键。

### 4.1 接口总览

| 资源路径 | 方法 | 用途 | 备注 |
|---|---|---|---|
| `FDC_URL/documents/search-page` | POST | 文档列表（复杂筛选 + 分页） | 请求体：`DocumentPaginationQuery`，筛选条件放在 `filter` 属性中 |
| `FDC_URL/documents/page` | GET | 文档列表（简单筛选 + 分页，可选） | Query：`pageNumber`、`pageSize`、`filter.*` |
| `FDC_URL/documents/{id}` | GET | 文档详情 | 列表点击业务编码进入详情 |
| `FDC_URL/documents/export` | POST | 导出文档列表（异步任务） | 未勾选行=按当前筛选导出；勾选=请求体带 `documentId` 列表（及筛选快照）；建议 `Idempotency-Key` |
| `FDC_URL/document-query-imports/template` | GET | 批量导入查询模板 | **非 F03 主范围**，见 `F03_90_ArchiveReceivableData_Reference.md` |
| `FDC_URL/document-query-imports/import` | POST | 批量导入查询提交 | **非 F03 主范围**，见 `F03_90` |


## 5. 验收标准（AC）

- **AC-01**：未选择“文档类型”点击“更多筛选条件”，系统提示“请选择文档类型”，不展开更多筛选。  
- **AC-02**：选择文档类型后点击“查询”，系统展示符合条件的文档列表并可分页。  
- **AC-03**：点击列表中“文档业务编码”，可进入对应文档详情页且信息加载成功。  
- **AC-04**：批量导出：未勾选导出当前查询结果；勾选则仅导出勾选记录，并提示可跳转“我的导出”。

## 5. 功能详细规格（UI/UX §5.2 页面分区模块）

> 本节用于承载 `F03/raw/old_spec.md` 中“分区页面字段信息”，避免字段沉淀丢失。原型参考：`reference_html/pages/document_search.html`。

### 5.2 页面/交互/原型说明（UI/UX）

#### 5.2.1 页面一览（本文件范围）
| 页面编号 | 页面名称 | 路由/入口 | 页面类型 | 简述 |
|---|---|---|---|---|
| P-01 | 文档查询列表页面 | 文档查询入口（`reference_html/pages/document_search.html`） | 列表页（筛选区 + 列表区 + 分页/操作） | 支持多条件查询、列表浏览、从列表发起批量导出/批量导入查询 |

#### 5.2.2 分页面规格
##### P-01 《文档查询列表页面》

###### （1）页面类型与整体布局
- **页面类型**：列表页
- **布局骨架**：
  - **L1** Header：返回首页 + 搜索框（原型文案：搜索文档...）
  - **L2** 标题区：文档查询
  - **L3** 筛选区：默认筛选（必填项校验） + 更多筛选（折叠/展开）
  - **L4** 工具栏：重置、查询、批量导入查询、批量导出、展开、刷新、设置
  - **L5** 主内容区：数据表格（支持勾选、链接列、可配置列）+ 分页
- **原型与设计**：`reference_html/pages/document_search.html`

###### （2）分区 → 模块拆解

**〔L3 筛选区〕— 模块 M1：默认筛选（精简版）**

| 字段（元素） | 字段名-英文 | 控件 | 必填 | 取值/来源 | 交互要点 |
|---|---|---|---:|---|---|
| 文档类型 | Document Type | 下拉单选 | 是 | `fdc_document_type_t`（权限过滤展示 name） | 未选时阻断“更多筛选/批量导入查询/查询/批量导出”等动作 |
| 归档主体 | Archived Entity | 弹窗选择 | 否 | 按权限过滤（old_spec 对应） | 只读输入框 + 打开选择弹窗 |
| 业务模块 | Business Module | 下拉多选 | 否 | `fdc_business_module_t`（展示 name/description） | 选择后用于后端过滤与权限范围联动 |
| 开始档期 | Start Period | 年月范围 | 否 | 入参校验 | 范围不超过 1 年（超出提示：选择范围不能超过一年） |
| 载体类型 | Carrier Type | 下拉多选 | 否 | LOOKUP：`FDC_CARRIER_TYPE` | 根据原型：默认筛选栏展示 |
| 文档状态 | Doc Status | 下拉多选 | 否 | LOOKUP：`FDC_DOC_STATUS` | 根据权限选择性展示 |
| 文档生成时间 | Doc Generation Time | 年月日·起止范围 | 否 | 入参校验 | 范围不超过 1 年（超出提示：选择范围不能超过一年） |
| 文档业务编码 | Doc Business Code | 文本域 | 否 | 文本校验 | 单条模糊/多条精确；多条数量 ≤ 100（超出提示：输入条目不能超过100条） |
| 文档名称 | Doc Name | 文本输入 | 否 | 文本校验 | 支持模糊搜索 |

**〔L3 筛选区〕— 模块 M1：默认筛选（全量版）**

| 元素 | 类型/控件 | 必填 | 默认值/提示语 | 校验/取值来源 | 交互与联动 | 备注 |
|---|---|---:|---|---|---|---|
| 文档类型 | 下拉选项（单选） | 是 | 请选择 | `fdc_document_type_t` name；权限过滤 | 未选择时禁止更多筛选/批量导入查询等并提示“请选择文档类型” | old_spec：默认筛选栏 |
| 归档主体 | 弹窗组件 | 否 | 请选择 | 组件按权限过滤 | 只读输入框 + 打开弹窗 | |
| 业务模块 | 下拉选项（多选） | 否 | 请选择 | `fdc_business_module_t`（name/description） | 与后端 filter 组合查询 | |
| 开始档期 | 年月范围 | 否 |（空） | 时间范围限定一年内 | UI 校验：超出提示“选择范围不能超过一年” | |
| 载体类型 | 下拉选项（多选） | 否 | 请选择 | LOOKUP：`FDC_CARRIER_TYPE` | 权限/lookup 下拉展示 | |
| 文档状态 | 下拉选项（多选） | 否 | 请选择 | LOOKUP：`FDC_DOC_STATUS` | 权限选择性展示 | |
| 文档生成时间 | 年月日·起止范围 | 否 |（空） | 时间范围限定一年内 | 超出提示“选择范围不能超过一年” | |
| 文档业务编码 | 文本域 | 否 | 请输入 | 单条支持模糊；多条仅精确；数量 ≤ 100 | 联动“查询”触发列表刷新 | |
| 文档名称 | 文本 | 否 | 请输入 | 支持模糊搜索 | 联动“查询”触发列表刷新 | |

**〔L3 筛选区〕— 模块 M2：更多筛选（精简版）**

| 字段（元素） | 字段名-英文 | 控件 | 必填 | 取值/来源 | 交互要点 |
|---|---|---|---:|---|---|
| 文档组织 | Doc Organization | 下拉多选 | 否 | 组件按后端可用数据/配置展示 | 更多筛选栏展示 |
| 国家 | Country | 下拉多选 | 否 | 地区服务/可选映射（old_spec） | 更多筛选栏展示 |
| 代表处 | Rep Office | 下拉多选 | 否 | 地区服务/可选映射（old_spec） | 更多筛选栏展示 |
| 地区部 | Region | 下拉多选 | 否 | 地区服务/可选映射（old_spec） | 更多筛选栏展示 |
| 文档状态（更多） | Doc Status | 下拉多选 | 否 | LOOKUP：`FDC_DOC_STATUS`（按权限展示） | 与文档id字段读取关联（old_spec 表述） |
| 密级 | Security Level | 下拉多选 | 否 | LOOKUP：`FDC_SECURITY_LEVEL`（按权限展示） | 更多筛选栏展示 |
| 描述 | Description | 输入框 | 否 | 模糊检索 | 更多筛选栏展示 |
| 是否可见 | Visibility | 下拉多选 | 否 | 是/否（按权限选择性展示） | 更多筛选栏展示 |
| 归档地 | Arch Place | 弹窗组件 | 否 | 行政区域服务映射（old_spec） | 更多筛选栏展示 |
| 产生地 | Originating Place | 弹窗组件 | 否 | 行政区域服务映射（old_spec） | 更多筛选栏展示 |
| 归档责任人 | Owner | 人员工号 | 否 | 用户/工号联想 | 联想失败提示“请输入正确的工号” |
| 文档责任部门 | Resp Arch Dept | 弹窗组件 | 否 | 部门选择弹窗 | 更多筛选栏展示 |
| 创建人 | Created By | 人员工号 | 否 | 用户/工号联想 | 联想失败提示“请输入正确的工号” |
| 创建时间 | Creation Time | 年月日范围 | 否 | 时间范围校验 | 范围不超过 1 年（超出提示） |
| 系统来源 | Source System | 下拉多选 | 否 | LOOKUP：`FDC_SOURCE_SYS` | 更多筛选栏展示 |
| 文档归档编码 | （未命名/空英文） | 文本域 | 否 | 文本过滤 | 更多筛选栏展示 |
| 条码模块 | Barcode Module | 弹窗组件 | 否 | 条码模块选择（old_spec） | 更多筛选栏展示 |
| 档案条码 | Archive Barcode | 文本域 | 否 | 文本过滤 | 更多筛选栏展示 |
| 册条码 | Volume Barcode | 文本域 | 否 | 文本过滤 | 更多筛选栏展示 |
| 成册人 | Volume Compiler | 人员工号 | 否 | 工号过滤 | 更多筛选栏展示 |
| 文号 | File No. | 文本域 | 否 | 文本过滤 | 更多筛选栏展示 |
| 起止号 | S/E No. | 文本域 | 否 | 文本过滤 | 更多筛选栏展示 |
| 发票号 | Invoice No. | 文本域 | 否 | 文本过滤 | 更多筛选栏展示 |
| 其他归档号 | Other Arch No | 文本域 | 否 | 多条输入 ≤ 100 | 超出提示“输入条目不能超过100条” |

**〔L3 筛选区〕— 模块 M2：更多筛选（全量版）**

| 元素 | 类型/控件 | 必填 | 默认值/提示语 | 校验/取值来源 | 交互与联动 | 备注 |
|---|---|---:|---|---|---|---|
| 文档组织 | 下拉选项（多选） | 否 | 请选择 | 组件按后端可用数据/配置展示 | 更多筛选栏展示 | |
| 国家 | 下拉选项（多选） | 否 | 请选择 | （old_spec：地区相关） | 更多筛选栏展示 | |
| 代表处 | 下拉选项（多选） | 否 | 请选择 | （old_spec：地区相关） | 更多筛选栏展示 | |
| 地区部 | 下拉选项（多选） | 否 | 请选择 | （old_spec：地区相关） | 更多筛选栏展示 | |
| 文档状态（更多） | 下拉选项（多选） | 否 | 请选择 | 基于 docId 读取 `fdc_doc_t.doc_status`；LOOKUP：`FDC_DOC_STATUS`；权限选择性展示 | 更多筛选栏展示 | old_spec：来源按权限选择性展示 |
| 密级 | 下拉选项（多选） | 否 | 请选择 | LOOKUP：`FDC_SECURITY_LEVEL` | 根据权限选择性展示 | |
| 描述 | 输入框 | 否 | 请输入 | 支持模糊搜索 | 与“查询”组合 | |
| 是否可见 | 下拉选项（多选） | 否 | 请选择 | 是/否（按权限选择性展示） | 与“查询”组合 | |
| 归档地 | 弹窗组件 | 否 | 请选择 | 行政区划服务映射（按入参层级选择） | 更多筛选栏展示 | |
| 产生地 | 弹窗组件 | 否 | 请选择 | 行政区划服务映射（按入参层级选择） | 更多筛选栏展示 | |
| 归档责任人 | 人员工号 | 否 | 请输入 | 自动联想工号；失败提示“请输入正确的工号” | 与“查询”组合 | |
| 文档责任部门 | 弹窗组件 | 否 | 请选择 | 部门选择（old_spec：Resp Arch Dept） | 更多筛选栏展示 | |
| 创建人 | 人员工号 | 否 | 请输入 | 自动联想工号；失败提示“请输入正确的工号” | 与“查询”组合 | |
| 创建时间 | 年月日范围 | 否 |（空） | 时间范围限定一年内 | 超出提示“选择范围不能超过一年” | |
| 系统来源 | 下拉选项（多选） | 否 | 请选择 | LOOKUP：`FDC_SOURCE_SYS` | 更多筛选栏展示 | |
| 文档归档编码 | 文本域 | 否 | 请输入 | 文本过滤 | 更多筛选栏展示 | |
| 条码模块 | 弹窗组件 | 否 | 请选择 | 条码模块选择 | 更多筛选栏展示 | |
| 档案条码 | 文本域 | 否 | 请输入 | 文本过滤 | 更多筛选栏展示 | |
| 册条码 | 文本域 | 否 | 请输入 | 文本过滤 | 更多筛选栏展示 | |
| 成册人 | 人员工号 | 否 | 请输入 | 工号过滤 | 更多筛选栏展示 | |
| 文号 | 文本域 | 否 | 请输入 | 文本过滤 | 更多筛选栏展示 | |
| 起止号 | 文本域 | 否 | 请输入 | 文本过滤 | 更多筛选栏展示 | |
| 发票号 | 文本域 | 否 | 请输入 | 文本过滤 | 更多筛选栏展示 | |
| 其他归档号 | 文本域 | 否 | 请输入 | 多条输入 ≤ 100；超出提示“输入条目不能超过100条” | 更多筛选栏展示 | |

**〔L4 工具栏〕— 模块 M3：按钮组（全量版）**

| 按钮名称 | 样式 | 可见条件 | 禁用条件 | 点击后行为 | 权限点 `perm_code` | 对接 API（UI/页面级） |
|---|---|---|---|---|---|---|
| 更多筛选条件 | 文本按钮 | 文档类型已选择 | 文档类型为空 | 展示更多筛选并调用“查询更多筛选条件 API”；未选则提示“请选择文档类型” |（见 `/.docs/03_Security.md`，待补充具体 perm_code） | 查询更多筛选条件（old_spec：查询更多筛选条件API） |
| 收起 | 文本按钮 | 更多筛选已展开 | - | 收起更多筛选 |（待补充） | - |
| 重置 | 文本按钮 | - | - | 清空查询条件 |（待补充） | - |
| 查询 | 主按钮 | - | 文档类型为空 | 调用查询文档 API，响应成功后展示列表；（old_spec：是否要求至少一个条件待定；本期以“文档类型必选”为最小条件） |（待补充） | `documents/search-page`（列表服务） |
| 批量导入查询 | 主按钮 | 文档类型已选择 | 文档类型为空 | 弹出“批量导入查询”弹窗；未选则提示“请选择文档类型” |（待补充） | 打开批量导入查询弹窗（模板/上传） |
| 批量导出 | 主按钮 | - | 文档类型为空（与“查询/导出动作”最小条件一致） | 若无勾选：导出当前查询结果；若有勾选：导出勾选数据；提交后弹出“导出跳转提示弹窗” | `fdc:document:export`（示例，待与 `03_Security.md` 实际登记对齐） | `documents/export`（批量导出服务） |
| 展开 | 图标/按钮 | - | - | 全屏展示列表区 |（待补充） | - |
| 刷新 | 图标/按钮 | - | - | 按缓存筛选条件重新查询展示结果 |（待补充） | 复用查询 API |
| 设置 | 图标/按钮 | - | - | 打开 Grid 个性化 settings 弹窗 |（待补充） | - |

**〔L5 主内容区〕— 模块 M4：文档列表表格（全量版列字段）**

| 列名 | 字段名-英文 | 数据来源/字段逻辑（old_spec） | 展示格式 | 排序 | 交互（链接/勾选） | 备注 |
|---|---|---|---|---|---|---|
| 文档类型 | Document Type | 根据 docId 在 `fdc_doc_t` 取 `business_module_id` → 关联 `fdc_business_module_t` → `document_type_id` → `fdc_document_type_t.document_type_name` | 文本 | 可配置 | - | 设置中扩展字段可选 |
| 文档业务编码 | Doc Business No. | `fdc_doc_t.doc_busi_no` | 链接 | - | 点击跳转文档详情并调用查询文档详情 API | 默认展示 |
| 公司/主体 | Company/Entity | `fdc_doc_t.archived_entity_unit_id` → `fdc_archived_entity_unit_t` → `fdc_archived_entity_t.archived_entity_name` | 文本 | - | - | 默认展示 |
| 业务模块 | Business Module | `fdc_doc_t.business_module_id` → `fdc_business_module_t.business_module_name` | 文本 | - | - | 默认展示 |
| 开始档期 | Start Period | `fdc_doc_t.start_period` | 文本 | - | - | 设置中扩展字段可选 |
| 结束档期 | End Period | `fdc_doc_t.end_period` | 文本 | - | - | 默认展示 |
| 归档地 | Arch Place | `fdc_doc_t.arch_place_alpha2_code`（按行政层级入参查询 alias 中文/英文） | 文本 | - | - | 默认展示 |
| 产生地 | Originating Place | `fdc_doc_t.origin_place_alpha2_code`（按行政层级入参查询 alias 中文/英文） | 文本 | - | - | 设置中扩展字段可选 |
| 文档组织 | Doc Organization | `fdc_doc_t.doc_organization_code` → `fdc_document_organization_t` 带出 `doc_organization_name` | 文本 | - | - | 默认展示 |
| 文档状态 | Doc Status | `fdc_doc_t.doc_status` | 文本 | - | - | 默认展示 |
| 文档名称 | Doc Name | `fdc_doc_t.doc_name` | 文本 | - | - | 默认展示 |
| 文档生成日期 | Doc Generation Date | `fdc_doc_t.doc_generation_date` | 文本 | - | - | 默认展示 |
| 归档责任人 | Owner | `fdc_doc_t.owner_id` → 用户表 `user_id` → 带出 `user_name` | 文本 | - | - | 默认展示 |
| 文档责任部门 | Responsible Dept. | `fdc_doc_t.dept_code` → 入参查询获取对应 organizationName | 文本 | - | - | 设置中扩展字段可选 |
| 载体类型 | Carrier Type | `fdc_doc_t.carrier_type` → Lookup Carrier Type 值 | 文本 | - | - | 设置中扩展字段可选 |
| 是否可见 | Visibility | `fdc_doc_t` 匹配归档流向取 `visible_flag` | 文本 | - | - | 设置中扩展字段可选 |
| 系统来源 | Source System | `fdc_doc_t.source_system` | 文本 | - | - | 设置中扩展字段可选 |
| 密级 | Security Level | `fdc_doc_t.business_module_id` → 关联 `fdc_business_module_t` | 文本 | - | - | 设置中扩展字段可选 |
| 描述 | Description | `fdc_doc_t.description` | 文本 | - | - | 设置中扩展字段可选 |
| 文档归档编码 | （old_spec：字段未给英文） | `fdc_doc_t`（old_spec 表述为“根据文档id在 fdc_doc_t 表中获取”） | 文本 | - | - | 设置中扩展字段可选 |
| 创建时间 | Creation Time | `fdc_doc_t.creation_date` | 文本 | - | - | 设置中扩展字段可选 |
| 创建人 | Created By | `fdc_doc_t.created_by` → 用户表带出 `user_name` | 文本 | - | - | 设置中扩展字段可选 |
| 地区部 | Region | 由 `archived_entity_unit_id` → archived_entity_unit_t → archived_entity_t 取 region_code | 文本 | - | - | 设置中扩展字段可选 |
| 代表处 | Rep Office | 由 `archived_entity_unit_id` → 取 rep_office_code → 调用 iData 获取名称 | 文本 | - | - | 设置中扩展字段可选 |
| 国家/地区 | Country | 由 `archived_entity_unit_id` → 取 country_code → 调用 iData 获取名称 | 文本 | - | - | 设置中扩展字段可选 |

**〔L4/L5〕— 模块 M5：批量导入查询弹窗（字段 + 按钮，精简/全量一致）**

| 弹窗元素 | 字段名-英文 | 类型/控件 | 默认值/提示语 | 交互要点 | 备注 |
|---|---|---|---|---|---|
| 下载导入模板说明 | Import Template（文案） | 提示 + 按钮 | 弹窗中提示“下载模板后填写并上传” | 点击下载模板按钮：根据选择的文档类型下载不同文档类型配置的模板 | old_spec：Batch Import Query |
| 文件选择/上传 | （select File） | 拖拽/选择文件组件 | Excel (.xlsx, .xls) | 支持点击或拖拽本地上传 |  |

| 弹窗按钮 | 样式 | 可见条件 | 禁用条件 | 点击后行为 |
|---|---|---|---|---|
| 确定 | Confirm | - | - | 调用批量导入查询服务（old_spec：确认导入） |
| 取消 | Cancel | - | - | 关闭弹窗 |


