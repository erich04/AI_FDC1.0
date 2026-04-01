# F07_01 借阅申请（Demo v2）

## 1. 场景

申请人发起借阅申请，支持两种明细录入方式：表单式需求描述、从系统选择具体档案。提交后先后进入“需求审核人审批”“需求审批人审批”。

## 2. 页面结构（UI/UX）

### 2.1 页面分区

- Header：流程节点图 + 借阅单基本信息
- 申请信息填写区：使用人、用途、申请原因、两级审批人、申请环节抄送人
- 申请明细填写区：表单式需求 / 选档式需求
- 按钮区（底部）：保存草稿、提交、撤回

## 3. 功能详细规格（UI/UX §5.2 页面分区模块）

### 3.1 Header 区

**字段表**


| 字段名称 | 字段名-英文 | 类型 | 字段逻辑说明 | 备注 | 序号 |
|---|---|---|---|---|---:|
| 流程节点图 | Process Node Graph | 组件 | COMPUTE：按主单当前状态渲染已完成/进行中/未完成节点 | 全页面固定顶栏 | 1 |
| 借阅单号 | Borrow Order No | 文本 | DB：fdc_borrow_order_t.borrow_order_no | 草稿阶段可为空，提交后生成 | 2 |
| 申请人 | Applicant | 文本 | DB：fdc_borrow_order_t.applicant_user_id；FK：fdc_borrow_order_t.applicant_user_id -> tpl_user_t.user_id | 只读 | 3 |
| 申请时间 | Apply Time | 日期时间 | DB：fdc_borrow_order_t.creation_date | 只读 | 4 |


### 3.2 申请信息填写区

**字段表**


| 字段名称 | 字段名-英文 | 类型 | 字段逻辑说明 | 备注 | 序号 |
|---|---|---|---|---|---:|
| 使用人工号 | User Employee No | 输入+选择 | API：GET /users/by-employee-no（入参：employee_no；出参映射：user_id/employee_name/dept_code） | 必填 | 1 |
| 使用人姓名 | User Name | 文本 | API：GET /users/by-employee-no（出参映射：employee_name） | 自动带出，只读 | 2 |
| 使用人部门 | User Dept | 文本 | API：GET /users/by-employee-no（出参映射：dept_code/dept_name） | 自动带出，只读 | 3 |
| 申请用途 | Borrow Purpose | 下拉单选 | LOOKUP：FDC_BORROW_PURPOSE（展示字段：label；取值码：fdc_borrow_order_t.borrow_purpose_code） | 必填 | 4 |
| 申请原因 | Apply Reason | 多行文本 | DB：fdc_borrow_order_t.apply_reason | 必填，长度上限 500 | 5 |
| 需求审核人 | Demand Reviewer | 人员选择 | API：GET /org/min-unit-supervisor（入参：user_id={使用人}; 出参映射：supervisor_user_id）；默认自动带出，申请页可修改 | 提交后第1审批节点 | 6 |
| 需求审批人 | Demand Approver | 人员选择 | API：GET /org/l3-supervisor（入参：user_id={使用人}; 出参映射：supervisor_user_id）；默认自动带出，申请页可修改 | 提交后第2审批节点 | 7 |
| 抄送人（申请环节） | Apply Cc Users | 人员多选 | API：GET /users/search；保存到 fdc_borrow_cc_t（环节=申请） | 非必填，可多人 | 8 |


### 3.3 申请明细填写区

> 明细录入模式为**二选一**：用户只能选择一种方式填写，不可混用。
> - 模式 A：按需求内容填写
> - 模式 B：添加档案

#### 模块 M1：按需求内容填写

**字段表**


| 字段名称 | 字段名-英文 | 类型 | 字段逻辑说明 | 备注 | 序号 |
|---|---|---|---|---|---:|
| 归档主体 | Archived Entity | 下拉选择 | FK：fdc_borrow_order_line_t.archived_entity_unit_id -> fdc_archived_entity_unit_t.archived_entity_unit_name | 必填 | 1 |
| 文档类型 | Document Type | 下拉选择 | FK：fdc_borrow_order_line_t.document_type_id -> fdc_document_type_t.document_type_name | 必填 | 2 |
| 文档需求说明 | Demand Description | 多行文本 | DB：fdc_borrow_order_line_t.demand_description | 必填 | 3 |
| 载体类型 | Carrier Type | 下拉单选 | LOOKUP：FDC_CARRIER_TYPE | 必填 | 4 |
| 需求类型（借阅内容类型） | Demand Type / Borrow Item Type | 下拉单选 | LOOKUP：FDC_BORROW_ITEM_TYPE（原件、电子件/扫描件、复印件）；当载体类型=电子件时，仅可选电子件/扫描件 | 必填 | 5 |
| 行级日期字段 | Row-level Date Fields | — | 不在明细行内填写；统一在 3.3 下方“明细区补充选项”按明细类型集合动态出现 | 统一控制 | 5 |
| 操作 | Row Actions | 图标按钮 | UI动作：每行提供删除图标；删除前二次确认；删除后重算明细数量 | 行级操作 | 7 |


#### 模块 M2：添加档案

**字段表**


| 字段名称 | 字段名-英文 | 类型 | 字段逻辑说明 | 备注 | 序号 |
|---|---|---|---|---|---:|
| 文档ID | Document ID | 隐藏字段 | DB：fdc_borrow_order_line_t.doc_id | 由选档弹窗返回 | 1 |
| 文档名称 | Document Name | 文本 | FK：fdc_borrow_order_line_t.doc_id -> fdc_doc_t.doc_name | 只读 | 2 |
| 载体类型 | Carrier Type | 文本 | LOOKUP：FDC_CARRIER_TYPE（取值码：fdc_doc_t.carrier_type） | 只读 | 3 |
| 需求类型（借阅内容类型） | Demand Type / Borrow Item Type | 下拉单选 | LOOKUP：FDC_BORROW_ITEM_TYPE（ORIGINAL/ELECTRONIC_OR_SCAN/COPY）；当载体类型=电子件时，仅可选电子件/扫描件 | 必填（选中档案后填写） | 4 |
| 附件选择 | Attachment Selection | 多选 | API：GET /documents/{doc_id}/attachments（入参：doc_id；出参映射：doc_att_id/file_name） | 借电子件时可选 | 5 |
| 选中附件数 | Selected Attachment Count | 数值 | COMPUTE：统计 attachment_ids 数量写入 fdc_borrow_order_line_t.selected_attachment_count | 自动计算 | 6 |
| 操作 | Row Actions | 图标按钮 | UI动作：每行提供删除图标；删除前二次确认；删除后重算明细数量 | 行级操作 | 7 |

#### 3.3.1 明细模式与添加交互

| 规则项 | 说明 |
|---|---|
| 模式选择 | 页面提供“按需求内容填写 / 添加档案”单选切换，且互斥；切换模式前需提示当前模式已录入数据将清空（确认后执行）。 |
| 模式 A 添加 | 点击“添加”按钮新增一行 M1 空白明细；每行操作列展示删除图标。 |
| 模式 B 添加 | 点击“添加”按钮弹出档案搜索栏（弹窗/抽屉）；用户勾选档案后，需选择需求类型（借阅内容类型）再添加入列表。 |
| 载体-需求类型约束 | 任一环节（申请/办理）当载体类型=电子件时，需求类型仅允许“电子件/扫描件”。 |
| 行删除 | 任一模式下点击删除图标执行行删除并重算明细数量；删除最后一行后，主单视为无明细。 |

#### 3.3.2 明细区补充选项（位于 3.3 下方）

| 选项名称 | 字段名-英文 | 类型 | 字段逻辑说明 | 备注 |
|---|---|---|---|---|
| 原件是否归还 | Original Need Return | 单选 | COMPUTE：当任一明细 `需求类型` 包含原件（`ORIGINAL`）时显示；否则隐藏 | 明细区下方动态显示 |
| 归还日期 | Return Due Date | 日期选择器 | COMPUTE：仅当“原件是否归还=是”时显示；并限制 `<= 当前日期 + 30 天` | 必填条件项 |
| 下载有效期 | Download Expire Date | 日期选择器 | COMPUTE：当任一明细 `需求类型` 包含电子件/扫描件（`ELECTRONIC_OR_SCAN`）时显示；并限制 `<= 当前日期 + 90 天` | 必填条件项 |

### 3.4 按钮区（底部）

**按钮表**

| 按钮名称 | 按钮名称-英文 | 显示位置 | 按钮逻辑说明（含调用UI API） | 绑定权限项名称 | 新增权限项说明 | 编号 |
|---|---|---|---|---|---|---:|
| 保存草稿 | Save Draft | 页面底部右侧 | UI动作：保存当前表单；BE/资源：POST /borrow-orders/draft；入参来源：页面字段+明细草稿；结果处理：成功提示并返回借阅单号 | `fdc:borrow:apply:draft` | 新增 | 1 |
| 提交申请 | Submit Apply | 页面底部右侧 | UI动作：提交；BE/资源：POST /borrow-orders/submit；入参来源：主单+明细；结果处理：成功后进入待审批并触发主管待办 | `fdc:borrow:apply:submit` | 新增 | 2 |
| 撤回申请 | Withdraw Apply | 页面底部右侧 | UI动作：二次确认后撤回；BE/资源：POST /borrow-orders/{id}/withdraw；入参来源：borrow_order_id；结果处理：成功变更为已撤销 | `fdc:borrow:apply:withdraw` | 新增 | 3 |

### 3.5 明细区按钮（区域内）

| 按钮名称 | 按钮名称-英文 | 显示位置 | 按钮逻辑说明（含调用UI API） | 绑定权限项名称 | 新增权限项说明 | 编号 |
|---|---|---|---|---|---|---:|
| 添加 | Add Line / Add Archive | 申请明细区右上 | UI动作：模式 A 新增空白行；模式 B 打开档案搜索弹窗，勾选并选择需求类型后添加 | `fdc:borrow:apply:edit-detail` | 新增 |
| 删除（图标） | Delete Row | 明细表操作列 | UI动作：删除当前行；结果处理：删除后重算明细并刷新汇总状态 | `fdc:borrow:apply:edit-detail` | 复用 |


## 4. 规则与策略（Rules）

- **R-001**：申请阶段支持多条明细，主单保存时必须至少包含 1 条明细。
- **R-001A**：申请明细录入模式为互斥二选一（按需求内容填写 / 添加档案），同一借阅单不可混用两种模式。
- **R-002**：主单是否“包含原件借阅/包含电子件扫描件”由明细 `需求类型（借阅内容类型）` 自动判定，不在申请信息区人工填写。
- **R-003**：明细行不填写“是否归还”和“归还日期/下载有效期”；系统在明细区下方按明细类型集合动态展示补充选项。
- **R-003A**：当明细包含原件类型（`ORIGINAL`）时，显示“原件是否归还”；若选择“是”，显示“归还日期”并限制 `<= 当前日期 + 30 天`。
- **R-003B**：当明细包含电子件/扫描件类型（`ELECTRONIC_OR_SCAN`）时，显示“下载有效期”并限制 `<= 当前日期 + 90 天`。
- **R-004**：申请提交后先进入“需求审核人审批”（默认最小部门任命主管，可在申请页改），再进入“需求审批人审批”（默认 L3 主管，可在申请页改）。
- **R-005**：`需求类型` 与 `借阅内容类型` 是同一概念，统一使用 LOOKUP：`FDC_BORROW_ITEM_TYPE`（原件、电子件/扫描件、复印件）。
- **R-006**：申请环节支持抄送多人；提交时抄送信息随申请单保存并触发通知。
- **R-007**：申请与办理环节统一执行载体-需求类型约束：当载体类型=电子件时，需求类型仅允许“电子件/扫描件”。

## 5. 验收标准（AC）

- **AC-001**：申请人可保存草稿，重新进入页面后可继续编辑并提交。
- **AC-002**：提交时按明细自动判定主单是否需要归还日期/下载有效期，并执行相应规则校验；不满足时阻断并提示。
- **AC-003**：支持同一申请包含多条明细，但明细录入模式为二选一，不可混合“按需求内容填写”和“添加档案”。
- **AC-004**：模式 A 点击“添加”可新增明细行且支持行删除；模式 B 点击“添加”必须经“选档 + 选择需求类型”后方可入表。
- **AC-005**：当存在原件类型明细时，自动出现“原件是否归还”；选“是”后才显示并校验“归还日期（<=30天）”。
- **AC-006**：当存在电子件/扫描件类型明细时，自动出现并校验“下载有效期（<=90天）”。
- **AC-007**：提交时自动带出两级审批人（最小部门主管、L3主管），且申请人可在申请页改派。
- **AC-008**：申请环节可选择多个抄送人并随提交生效。
- **AC-009**：当载体类型=电子件时，需求类型仅可选择“电子件/扫描件”；其他选项不可选。

## 6. 测试点（TC）

- **TC-001**：原件借阅日期超过 30 天应拦截提交。
- **TC-002**：电子件有效期超过 90 天应拦截提交。
- **TC-003**：无明细时提交失败。
- **TC-004**：提交成功后主管待办生成。
- **TC-005**：已录入模式 A 明细后切换到模式 B，系统提示将清空原明细，确认后成功切换。
- **TC-006**：模式 B 未选择需求类型时，勾选档案后不可添加入表并提示。
- **TC-007**：删除最后一条原件类型明细后，“原件是否归还/归还日期”自动隐藏。
- **TC-008**：删除最后一条电子件/扫描件类型明细后，“下载有效期”自动隐藏。
- **TC-009**：提交后先进入需求审核人审批，再进入需求审批人审批，顺序正确。
- **TC-010**：申请页改派任一级审批人后，流程任务应分配给改派人。
- **TC-011**：申请环节选择多个抄送人，提交后均收到通知。
- **TC-012**：载体类型=电子件时，需求类型下拉仅保留“电子件/扫描件”。

