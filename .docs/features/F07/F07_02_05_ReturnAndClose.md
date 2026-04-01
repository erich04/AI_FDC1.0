# F07_02_05 使用人确认归还与结束（Demo v1）

## 1. 场景

使用人下载电子件或确认归还实物，办理人最终确认借阅单结束。

## 2. 页面结构（UI/UX）

### 2.1 页面分区

- Header：流程节点图 + 借阅单号
- 列表区：可下载附件、下载有效期、归还确认信息
- 按钮区（底部）：审批意见、抄送人、下载电子件/确认归还/结束确认

## 3. 功能详细规格（UI/UX §5.2 页面分区模块）

### 3.1 Header 区字段表

| 字段名称 | 字段名-英文 | 类型 | 字段逻辑说明 | 备注 | 序号 |
|---|---|---|---|---|---:|
| 流程节点图 | Process Node Graph | 组件 | COMPUTE：按状态渲染当前在“待确认接收/待归还” | 固定顶栏 | 1 |
| 借阅单号 | Borrow Order No | 文本 | DB：fdc_borrow_order_t.borrow_order_no | 只读 | 2 |

### 3.2 列表区字段表（使用人视角）

| 字段名称 | 字段名-英文 | 类型 | 字段逻辑说明 | 备注 | 序号 |
|---|---|---|---|---|---:|
| 可下载附件列表 | Downloadable Attachments | 列表 | API：GET /borrow-orders/{id}/attachments（按有效期与权限过滤） | 电子件下载 | 1 |
| 下载有效期 | Download Expire At | 日期时间 | DB：fdc_borrow_order_t.download_expire_date | 超时后禁用下载 | 2 |
| 归还确认标识 | Return Confirm Flag | 勾选 | DB：fdc_borrow_order_line_t.return_confirm_flag | 实物归还时必填 | 3 |
| 归还确认时间 | Return Confirm Time | 日期时间 | DB：fdc_borrow_order_line_t.return_confirm_time | 系统写入 | 4 |

### 3.3 按钮区字段表（底部）

| 字段名称 | 字段名-英文 | 类型 | 字段逻辑说明 | 备注 | 序号 |
|---|---|---|---|---|---:|
| 审批意见 | Approval Opinion | 多行文本 | DB：fdc_borrow_task_action_log_t.opinion | 办理人结束时可填写 | 1 |
| 抄送人 | Cc Users | 人员多选 | API：GET /users/search；结果映射到 fdc_borrow_cc_t.cc_user_id | 非必填 | 2 |

### 3.4 按钮区按钮表（底部）

| 按钮名称 | 按钮名称-英文 | 显示位置 | 按钮逻辑说明（含调用UI API） | 绑定权限项名称 | 新增权限项说明 | 编号 |
|---|---|---|---|---|---|---:|
| 下载电子件 | Download File | 页面底部右侧 | UI动作：下载；BE/资源：GET /borrow-attachments/{id}/download；入参来源：attachment_id；结果处理：成功下载并记审计 | `fdc:borrow:file:download` | 新增 | 1 |
| 确认归还 | Confirm Return | 页面底部右侧 | UI动作：确认归还；BE/资源：POST /borrow-lines/{line_id}/return-confirm；入参来源：line_id；结果处理：明细进入待确认接收/已完成 | `fdc:borrow:return:confirm` | 新增 | 2 |
| 结束确认 | Close Borrow | 页面底部右侧 | UI动作：办理人确认结束；BE/资源：POST /borrow-orders/{id}/close；入参来源：borrow_order_id/approval_opinion/cc_user_ids；结果处理：全部明细完成后主单已完成 | `fdc:borrow:close` | 新增 | 3 |

## 4. 规则与策略（Rules）

- **R-0240**：电子件下载受下载有效期控制，过期不可下载。
- **R-0241**：实物归还确认后才能进入结束确认。

## 5. 验收标准（AC）

- **AC-0240**：下载有效期到期后，电子件下载入口自动关闭。
- **AC-0241**：全部明细满足完成条件后，主单才可结束。

## 6. 测试点（TC）

- **TC-0240**：过期下载请求被拒绝。
- **TC-0241**：非全部明细完成时，结束确认失败。
