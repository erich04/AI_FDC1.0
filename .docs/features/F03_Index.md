# F03 文档查询（索引）

本目录输出 F03「文档查询」功能规格真相来源（SSoT）。  
**说明**：工作空间中的 **导入查询**、**导出查询（我的导出）** 为**全系统通用**任务能力，规格见 `F03_06`、`F03_05`；本目录收录它们是为便于与文档查询入口对照，**不**表示这两项能力仅服务于 F03。  
说明：原型 HTML 中「应归档数据管理」能力（例如 `data_maintenance.html`）**不属于** F03 文档查询主流程验收范围，见 `F03_90`。工作空间「导入查询」页面规格以 `F03_06` 为准，与应归档数据参考摘录并存。

## 1. 文档清单

### 1.1 F03（文档查询主范围）

- `F03/F03_00_Overview.md`：范围、角色权限概览、页面清单、共性规则
- `F03/F03_01_DocumentSearch.md`：文档查询列表页（筛选区、列表区、批量导出/批量导入查询入口）
- `F03/F03_02_DocumentDetail.md`：文档详情页（基本信息/扩展信息/归档信息/附件/操作日志）
- `F03/F03_03_AttachmentPreview.md`：附件预览页（受控水印、缩放、缩略图、下载）
- `F03/F03_04_BatchExport.md`：批量导出交互与任务生成
- `F03/F03_05_MyExports.md`：我的导出 / 工作空间「导出查询」（任务列表、下载、删除）
- `F03/F03_06_MyImportQueries.md`：工作空间「导入查询」（系统通用任务列表，结构对齐 `F03_05`）
- `F03/F03_07_MyDrafts.md`：工作空间「我的草稿」（系统通用，接口待对齐）

### 1.2 非 F03 范围（仅参考，后续应归档到对应功能）

- `F03/F03_08_PendingArchiveBatchCreate.md`：应归档数据列表「批量创建」CSV 模板字段口径（code/展示值、改造目标；与文档查询「批量导入查询」区分）
- `F03/F03_90_ArchiveReceivableData_Reference.md`：应归档数据管理/导入查询（HTML 原型参考），**不计入 F03 验收范围**

## 2. 强制遵循

- 需求规格结构：`/.docs/features/Template.md`
- 数据模型规范：`/.docs/01_DataModel.md`
- **REST API 全局约定**：`/.docs/05_API_Conventions.md`（F03 接口表已按该规范统一为扁平资源与 `/page`、`/search-page`、`/export` 等）
- 安全与权限：`/.docs/03_Security.md`
- 术语口径：`/.docs/04_Glossary.md`

