# F03_00 文档查询总览

> 本文档用于沉淀 F03 共性约束；各页面细节见 `F03_01`~`F03_05`。

## 1. 背景与范围

### 1.1 功能范围（In scope）

- 文档查询列表：多条件筛选、结果列表、跳转文档详情
- 文档详情：基本信息、归档信息、扩展信息（可折叠）、附件列表、操作日志
- 附件预览：获取 `EDM_ID` 并打开外部系统预览页面、下载原文（按权限）
- 批量导出：从查询结果或勾选结果发起导出任务、提示跳转“我的导出”
- 我的导出：导出任务列表、下载/删除

### 1.2 非范围（Out of scope）

- 应归档数据相关“数据维护/导入查询”（HTML：`data_maintenance.html`、`import_query.html`）不计入 F03 范围，详见 `F03_90_ArchiveReceivableData_Reference.md`。

## 2. 页面清单（HTML 原型对应）


| 页面   | HTML 原型                                        | 说明                                |
| ---- | ---------------------------------------------- | --------------------------------- |
| 文档查询 | `reference_html/pages/document_search.html`    | 筛选区 + 列表区；含“批量导出”“批量导入查询”入口       |
| 文档详情 | `reference_html/pages/document_detail.html`    | 多模块折叠信息 + 附件 + 操作日志               |
| 文档编辑 | `reference_html/pages/document_edit.html`      | 原型存在，但是否属于 F03 需确认（建议纳入数据维护/管理功能） |
| 文档创建 | `reference_html/pages/document_create.html`    | 原型存在，但是否属于 F03 需确认（建议纳入数据维护/管理功能） |
| 附件预览（外部系统） | `external_system（无内部预览页）` | 点击详情附件列表“预览”后获取 `EDM_ID` 并打开外部系统预览页面 |
| 我的导出 | `reference_html/pages/my_exports.html`         | 导出任务列表、下载、删除                      |


## 3. 角色与权限（来自旧规格）

> 详细权限点需与 `/.docs/03_Security.md` 的 RBAC 对齐。本节先按旧规格沉淀“页面能力矩阵”。

旧规格角色示例（节选）：财经文档数据维护员、文档账管员、文档管理员、系统管理员、IT support、报表分析员等。  
权限能力项（节选）：文档查询列表、文档列表导出、文档批量查询、附件预览/下载/批量下载、导入查询结果列表/重新查询/结果导出等。

## 4. 共性规则（跨页面一致）

- **必选条件**：文档查询页“文档类型”为必选；未选择时触发提示“请选择文档类型”（HTML toast + 旧规格一致）。
- **时间范围**：涉及日期/档期范围输入时，限定范围不超过 1 年；超出提示“选择范围不能超过一年”。
- **批量输入上限**：如“文档业务编码/其他归档号”等支持多条输入时，上限 100 条；超出提示“输入条目不能超过100条”。
- **查询结果导出**：
  - 未勾选行：默认导出当前查询结果（旧规格）
  - 勾选行：导出勾选集合
  - 成功后提示并引导跳转“导入导出 > 我的导出”

## 5. 关键数据来源（旧规格提及）

> 字段命名与类型以 `/.docs/01_DataModel.md` 为准；这里仅记录“数据来源关联关系”。

- 文档主表（旧规格引用）：`fdc_doc_t`（示例字段：`doc_busi_no`、`doc_name`、`doc_status`、`start_period`、`end_period`、`carrier_type`、`source_system`…）
- 文档类型：`fdc_document_type_t`
- 业务模块：`fdc_business_module_t`
- 归档主体：`fdc_archived_entity_t`、`fdc_archived_entity_unit_t`
- 文档组织：`fdc_document_organization_t`
- 附件：`fdc_doc_att_t`
- 操作日志：`fdc_doc_op_log_t`、`fdc_doc_log_att_t`
- LOOKUP：`FDC_CARRIER_TYPE`、`FDC_DOC_STATUS`、`FDC_SECURITY_LEVEL`、`FDC_SOURCE_SYS` 等

## 6. F03 相关 API 资源（扁平命名）

> 全局约定见 `/.docs/05_API_Conventions.md`；各端点请求/响应体见 `F03_01`～`F03_05`。

| 资源 | 说明 |
|---|---|
| `documents` | 列表：`POST .../search-page`（主）、可选 `GET .../page`；详情：`GET .../{id}`；导出：`POST .../export` |
| `document-attachments` | 列表：`GET .../page` 或 `POST .../search-page`；预览/下载：`GET .../{id}/preview`、`GET .../{id}/download`；批量：`POST .../export` |
| `document-operation-logs` | 列表：`POST .../search-page`（filter.documentId） |
| `document-operation-log-attachments` | 补充附件：`GET .../{id}/download` |
| `export-tasks` | 我的导出：`GET .../page`；详情/下载/删除：`GET|DELETE .../{id}`，`GET .../{id}/download` |
| `document-query-imports` | 批量导入查询（参考线，见 `F03_90`）：`GET .../template`、`POST .../import` |

