# F07_02_02 借阅分单（候选抢单）（Demo v1）

## 1. 场景

候选分单人抢单认领后，为每条借阅明细补充借出审批人与办理人路由信息。

## 2. 页面结构（UI/UX）

### 2.1 页面分区

- Header：流程节点图 + 借阅单号 + 分单策略
- 列表区：每条明细的澄清与路由配置
- 按钮区（底部）：候选分单人、认领信息、审批意见、抄送人、抢单认领/确认分单/退回

## 3. 功能详细规格（UI/UX §5.2 页面分区模块）

### 3.1 Header 区字段表

| 字段名称 | 字段名-英文 | 类型 | 字段逻辑说明 | 备注 | 序号 |
|---|---|---|---|---|---:|
| 流程节点图 | Process Node Graph | 组件 | COMPUTE：按状态渲染当前在“待分单” | 固定顶栏 | 1 |
| 借阅单号 | Borrow Order No | 文本 | DB：fdc_borrow_order_t.borrow_order_no | 只读 | 2 |
| 分单策略 | Dispatch Mode | 文本 | LOOKUP：FDC_BORROW_DISPATCH_MODE（CANDIDATE_CLAIM） | 本期固定候选抢单 | 3 |

### 3.2 列表区字段表（每条明细）

| 字段名称 | 字段名-英文 | 类型 | 字段逻辑说明 | 备注 | 序号 |
|---|---|---|---|---|---:|
| 需求澄清内容 | Clarification | 多行文本 | DB：fdc_borrow_line_route_t.clarification | 表单式需求必填 | 1 |
| 借出审批人集合 | Lend Approvers | 人员多选 | API：GET /users/search；结果映射到 fdc_borrow_line_route_t.approver_user_ids | 支持多审批人 | 2 |
| 借出审批策略 | Approval Strategy | 下拉单选 | LOOKUP：FDC_BORROW_APPROVAL_STRATEGY（ALL/ANY/SEQ） | 默认 ALL | 3 |
| 办理人集合 | Handlers | 人员多选 | API：GET /users/search；结果映射到 fdc_borrow_line_route_t.handler_user_ids | 支持多办理人 | 4 |
| 办理策略 | Handler Strategy | 下拉单选 | LOOKUP：FDC_BORROW_HANDLER_STRATEGY（ASSIGN/CLAIM） | 默认 ASSIGN | 5 |

### 3.3 按钮区字段表（底部）

| 字段名称 | 字段名-英文 | 类型 | 字段逻辑说明 | 备注 | 序号 |
|---|---|---|---|---|---:|
| 候选分单人集合 | Dispatch Candidate Users | 人员多选 | API：GET /users/by-role?role_code=FDC_ROLE_BORROW_LIAISON；结果映射到 fdc_borrow_dispatch_pool_t.candidate_user_id | 支持多人 | 1 |
| 分单认领人 | Dispatcher Claimer | 文本 | COMPUTE：从候选集合中按抢单产生认领记录 | 抢单后只读 | 2 |
| 审批意见 | Approval Opinion | 多行文本 | DB：fdc_borrow_task_action_log_t.opinion | 非必填 | 3 |
| 抄送人 | Cc Users | 人员多选 | API：GET /users/search；结果映射到 fdc_borrow_cc_t.cc_user_id | 非必填 | 4 |

### 3.4 按钮区按钮表（底部）

| 按钮名称 | 按钮名称-英文 | 显示位置 | 按钮逻辑说明（含调用UI API） | 绑定权限项名称 | 新增权限项说明 | 编号 |
|---|---|---|---|---|---|---:|
| 抢单认领 | Claim Dispatch | 底部右侧 | UI动作：认领分单任务；BE/资源：POST /borrow-dispatch-tasks/{id}/claim；入参来源：current_user_id；结果处理：认领成功后允许编辑分单明细 | `fdc:borrow:dispatch:claim` | 新增 | 1 |
| 确认分单 | Confirm Dispatch | 底部右侧 | UI动作：提交分单；BE/资源：POST /borrow-orders/{id}/dispatch；入参来源：各明细路由配置+approval_opinion+cc_user_ids；结果处理：生成借出审批任务 | `fdc:borrow:dispatch` | 新增 | 2 |
| 退回申请人 | Return To Applicant | 底部右侧 | UI动作：退回并填写原因；BE/资源：POST /borrow-orders/{id}/return；入参来源：approval_opinion；结果处理：主单进入已驳回 | `fdc:borrow:dispatch:return` | 新增 | 3 |

## 4. 规则与策略（Rules）

- **R-0210**：借阅分单采用“多人候选+抢单认领”模式；未认领不可编辑分单内容。
- **R-0211**：支持明细级审批策略与办理策略配置。

## 5. 验收标准（AC）

- **AC-0210**：分单支持多人候选，任一候选人抢单成功后进入可编辑状态，其余候选人不可再认领。
- **AC-0211**：分单提交后正确生成借出审批任务。

## 6. 测试点（TC）

- **TC-0210**：同一分单任务由两个候选人并发抢单，系统仅允许一人成功。
