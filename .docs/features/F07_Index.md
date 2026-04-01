# F07 借阅文档（索引）

本目录用于沉淀 F07「借阅文档」功能规格真相来源（SSoT）。

## 1. 文档清单

| 文档 | 说明 |
|---|---|
| `F07/F07_00_Overview.md` | 范围、与真相文档对齐说明、子文档路由、待决事项 |
| `F07/F07_01_BorrowApplication.md` | 借阅申请（Demo v1） |
| `F07/F07_02_BorrowWorkflowProcessing.md` | 借阅流程办理（场景索引） |
| `F07/F07_02_01_SupervisorApproval.md` | 需求审核人审批（最小部门主管，单场景规格） |
| `F07/F07_02_01B_DemandApproverApproval.md` | 需求审批人审批（L3 主管，单场景规格） |
| `F07/F07_02_02_DispatchClaim.md` | 借阅分单（候选抢单，单场景规格） |
| `F07/F07_02_03_LendingApproval.md` | 借出审批（单场景规格） |
| `F07/F07_02_04_LendingHandling.md` | 借阅办理（单场景规格） |
| `F07/F07_02_05_ReturnAndClose.md` | 使用人确认归还与结束（单场景规格） |
| `F07/F07_03_Renewal.md` | 续借申请（待编写） |
| `F07/F07_04_BorrowRecords.md` | 借阅记录管理（待编写） |
| `F07/F07_05_SmartBorrow.md` | 智能借阅（待编写） |

> 子文档落地前，业务口径、字段与 API 以 `F07_00` 的「合规约束」为准；子文档须按 `/.docs/features/Template.md` 补全 R/AC/TC 与权限矩阵。

## 2. 强制遵循

- 需求规格结构：`/.docs/features/Template.md`
- 数据模型：`/.docs/01_DataModel.md`（**当前尚无借阅域实体；新增字段须在子文档「数据模型设计」中列出并映射至修订后的 `01`）**
- 工作流状态机：`/.docs/02_Workflow.md`（**当前尚无借阅电子流；须在子文档与 `02` 对齐后验收**）
- 安全与权限：`/.docs/03_Security.md`（已含 `FDC_ROLE_BORROW_LIAISON`、`FDC_ROLE_END_USER` 等与借阅相关的角色描述）
- 术语：`/.docs/04_Glossary.md`
- REST 约定：`/.docs/05_API_Conventions.md`
- 快码：`/.docs/06_Lookup.md`
