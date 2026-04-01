# F07_02_01 需求审核人审批（最小部门主管）（Demo v2）

## 1. 场景

需求审核人（默认自动关联为使用人所在最小部门任命主管）对借阅申请进行审批，可同意、驳回、转审；申请环节可改派后以改派结果为准。

## 2. 页面结构（UI/UX）

### 2.1 页面分区

- Header：流程节点图 + 审批任务信息
- 按钮区（底部）：审批意见、抄送人、同意/驳回/转审

## 3. 功能详细规格（UI/UX §5.2 页面分区模块）

### 3.1 Header 区字段表

| 字段名称 | 字段名-英文 | 类型 | 字段逻辑说明 | 备注 | 序号 |
|---|---|---|---|---|---:|
| 流程节点图 | Process Node Graph | 组件 | COMPUTE：按状态渲染当前在“待需求审核人审批” | 固定顶栏 | 1 |
| 审批任务号 | Approval Task No | 文本 | DB：fdc_borrow_task_t.task_no | 只读 | 2 |
| 借阅单信息 | Borrow Order Summary | 文本块 | FK：fdc_borrow_task_t.borrow_order_id -> fdc_borrow_order_t.* | 只读 | 3 |
| 审核人来源 | Reviewer Source | 文本 | API：GET /org/min-unit-supervisor（入参：user_id={使用人}） | 默认自动带出，可在申请环节改派 |

### 3.2 按钮区字段表（底部）

| 字段名称 | 字段名-英文 | 类型 | 字段逻辑说明 | 备注 | 序号 |
|---|---|---|---|---|---:|
| 审批意见 | Approval Opinion | 多行文本 | DB：fdc_borrow_task_action_log_t.opinion | 同意/驳回/转审均可填写 | 1 |
| 抄送人 | Cc Users | 人员多选 | API：GET /users/search（出参映射：user_id -> fdc_borrow_cc_t.cc_user_id） | 非必填 | 2 |

### 3.3 按钮区按钮表（底部）

| 按钮名称 | 按钮名称-英文 | 显示位置 | 按钮逻辑说明（含调用UI API） | 绑定权限项名称 | 新增权限项说明 | 编号 |
|---|---|---|---|---|---|---:|
| 同意 | Approve | 底部右侧 | UI动作：提交审批；BE/资源：POST /borrow-tasks/{id}/approve；入参来源：approval_opinion/cc_user_ids；结果处理：按策略流转下一节点 | `fdc:borrow:approve` | 新增 | 1 |
| 驳回 | Reject | 底部右侧 | UI动作：二次确认后驳回；BE/资源：POST /borrow-tasks/{id}/reject；入参来源：approval_opinion/cc_user_ids；结果处理：主单/明细进入已驳回 | `fdc:borrow:approve` | 复用 | 2 |
| 转审 | Transfer | 底部右侧 | UI动作：选择新审批人；BE/资源：POST /borrow-tasks/{id}/transfer；入参来源：target_user_id/approval_opinion/cc_user_ids；结果处理：当前任务关闭并生成新任务 | `fdc:borrow:approve:transfer` | 新增 | 3 |

## 4. 规则与策略（Rules）

- **R-0201**：需求审核人审批节点支持同意、驳回、转审。
- **R-0202**：本节点允许抄送。
- **R-0203**：默认审批人为“使用人所在最小部门任命主管”；若申请环节改派，则按改派人生成任务。

## 5. 验收标准（AC）

- **AC-0201**：需求审核人可完成同意、驳回、转审，且状态流转正确。
- **AC-0202**：申请环节改派后，本节点任务应分配给改派人。

## 6. 测试点（TC）

- **TC-0201**：转审后原审批任务关闭，新审批人待办生成。
- **TC-0202**：未改派时默认任务分配给最小部门主管；改派后分配给改派人。
