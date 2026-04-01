# F07_02_04 借阅办理（Demo v1）

## 1. 场景

办理人对已审批通过的借阅明细执行借出办理，可确认借出或转派办理。

## 2. 页面结构（UI/UX）

### 2.1 页面分区

- Header：流程节点图 + 借阅单号 + 办理策略
- 列表区：借出内容确认、借出方式、电子件预览、原件借出份数
- 按钮区（底部）：审批意见、抄送人、确认办理借出/办理转派

## 3. 功能详细规格（UI/UX §5.2 页面分区模块）

### 3.1 Header 区字段表

| 字段名称 | 字段名-英文 | 类型 | 字段逻辑说明 | 备注 | 序号 |
|---|---|---|---|---|---:|
| 流程节点图 | Process Node Graph | 组件 | COMPUTE：按状态渲染当前在“待办理借出” | 固定顶栏 | 1 |
| 借阅单号 | Borrow Order No | 文本 | DB：fdc_borrow_order_t.borrow_order_no | 只读 | 2 |
| 办理策略 | Handler Strategy | 文本 | DB：fdc_borrow_line_route_t.handler_strategy | 只读 | 3 |

### 3.2 列表区字段表（办理视角）

| 字段名称 | 字段名-英文 | 类型 | 字段逻辑说明 | 备注 | 序号 |
|---|---|---|---|---|---:|
| 借出内容确认 | Lending Content Confirm | 勾选 | DB：fdc_borrow_order_line_t.lending_confirm_flag | 办理人确认 | 1 |
| 借出方式（需求类型） | Lending Method / Demand Type | 下拉 | LOOKUP：FDC_BORROW_ITEM_TYPE（ORIGINAL/ELECTRONIC_OR_SCAN/COPY） | 只允许已审批通过类型 | 2 |
| 电子件预览 | External Preview | 链接按钮 | API：GET /document-attachments/{id}/preview（外部预览URL） | 办理人可预览 | 3 |
| 原件借出份数 | Original Borrow Qty | 数值 | COMPUTE：确认借出时扣减 fdc_arch_t.remaining_copies_quantity | 原件必填 | 4 |

### 3.3 按钮区字段表（底部）

| 字段名称 | 字段名-英文 | 类型 | 字段逻辑说明 | 备注 | 序号 |
|---|---|---|---|---|---:|
| 审批意见 | Approval Opinion | 多行文本 | DB：fdc_borrow_task_action_log_t.opinion | 非必填 | 1 |
| 抄送人 | Cc Users | 人员多选 | API：GET /users/search；结果映射到 fdc_borrow_cc_t.cc_user_id | 非必填 | 2 |

### 3.4 按钮区按钮表（底部）

| 按钮名称 | 按钮名称-英文 | 显示位置 | 按钮逻辑说明（含调用UI API） | 绑定权限项名称 | 新增权限项说明 | 编号 |
|---|---|---|---|---|---|---:|
| 确认办理借出 | Confirm Lending | 底部右侧 | UI动作：确认办理；BE/资源：POST /borrow-lines/{line_id}/lend-confirm；入参来源：借出项选择+approval_opinion+cc_user_ids；结果处理：进入待归还/待确认接收 | `fdc:borrow:handle:lend` | 新增 | 1 |
| 办理转派 | Reassign Handle | 底部右侧 | UI动作：转派办理人；BE/资源：POST /borrow-tasks/{id}/reassign | `fdc:borrow:handle:reassign` | 新增 | 2 |

## 4. 规则与策略（Rules）

- **R-0230**：办理节点支持 `ASSIGN/CLAIM`，同一明细同一时刻只允许一个办理任务处于进行中。
- **R-0231**：原件办理借出成功时扣减档案剩余份数。

## 5. 验收标准（AC）

- **AC-0230**：办理借出后，原件份数正确扣减并限制后续借出。

## 6. 测试点（TC）

- **TC-0230**：原件剩余份数为 0 时，办理借出失败并提示。
