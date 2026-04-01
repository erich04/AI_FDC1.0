# F03 文档查询（索引）

本目录输出 F03「文档查询」功能规格真相来源（SSoT）。  
说明：原型 HTML 中包含一部分“应归档数据/数据维护/导入查询”能力（例如 `data_maintenance.html`、`import_query.html`），这部分**不属于 F03 文档查询主范围**，已单独沉淀为参考文档，避免混淆。

## 1. 文档清单

### 1.1 F03（文档查询主范围）

- `F03/F03_00_Overview.md`：范围、角色权限概览、页面清单、共性规则
- `F03/F03_01_DocumentSearch.md`：文档查询列表页（筛选区、列表区、批量导出/批量导入查询入口）
- `F03/F03_02_DocumentDetail.md`：文档详情页（基本信息/扩展信息/归档信息/附件/操作日志）
- `F03/F03_03_AttachmentPreview.md`：附件预览页（受控水印、缩放、缩略图、下载）
- `F03/F03_04_BatchExport.md`：批量导出交互与任务生成
- `F03/F03_05_MyExports.md`：我的导出（任务列表、下载、删除）

### 1.2 非 F03 范围（仅参考，后续应归档到对应功能）

- `F03/F03_90_ArchiveReceivableData_Reference.md`：应归档数据/数据维护/导入查询（HTML 原型参考），**不计入 F03 验收范围**

## 2. 强制遵循

- 需求规格结构：`/.docs/features/Template.md`
- 数据模型规范：`/.docs/01_DataModel.md`
- **REST API 全局约定**：`/.docs/05_API_Conventions.md`（F03 接口表已按该规范统一为扁平资源与 `/page`、`/search-page`、`/export` 等）
- 安全与权限：`/.docs/03_Security.md`
- 术语口径：`/.docs/04_Glossary.md`

