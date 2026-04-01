# F03_02 文档详情

> 参考：旧规格 `1.2 规格设计-文档详情` + HTML `reference_html/pages/document_detail.html`。

## 1. 业务场景

- **S-01 查看文档详情**
  - **触发**：从文档查询列表点击“文档业务编码”
  - **系统响应**：加载并展示基本信息、扩展信息、归档信息、附件列表、操作日志

- **S-02 附件预览/下载**
  - **触发**：附件列表操作列点击“预览/下载”，或“批量下载”
  - **系统响应**：按权限执行；预览时获取该附件的唯一标识 `EDM_ID` 并打开外部系统的对应预览页面（本系统不再提供内部“附件预览页”）

## 2. 页面结构（与原型一致）

- **Header**：返回到文档查询 + 搜索框 + 用户信息
- **面包屑**：首页 / 文档查询 / 文档详情
- **标题区**：文档名称 + 状态标签（示例：电子归档/已生效/密级）+ 编辑按钮（是否属于F03范围待定）
- **信息区**（折叠/展开）：
  - 文档基本信息
  - 扩展信息（原型可折叠）
  - 归档信息
- **附件列表区**
- **操作日志区**

## 3. 字段清单（旧规格沉淀）

> 字段命名/类型/长度以 `/.docs/01_DataModel.md` 为准。本节仅沉淀“展示字段集合与来源关系”。

### 3.1 文档基本信息

旧规格字段（节选）：

- 文档类型：`fdc_doc_t.business_module_id -> fdc_business_module_t.document_type_id -> fdc_document_type_t.document_type_name`
- 文档业务编码：`fdc_doc_t.doc_busi_no`
- 公司/主体：`fdc_doc_t.archived_entity_unit_id -> fdc_archived_entity_unit_t -> fdc_archived_entity_t.archived_entity_name`
- 业务模块：`fdc_doc_t.business_module_id -> fdc_business_module_t.business_module_name`
- 开始/结束档期：`fdc_doc_t.start_period` / `fdc_doc_t.end_period`
- 归档地/产生地：`fdc_doc_t.*_alpha2_code` → 行政区划服务（按层级入参）
- 归档责任人：`fdc_doc_t.owner_id` → 用户服务
- 责任部门：`fdc_doc_t.dept_code` → 部门查询服务
- 载体类型：`fdc_doc_t.carrier_type` → LOOKUP `FDC_CARRIER_TYPE`
- 系统来源：`fdc_doc_t.source_system` → LOOKUP `FDC_SOURCE_SYS`
- 密级：旧规格提到通过业务模块关联取得（需在数据模型中明确字段/来源）
- 描述/备注：`fdc_doc_t.description`
- 创建人/创建时间：`fdc_doc_t.created_by` / `fdc_doc_t.creation_date`（字段名以数据模型为准）

### 3.2 归档信息（示例字段集合）

- 条码模块、档案条码、册内序号、册号、册条码
- 文档组织、库房、库位
- 份数、剩余份数、档案类型
- 是否可见：旧规格：`fdc_doc_t` 匹配归档流向得到 `visible_flag`

### 3.3 扩展信息（待定字段集合）

旧规格列出：文号、发票号、会计、扫描员、其它归档号、项目/客户/交易对手、银行/币种/金额、票据相关字段等。  
建议在实现时以“文档类型/业务模块决定扩展字段配置”为原则，避免在 `fdc_doc_t` 上无限扩列；可考虑 EXT 表或 JSON 扩展（需与 `/.docs/01_DataModel.md` 对齐）。

## 3. 附件列表

### 4.1 列表字段（旧规格）

- 文件名：`fdc_doc_att_t.file_name`
- 附件类型：`fdc_doc_att_t.att_type`
- 大小：`fdc_doc_att_t.file_size`
- 上传时间：`fdc_doc_att_t.upload_time`
- 补充信息：`fdc_doc_att_t.additional_info`
- 操作：预览/下载（图标按钮）

### 4.2 操作与权限

- **批量下载**：需要“附件下载”权限；无权限则按钮不可见或置灰（旧规格：不可见）
- **预览**：需要“附件预览”权限；无权限则置灰
- **下载**：需要“附件下载”权限；无权限则置灰

## 4. 操作日志

### 5.1 字段（旧规格）

- 操作人：`fdc_doc_op_log_t.operated_by`
- 操作类型：`fdc_doc_op_log_t.operation_type`
- 操作内容：`fdc_doc_op_log_t.op_content`
- 时间：`fdc_doc_op_log_t.operation_time`
- 备注：`fdc_doc_op_log_t.remarks`
- 补充附件：`fdc_doc_log_att_t.log_att_name` + `edm_id`（下载）

## 5. 功能详细规格（Functional Specs）

### 5.1 业务场景与用户旅程

- **S-01 查看文档详情**
  - **前置条件**：
    - 用户具备文档查询权限；
    - 列表页存在可访问的文档记录。
  - **操作步骤**：
    - 在“文档查询列表页”点击“文档业务编码”进入详情页。
  - **系统响应**：
    - 详情页加载并展示：文档基本信息、扩展信息、归档信息、附件列表、操作日志。
  - **异常与提示**：
    - 无权限或资源不可访问时返回对应错误/提示；
    - 文档不存在时给出资源不存在提示。
  - **产出**：只读展示详情信息。

- **S-02 附件预览/下载**
  - **前置条件**：
    - 用户具备附件预览/下载权限（取决于具体按钮操作）。
  - **操作步骤**：
    - 在详情页附件列表操作列点击“预览/下载”，或点击“批量下载”。
  - **系统响应**：
    - 预览：根据附件id获取该附件 `EDM_ID`（来源：`fdc_doc_att_t.additional_info` 的唯一标识码口径），调用“附件预览”接口获取外部预览入口并打开；
    - 下载触发附件下载动作（按权限）。
  - **异常与提示**：
    - 无权限时按钮不可见/置灰；
    - 下载/预览失败给出错误提示。
  - **产出**：预览内容或下载结果。

### 5.2 页面/交互/原型说明（UI/UX）

#### 5.2.1 页面一览（本功能涉及的全部页面）
| 页面编号 | 页面名称 | 路由/入口（或菜单路径） | 页面类型 | 简述 | 详述 |
|---|---|---|---|---|---|
| P-02 | 文档详情 | 从“文档查询列表页”点击进入 | 列表页/详情页（详情页） | 展示文档基础/归档/扩展信息、附件列表、操作日志 | 见下节 P-02 |

#### 5.2.2 分页面规格（每页复制一整块）

##### P-02 《文档详情》

###### （1）页面类型与整体布局
- **页面类型**：详情页
- **布局骨架**：
  - **L1** Header：返回文档查询 + 搜索框 + 用户信息
  - **L2** 面包屑导航：显示当前页面路径
  - **L3** 标题区：显示文档名称、状态标签和操作按钮
  - **L4** 信息展示区：包含多个可折叠的信息模块（基本信息/扩展信息/归档信息）
  - **L5** 附件列表区：显示文档附件信息
  - **L6** 操作日志区：显示文档操作历史记录
- **原型与设计**：`reference_html/pages/document_detail.html`

###### （2）分区 → 模块拆解（按 old_spec 逐行对齐字段与按钮）

**〔L2 面包屑导航区〕— 模块 M0：页面路径（精简版）**
|元素名称|字段名-英文|类型|链接逻辑|
|--|--|--|--|
|首页|Home|链接|跳转到系统首页|
|文档查询|Document Search|链接|跳转到文档查询页面|
|文档详情|Document Detail|文本|当前页面|

**〔L3 标题〕— 模块 M1：标题**
|**序号**|**字段名称**|**字段名-英文**|**类型**|**字段逻辑说明**|**备注**|
|--|--|--|--|--|--|
|1|文档名称|Doc Name|文本|根据文档id在fdc_doc_t表中获取doc_name字段|字体加粗，比文档业务编码字号|
|2|文档业务编码|Doc Business No.|文本|根据文档id在fdc_doc_t表中获取doc_busi_no字段||
|3|标签|Tag|文本|待定||

**〔L4 基本信息〕— 模块 M2：文档基本信息**
|**序号**|**字段名称**|**字段名-英文**|**类型**|**字段逻辑说明**|**备注**|
|--|--|--|--|--|--|
|1|文档类型|Document Type|文本|根据文档id在fdc_doc_t表中获取business_module_id，作为外键关联fdc_business_module_t获取document_type_id, 作为外键关联fdc_document_type_t获取document_type_name||
|2|文档业务编码|Doc Business No.|文本|根据文档id在fdc_doc_t表中获取doc_busi_no字段||
|3|公司/主体|Company/Entity|文本|根据文档id在fdc_doc_t表中获取archived_entity_unit_id，作为外键关联fdc_archived_entity_unit_t获取archived_entity_id，作为外键关联fdc_archived_entity_t获取archived_entity_name||
|4|业务模块|Business Module|文本|根据文档id在fdc_doc_t表中获取business_module_id，作为外键关联fdc_business_module_t获取business_module_name||
|5|开始档期|Start Period|文本|根据文档id在fdc_doc_t表中获取start_period字段||
|6|结束档期|End Period|文本|根据文档id在fdc_doc_t表中获取end_period字段||
|7|归档地|Arch Place|文本|根据文档id在fdc_doc_t表中获取arch_place_alpha2_code字段，作为入参（通过-分割符数量判断行政区域层级决定具体入参）查询****政区域获取对应AliasChinese和AliasEnglish出参||
|8|产生地|Originating Place|文本|根据文档id在fdc_doc_t表中获取origin_place_alpha2_code字段，作为入参（通过-分割符数量判断行政区域层级决定具体入参）查询****政区域获取对应AliasChinese和AliasEnglish出参||
|9|文档名称|Doc Name|文本|根据文档id在fdc_doc_t表中获取doc_name字段|单独展示一行|
|10|文档生成日期|Doc Generation Date|文本|根据文档id在fdc_doc_t表中获取doc_generation_date字段||
|11|归档责任人|Owner|文本|根据文档id在fdc_doc_t表中获取owner_id字段，作为外键外联【用户表】的user_id，带出user_name字段展示||
|12|文档责任部门|Responsible Dept.|文本|根据文档id在fdc_doc_t表中获取dept_code字段，作为入参查询****获取对应organizationName字段||
|13|载体类型|Carrier Type|文本|根据文档id在fdc_doc_t表中获取carrier_type字段，关联Lookup中的Carrier Type值||
|14|系统来源|Source System|文本|根据文档id在fdc_doc_t表中获取source_system字段||
|15|密级|Security Level|文本|根据文档id在fdc_doc_t表中获取business_module_id，作为外键关联fdc_business_module_t获取||
|16|描述|Description|文本|根据文档id在fdc_doc_t表中获取description字段|单独展示一行|
|17|文档归档编码||文本|根据文档id在fdc_doc_t表中获取||
|18|创建时间|Creation Time|文本|根据文档id在fdc_doc_t表中获取creation_date字段||
|19|创建人|Created By|文本|根据文档id在fdc_doc_t表中获取created_by字段，作为外键外联【用户表】的user_id，带出user_name字段展示||
|20|地区部|Region|文本|根据文档id在fdc_doc_t表中获取archived_entity_unit_id，作为外键关联fdc_archived_entity_unit_t获取archived_entity_id，作为外键关联fdc_archived_entity_t获取region_code，地区部名称根据region_code调用idata服务获取对应的名称||
|21|代表处|Rep Office|文本|根据文档id在fdc_doc_t表中获取archived_entity_unit_id，作为外键关联fdc_archived_entity_unit_t获取archived_entity_id，作为外键关联fdc_archived_entity_t获取rep_office_code，地区部名称根据rep_office_code 调用idata服务获取对应的名称||
|22|国家/地区|Country|文本|根据文档id在fdc_doc_t表中获取archived_entity_unit_id，作为外键关联fdc_archived_entity_unit_t获取archived_entity_id，作为外键关联fdc_archived_entity_t获取country_code，国家名称根据country_code 调用idata服务获取对应的名称||

**〔L4 归档信息〕— 模块 M3：归档信息**
|**序号**|**字段名称**|**字段名-英文**|**类型**|**字段逻辑说明**|**备注**|
|--|--|--|--|--|--|
|1|条码模块|Barcode Module|文本|||
|2|档案条码|Archive Barcode|文本|||
|3|文档编号|Volume Seq. No.|文本||册内序号|
|4|册号|Volume No.|文本|||
|5|册条码|Volume Barcode|文本|||
|6|文档组织|Doc Organization|文本|||
|7|库房|Repository|文本|||
|8|库位|Storage Location|文本|||
|9|份数|Copies|文本|||
|10|剩余份数|Remaining Copies|文本|||
|11|档案类型|Archive Type|文本|||
|12|是否可见|Visibility|文本|根据文档id在fdc_doc_t表中匹配归档流向，获取visible_flag||

**按钮：**
|**编号**|**按钮名称**|**按钮名称-英文**|**显示位置**|**按钮逻辑说明（含调用UI API）**|**绑定权限项名称**|**新增权限项说明**|
|--|--|--|--|--|--|--|
|1|下拉|/|归档信息栏标题-右上1|展示|||

**〔L4 扩展信息〕— 模块 M4：扩展信息（待定）**
|**序号**|**字段名称**|**字段名-英文**|**类型**|**字段逻辑说明**|**备注**|
|--|--|--|--|--|--|
|1|文号|File No.|文本|||
|2|发票号|Invoice No.|文本|||
|3|会计|Accountant|文本|||
|4|扫描员|Scanned By|文本|||
|5|其它归档号|Other Arch No.|文本|||
|6|项目名称|Project Name|文本|||
|7|客户|Customer|文本|||
|8|交易对手|Countparty|文本|||
|9|放款日期|Loan Date|文本|||
|10|移交人|Handed over|文本|||
|11|到期日|Expiry Date|文本|||
|12|银行名称|Bank Name|文本|||
|13|币种|Currency|文本|||
|14|金额|Amount|文本|||
|15|出票日|Issue Date|文本|||
|16|出票人账号|Drawer Account No.|文本|||
|17|收款行|Payee Bank|文本|||
|18|付款行|Paying Bank|文本|||
|19|出票人|Drawer|文本|||
|20|收款行账号|Payee Account No.|文本|||
|21|前手背书人|Prior Endorser|文本|||
|22|业务处理人|Handler|文本|||
|23|起息日||文本|||
|24|原存单号||文本|||
|25|存期||文本|||
|26|收益率||文本|||
|27|银行账户||文本|||
|28|细分类型||文本|||
|29|开立日期||文本|||
|30|开立银行名称||文本|||
|31|合同号|Contract No.|文本|||
|32|文件号||文本||是否保留 = 文号？|
|33|签发机构||文本|||
|34|每份页数|Pages per Copy|文本|||
|35|业务申请人||文本|||
|36|供应商||文本|||
|37|开立方式||文本|||
|38|保函编号||文本|||
|39|保函台账状态||文本|||
|40|保函失效日期||文本|||

**按钮：**
|**编号**|**按钮名称**|**按钮名称-英文**|**显示位置**|**按钮逻辑说明（含调用UI API）**|**绑定权限项名称**|**新增权限项说明**|
|--|--|--|--|--|--|--|
|1|下拉|/|扩展信息栏标题-右上1|展示|||

**〔L5 附件列表〕— 模块 M5：附件列表**
|**序号**|**字段名称**|**字段名-英文**|**类型**|**字段逻辑说明**|**备注**|
|--|--|--|--|--|--|
|1|文件名|File Name|文本|根据附件id在fdc_doc_att_t表中获取file_name字段|显示附件文件名和图标|
|2|附件类型|Attachment Type|文本|根据附件id在fdc_doc_att_t表中获取att_type字段|附件的业务类型|
|3|大小|File Size|文本|根据附件id在fdc_doc_att_t表中获取file_size字段|附件文件大小|
|4|上传时间|Upload Time|日期|根据附件id在fdc_doc_att_t表中获取upload_time字段|附件上传时间|
|5|补充信息|Additional Info|文本|根据附件id在fdc_doc_att_t表中获取additional_info字段|附件的唯一标识码|
|6|操作|Operations|按钮组||预览和下载功能|

**按钮：**
|**编号**|**按钮名称**|**按钮名称-英文**|**显示位置**|**按钮逻辑说明（含调用UI API）**|**绑定权限项名称**|**新增权限项说明**|
|--|--|--|--|--|--|--|
|1|批量下载|Batch Download|附件列表栏标题-右上1|点击批量下载，调用附件批量下载api，点击后弹出导出跳转提示弹窗|附件下载|如无权限，批量下载预览按钮不可见|
|2|下拉|/|附件列表栏标题-右上2|展示|||
|3|预览|图标|附件列表栏-操作列右1|点击预览：根据附件id获取 `EDM_ID`（来源：`fdc_doc_att_t.additional_info` 唯一标识码），调用“附件预览”接口获取外部预览入口（URL/重定向），并打开外部系统对应预览页面|附件预览|如无权限，预览按钮置灰|
|4|下载|图标|附件列表栏-操作列右2|点击下载，调用附件下载api，|附件下载|如无权限，下载按钮置灰|

**〔L6 操作日志〕— 模块 M6：操作日志列表**
|**序号**|**字段名称**|**字段名-英文**|**类型**|**字段逻辑说明**|**备注**|
|--|--|--|--|--|--|
|1|操作人|Operated By|文本|根据日志id在fdc_doc_op_log_t表中获取operated_by字段|操作执行人|
|2|操作类型|Operation Type|文本|根据日志id在fdc_doc_op_log_t表中获取operation_type字段|操作类型标识|
|3|操作内容|Operation Content|文本|根据日志id在fdc_doc_op_log_t表中获取op_content字段|具体操作描述|
|4|时间|Operation Time|日期时间|根据日志id在fdc_doc_op_log_t表中获取operation_time字段|操作执行时间|
|5|备注|Remarks|文本|根据日志id在fdc_doc_op_log_t表中获取remarks字段|操作补充说明|
|6|补充附件|Supporting Attachment|链接|根据日志id在fdc_doc_log_att_t表中获取log_att_name和edm_id字段|相关附件链接|

**按钮：**
|**编号**|**按钮名称**|**按钮名称-英文**|**显示位置**|**按钮逻辑说明（含调用UI API）**|**绑定权限项名称**|**新增权限项说明**|
|--|--|--|--|--|--|--|
|1|下拉|/|操作日志列表标题栏-右上1|展示|||
|2|补充附件下载|链接|操作日志列表补充附件列|点击补充附件名，调用补充附件下载api，点击后|||

### 页面API设计（前端交互类必选）（来自 old_spec）
| 序号 | 页面名称 | API 名称 | 使用位置 |
|---:|---|---|---|
| 1 | 文档详情 | 文档详情（整体） | 整体 |
| 2 | 文档详情 | 附件批量下载 | 附件列表栏-批量下载按钮 |
| 3 | 文档详情 | 单个附件预览 | 附件列表栏-单个预览按钮 |
| 4 | 文档详情 | 单个附件下载 | 附件列表栏-单个下载按钮 |
| 5 | 文档详情 | 补充附件下载 | 附件列表栏-补充附件链接 |

### 5.3 规则与策略（Rules）
- **R-01 附件预览/下载权限门控**  
  - **输入**：用户在文档详情页附件列表/操作日志触发预览、下载、批量下载动作。  
  - **处理**：若不具备对应权限，则按钮不可见或置灰。  
 - **输出**：具备权限时执行对应动作（预览打开外部预览入口/下载/导出跳转提示弹窗）。

- **R-02 外部系统预览入口与审计**  
  - **输入**：用户点击“预览”。  
  - **处理**：后端基于附件id解析 `EDM_ID`，拼接/下发外部系统预览入口（URL 或重定向）；前端据此打开外部预览页；全链路记录预览请求审计（操作者、附件ID、EDM_ID、时间、结果）。  
  - **输出**：在外部系统展示预览内容；本系统不再承担内部受控水印渲染。  

### 5.4 异常/边界/幂等（Edge cases）
- **E-01 文档无权限/不存在**：返回无权限/资源不存在提示，不加载详情模块数据。
- **E-02 附件不可用**：预览/下载失败给出错误提示；预览失败时不打开外部预览页，不产生文件。

### 5.5 与其他模块的交互（UI - API）
- 文档详情页：调用“文档详情（整体）”API加载基础/扩展/归档/附件/操作日志所需数据。
- 附件列表：调用“单个附件预览/单个附件下载/附件批量下载”API。
- 操作日志补充附件下载：点击补充附件名调用“补充附件下载”API。

## 6. 接口规范（API Specs）

> 遵循 `/.docs/05_API_Conventions.md`。附件与操作日志采用**扁平资源** `document-attachments`、`document-operation-logs`，避免 `/documents/{id}/attachments/...` 深层嵌套。`{id}` 为数值型主键。

| 资源路径 | 方法 | 用途 | 备注 |
|---|---|---|---|
| `FDC_URL/documents/{id}` | GET | 文档详情 | 可将附件列表、操作日志嵌套在同一响应中以减少请求次数 |
| `FDC_URL/document-attachments/page` | GET | 附件列表（简单筛选 + 分页） | Query：`filter.documentId`、`pageNumber`、`pageSize` |
| `FDC_URL/document-attachments/search-page` | POST | 附件列表（复杂筛选 + 分页） | 请求体：`DocumentAttachmentPaginationQuery`，含 `filter.documentId` |
| `FDC_URL/document-attachments/{id}/preview` | GET | 单个附件预览 | 返回外部系统预览入口（URL 或重定向），后端已完成 `EDM_ID` 解析与传递；前端据此打开 |
| `FDC_URL/document-attachments/{id}/download` | GET | 单个附件下载 | |
| `FDC_URL/document-attachments/export` | POST | 附件批量下载/打包导出 | 请求体：`documentId`、`attachmentIds` 等；可生成异步任务，与 `export-tasks` 对齐 |
| `FDC_URL/document-operation-logs/search-page` | POST | 操作日志列表 | 请求体含 `filter.documentId` |
| `FDC_URL/document-operation-log-attachments/{id}/download` | GET | 操作日志补充附件下载 | |

## 7. 验收标准（AC）

- **AC-01**：从列表进入详情后，标题区展示文档名称与状态标签；面包屑可返回文档查询。  
- **AC-02**：附件列表显示文件名/类型/大小/上传时间；无权限时预览/下载行为按权限受控。  
- **AC-03**：点击“预览”后打开外部系统对应附件预览入口；预览由外部系统渲染，本系统不再设计内部附件预览页。  
- **AC-04**：操作日志展示操作人/类型/内容/时间；存在补充附件时可按权限下载。  

