# F07_02_03 借出审批（Demo v1）

## 1. 场景

借出审批人对分单后的借阅明细进行借出审批，支持多人审批策略。

## 2. 页面结构（UI/UX）

### 2.1 页面分区

- Header：流程节点图 + 借阅单号 + 当前审批策略
- 列表区：明细状态、审批进度
- 按钮区（底部）：审批意见、抄送人、同意借出/驳回借出/转审

## 3. 功能详细规格（UI/UX §5.2 页面分区模块）

### 3.1 Header 区字段表

| 字段名称 | 字段名-英文 | 类型 | 字段逻辑说明 | 备注 | 序号 |
|---|---|---|---|---|---:|
| 流程节点图 | Process Node Graph | 组件 | COMPUTE：按状态渲染当前在“待借出审批” | 固定顶栏 | 1 |
| 借阅单号 | Borrow Order No | 文本 | DB：fdc_borrow_order_t.borrow_order_no | 只读 | 2 |
| 当前审批策略 | Current Approval Strategy | 文本 | DB：fdc_borrow_line_route_t.approval_strategy | 只读 | 3 |

### 3.2 列表区字段表（审批视角）

| 字段名称 | 字段名-英文 | 类型 | 字段逻辑说明 | 备注 | 序号 |
|---|---|---|---|---|---:|
| 明细状态 | Line Status | 文本 | DB：fdc_borrow_order_line_t.line_status | 只读 | 1 |
| 审批策略 | Approval Strategy | 文本 | DB：fdc_borrow_line_route_t.approval_strategy | 只读 | 2 |
| 已审批人 | Approved Users | 标签列表 | API：GET /borrow-lines/{line_id}/approval-progress | 实时显示 | 3 |

### 3.3 按钮区字段表（底部）

| 字段名称 | 字段名-英文 | 类型 | 字段逻辑说明 | 备注 | 序号 |
|---|---|---|---|---|---:|
| 审批意见 | Approval Opinion | 多行文本 | DB：fdc_borrow_task_action_log_t.opinion | 同意/驳回/转审均可填写 | 1 |
| 抄送人 | Cc Users | 人员多选 | API：GET /users/search（出参映射：user_id -> fdc_borrow_cc_t.cc_user_id） | 非必填 | 2 |

### 3.4 按钮区按钮表（底部）

| 按钮名称 | 按钮名称-英文 | 显示位置 | 按钮逻辑说明（含调用UI API） | 绑定权限项名称 | 新增权限项说明 | 编号 |
|---|---|---|---|---|---|---:|
| 同意借出 | Approve Lending | 底部右侧 | UI动作：提交同意；BE/资源：POST /borrow-tasks/{id}/approve-lending；入参来源：approval_opinion/cc_user_ids；结果处理：策略达成后流转到待办理借出 | `fdc:borrow:lending:approve` | 新增 | 1 |
| 驳回借出 | Reject Lending | 底部右侧 | UI动作：提交驳回；BE/资源：POST /borrow-tasks/{id}/reject-lending；入参来源：approval_opinion/cc_user_ids；结果处理：明细置为已驳回 | `fdc:borrow:lending:approve` | 复用 | 2 |
| 转审 | Transfer | 底部右侧 | UI动作：转审；BE/资源：POST /borrow-tasks/{id}/transfer；入参来源：target_user_id/approval_opinion/cc_user_ids | `fdc:borrow:lending:transfer` | 新增 | 3 |

## 4. 规则与策略（Rules）

- **R-0220**：借出审批支持 `ALL/ANY/SEQ` 多人策略。
- **R-0221**：本节点允许抄送。

## 5. 验收标准（AC）

- **AC-0220**：`ALL/ANY/SEQ` 三种策略均按预期流转。

## 6. 测试点（TC）

- **TC-0220**：`ANY` 策略下首个同意后，其余审批任务自动关闭。
