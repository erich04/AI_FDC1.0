# 总体原则

本数据模型设计规范参考总体规范，根据文档开发涉及的内容，结合jalor框架进行裁剪，供数据模型设计时使用。

## Lookup 快码字段

凡通过快码/字典存储的字段（表中常存 **编码值**），其**取值含义、有效与废弃**以 `/.docs/06_Lookup.md` 为唯一真相来源；本文档定义列名、类型、精度与约束。编写规格或生成代码时，解释或校验此类字段必须先对照 `06_Lookup.md` 对应 `LOOKUP_TYPE` 节。

# 数据模型设计规范

## 数据模型命名规范

命名规范："fdc_" + 用途说明 + "表名后缀"

命名示例：fdc_document_organization_t 文档组织表

## 数据模型后缀规范


| **分类** | **后缀**      | **描述**                                                                                                                             | **样例**                                                      |
| ------ | ----------- | ---------------------------------------------------------------------------------------------------------------------------------- | ----------------------------------------------------------- |
| IT配置表  | _T          | 用于存储IT功能实现相关的配置数据(如系统参数、菜单、页面、报表功能配置等)的物理实体表。                                                                                      | fdc_document_organization_t(文档组织表)                          |
| 结果表    | _T          | 用于存储业务交易、单据等处理结果数据的物理实体表，主要包括事务数据，主数据、基础数据及观测数据。                                                                                   | fdc_document_t(文档信息表)                                       |
| 过程表    | _T          | 用于存储业务处理过程数据的物理实体表，如业务交易过程，审批过程，变更过程等，主要为事务数据。                                                                                     | fdc_workflow_header_t(工作流表)                                 |
| 规则表    | _T          | 用于存储支撑业处理所需的参数、规则配置等数据的物理实体表，主要为规则数据。                                                                                              | fdc_archive_rule_t(归档流向表)                                   |
| 表视图    | _V          |                                                                                                                                    | fdc_document_organization_v(文档组织表视图)                        |
| 多语言表   | _ML_T       | 用于存储多语言信息的物理实体表，表名前缀同多语言原始表保持一致，多语言对应原始表表名+_ML_T（原表如果有‘_T’结尾，去掉原表‘_T’，如果按该规范超出了表名字段长度30个字节限制，可以对原始表名部分进行缩减。例：ABC_T对应的多语言表为ABC_ML_T | fdc_entity_translation_ml_t（实体多语言）                          |
| 扩展信息表  | _EXT_T      | 用于存储扩展信息的物理实体表                                                                                                                     | 如原表：fdc_document_t(文档信息表)，扩展表： fdc_document_ext_t (文档信息扩展表) |
| 接口表    | _TI         | 用于存储上游集成数据的物理实体表                                                                                                                   | fdc_receive_document_ti(待归档数据接口表)                           |
| 日志表    | _LOG_T      | 用于记录系统失败或其他事件的物理实体表。                                                                                                               | fdc_workflow_approve_log_t(工作流审批日志表)                        |
| 归档表    | _TH         | 用于存储归档业务数据的物理实体表。                                                                                                                  | fdc_document_th(文档历史数据归档表)                                  |
| 备份表    | _BAK[0-9]*$ | 用于存储为应付数据丢失或损坏等可能出现的意外情况的物理实体表。                                                                                                    | fdc_document_t_bak20261101(文档信息备份表)                         |
| 临时表    | _TMP        | 用来存储IT处理过程中所产生的数据的物理实体表。                                                                                                           | fdc_receive_document_tmp(待归档数据处理临时表)                        |


# 索引设计规范

## 索引设计原则


| 类型          | 设计方法                                                                                                      |
| ----------- | --------------------------------------------------------------------------------------------------------- |
| **索引字段选择**  | 1）频繁被使用在过滤条件的字段、关联查询的字段作为索引字段；                                                                            |
| **索引字段选择**  | 2）优先选择过滤性高的字段作为索引字段；                                                                                      |
| **索引字段选择**  | 3）避免使用频繁更新的列作为索引字段，影响DML性能；                                                                               |
| **索引字段选择**  | 4）取值范围固定的列不建索引、同样过滤条件下，保持索引长度最小；                                                                          |
| **唯一索引**    | 1）业务存在唯一标识的字段，建议设计唯一索引；                                                                                   |
| **唯一索引**    | 2）**不建议单表创建多个唯一索引**，同时维护多个唯一索引的开销远大于维护一个多列唯一索引，如果业务逻辑上多个唯一索引，与一个多列唯一索引等价，应使用多列唯一索引；                       |
| **全局&分区索引** | 1）查询包含分区键时，使用本地索引性能较好；                                                                                    |
| **全局&分区索引** | 2）跨分区时，使用全局索引性能较好，但是全局索引的维护代价更高。对于需要频繁增加/删除分区的分区表，不建议使用全局索引；                                              |
| **联合索引**    | 1）合理利用组合索引设计，避免冗余，例如已对(a,b,c)创建索引，原则上避免单独对 (a)、（b）、（c）、(a,b)、(b,c)创建索引。当查询时如果只带有a字段上的过滤条件，一般也会利用组合索引进行查询； |
| **联合索引**    | 2）设置合理的索引字段先后顺序，等值过滤条件、使用频度高、过滤性高的字段放置最左侧，作为先导列；                                                          |
| **联合索引**    | 3）**组合索引字段个数不超过5个，禁止总字符串长度超过200字节作为组合索引**；                                                                |


## 索引命名规范

1. 主键索引："pk_" + 原表名，系统自动生成，无需重新命名，例：pk_fdc_document_organization_t
2. 唯一索引："uk_" + 原表名，手工命名，例：uk_fdc_document_organization_t
3. 联合索引："idx_" + 原表名 + "n[0-9]"，手工命名，例：idx_fdc_document_organization_tn1

## 序列命名规范

数据模型主键xxx_id均使用自增序列，建表脚本需显性化定义序列的脚本。

序列命名："原表名_" + "id字段名" + "_seq"，例：fdc_warehouse_t_warehouse_id_seq

# 字段设计规范

## 字段命名规范

26个英文字母和0-9的自然数组成，命名简洁明确，多个字符用下划线’'分隔；不要在列的名称中包含数据类型；字段命名尽量使用完整名称，基本原则遵循表命名原则；

1. 【字段命名规则】{主题词 }_后缀，字段名长度不超过30个字符。
2. 【英文命名】基于属性名称转换成英文单词或国际通用英文简写；
3. 【禁用数据库保留字】不允许使用数据库系统的保留字作为字段名;
4. 【原则上业务字段须增加前缀表征含义】业务表中字段名涉及常见后缀，类似name、code、description通用字段，须增加业务表征前缀，如employee_name，避免直接使用name、code作为字段名；

## 字段常用后缀


| **大类**  | **类型**     | **常用后缀**         | **中文后缀** | **通用缩写** | **逻辑字段数据类型** | **逻辑字段数据类型**       | **逻辑字段数据类型** | **备注**                                                                                     |
| ------- | ---------- | ---------------- | -------- | -------- | ------------ | ------------------ | ------------ | ------------------------------------------------------------------------------------------ |
| **​ ​** | **​ ​**    | **​ ​**          | **​ ​**  | **​ ​**  | **逻辑数据类型**   | ​**默认长度**​**(精度)** | **标度**       | **​ ​**                                                                                    |
| 编号类     | 主键ID       | _ID              | XXX ID   |          | BIGINT       |                    |              | 适用范围：所有表的IT主键，原则上ID须定义为整型，除个别场景下，ID存在定义为Varchar类型，该场景下需重点关注评估是否确实为实际情况需要，例如类似IT类表，可能存在该场景； |
| 编号类     | 编号         | _NUMBER          | XXX号     | **NO**   | VARCHAR      | 60                 |              | 适用范围：交易数据的业务主键，一般按规则生成；不允许简写成NO                                                            |
| 编号类     | 编码         | _CODE            | XXX编码    |          | VARCHAR      | 60                 |              | 适用范围：主数据的业务主键，一般按规则人工定义。                                                                   |
| 代码类     | 类别(大类)     | _CATEGORY        | XXX类别    | **CAT**  | VARCHAR      | 30                 |              | 区分大的业务场景，用于区分不同的业务概念。                                                                      |
| 代码类     | 类型(小类)     | _TYPE            | XXX类型    |          | VARCHAR      | 30                 |              | 区分小的业务场景，用于区分不同的工作流。                                                                       |
| 代码类     | 状态         | _STATUS          | XXX状态    |          | VARCHAR      | 30                 |              | 与LOOKUP CODE保持一致                                                                           |
| 代码类     | 代码(基础数据类型) | _CODE            | XXX代码    |          | VARCHAR      | 30                 |              | 适用范围：基础数据类型的CODE（枚举值有限的场景），如币种代码                                                           |
| 代码类     | 型号         | _MODE            | XXX型号    |          | VARCHAR      | 30                 |              |                                                                                            |
| 代码类     | 层级         | _LEVEL           | XXX层级    |          | VARCHAR      | 30                 |              |                                                                                            |
| 代码类     | 方式         | _WAY             | XXX方式    |          | VARCHAR      | 30                 |              |                                                                                            |
| 描述类     | 名称         | _NAME            | XXX名称    |          | VARCHAR      | 100                |              |                                                                                            |
| 描述类     | 描述         | _DESCRIPTION     | XXX描述    | **DESC** | VARCHAR      | 500                |              |                                                                                            |
| 描述类     | 备注         | _REMARK          | XXX备注    | **RMK**  | VARCHAR      | 500                |              | 特定业务的备注，如包装备注。                                                                             |
| 描述类     | 顺序号        | _SEQUENCE_NUMBER | XXX序号    | **SN**   | INTEGER      |                    |              |                                                                                            |
| 数值类     | 数量         | _QUANTITY        | XXX数量    | **QTY**  | DECIMAL      | 24                 | 10           |                                                                                            |
| 数值类     | 金额         | _AMOUNT          | XXX金额    | **AMT**  | DECIMAL      | 38                 | 10           |                                                                                            |
| 数值类     | 单价         | _PRICE           | XXX单价    |          | DECIMAL      | 38                 | 18           |                                                                                            |
| 数值类     | 率          | _RATE            | XXX率     |          | DECIMAL      | 38                 | 18           |                                                                                            |
| 数值类     | 占比         | _PERCENTAGE      | XXX占比    | **PCT**  | DECIMAL      | 7                  | 6            |                                                                                            |
| 时间类     | 时间         | _TIME            | XXX时间    |          | DATETIME     |                    |              | 存储数据库时区的时间，不允许简写                                                                           |
| 时间类     | 日期         | _DATE            | XXX日期    |          | DATE         |                    |              | 存储数据库时区的时间，不允许简写                                                                           |
| 标识类     | 标识         | _FLAG            | XXX标识    |          | CHAR         | 1                  |              |                                                                                            |


## 字段长度规范


| 大类      | 小类     | 数据总长度  | 数据标度 | 逻辑字段类型及长度                          | 使用场景建议                               |
| ------- | ------ | ------ | ---- | ---------------------------------- | ------------------------------------ |
| **文本类** | 定长文本1  | 1      |      | 定长文本1，CHAR（1）                      | 用于标志类                                |
| **文本类** | 文本30   | 30     |      | 变长30个文字，VARCHAR、NVARCHAR、STRING等   | 代码类                                  |
| **文本类** | 文本60   | 60     |      | 变长60个文字，VARCHAR、NVARCHAR、STRING等   | 编号类                                  |
| **文本类** | 文本100  | 100    |      | 变长100个文字，VARCHAR、NVARCHAR、STRING等  | 编号类（编号类优先使用文本60）                     |
| **文本类** | 文本500  | 500    |      | 变长500个文字，VARCHAR、NVARCHAR、STRING等  | 描述类-描述，描述类-名称（名称优先使用文本100）           |
| **文本类** | 文本4000 | 4000   |      | 变长4000个文字，VARCHAR、NVARCHAR、STRING等 | 描述类-描述，谨慎使用                          |
| **文本类** | 超长文本   | NA     |      | CLOB，TEXT，STRING等                  | 业务属性禁止使用                             |
| **文本类** | 半结构化文本 | NA     |      | JSON，JSONB等                        | 业务属性禁止使用                             |
| **数字类** | 整数     | 4 byte |      | INT、INT4、NUMBER等                   | 编号类-序号，数量                            |
| **数字类** | 长整数    | 8 byte |      | BIGINT、INT8等                       | 编号类-IT主键                             |
| **数字类** | 浮点-金额  | 38     | 10   | DECIMAL(38，10)，NUMERIC(38，10)等     | 度量类-金额                               |
| **数字类** | 浮点-单价  | 38     | 18   | DECIMAL(38，18)，NUMERIC(38，18)等     | 度量类-单价，用于高精度要求的单价                    |
| **数字类** | 浮点-数量  | 24     | 10   | DECIMAL(24，10)，NUMERIC(24，10)等     | 度量类，长、宽、高，体积、重量等                     |
| **数字类** | 浮点-占比  | 7      | 6    | DECIMAL(7，6)，NUMERIC(7，6)等         | 度量类，指在总数中所占的比例                       |
| **数字类** | 浮点-对比  | 24     | 10   | DECIMAL(24，10)，NUMERIC(24，10)等     | 度量类，不同集合间的对比，如，同比、环比等；（高精度场景可用38，18） |
| **时间类** | 日期时间   |        |      | DATETIME                           | 时间类                                  |
| **时间类** | 日期     |        |      | DATE                               | 时间类，POSTSQL中用于涉及时区计算时，防止被自动转换        |


## 通用字段规范


| **应用场景**     | **字段中文名称** | **字段英文名称**                | **注释**                                                              | **逻辑数据类型**   | **物理数据类型**​**（OpenGauss）** |
| ------------ | ---------- | ------------------------- | ------------------------------------------------------------------- | ------------ | -------------------------- |
| 系统类字段标准      | 创建人        | CREATED_BY                | 创建人                                                                 | BIGINT       | INT8                       |
| 系统类字段标准      | 创建日期       | CREATION_DATE             | 创建日期                                                                | DATETIME     | TIMESTAMP                  |
| 系统类字段标准      | 最后修改人      | LAST_UPDATED_BY           | 最后修改人                                                               | BIGINT       | INT8                       |
| 系统类字段标准      | 最后修改日期     | LAST_UPDATE_DATE          | 最后修改日期                                                              | DATETIME     | TIMESTAMP                  |
| 系统类字段标准      | 最后修改版本     | LAST_UPDATE_VERSION       | 最后修改版本                                                              | INTEGER      | INT4                       |
| 系统类字段标准      | 本条记录说明     | SYS_DESCRIPTION           | 本条记录说明                                                              | VARCHAR(500) | NVARCHAR2(500)             |
| 标识类字段标准      | 有效标识       | ENABLE_FLAG               | 有效标识(Y/N)                                                           | CHAR(1)      | NVARCHAR2(1)               |
| 标识类字段标准      | 主要标识       | PRIMARY_FLAG              | 主要标识(Y/N)                                                           | CHAR(1)      | NVARCHAR2(1)               |
| 标识类字段标准      | 删除标识       | DELETE_FLAG               | 数据删除标识                                                              | CHAR(1)      | NVARCHAR2(1)               |
| 日期类字段标准      | 开始日期       | START_DATE                | 开始日期                                                                | DATE         | TIMESTAMP                  |
| 日期类字段标准      | 结束日期       | END_DATE                  | 结束日期                                                                | DATE         | TIMESTAMP                  |
| 日期类字段标准      | 生效日期       | EFFECTIVE_DATE            | 生效日期                                                                | DATE         | TIMESTAMP                  |
| 日期类字段标准      | 失效日期       | DISABLE_DATE              | 失效日期                                                                | DATE         | TIMESTAMP                  |
| 日期类字段标准      | 打开日期       | OPEN_DATE                 | 打开日期                                                                | DATE         | TIMESTAMP                  |
| 日期类字段标准      | 关闭日期       | CLOSE_DATE                | 关闭日期                                                                | DATE         | TIMESTAMP                  |
| 多语言表字段标准     | 语言编码       | language_code             | 语言编码                                                                | Varchar(30)  | NVARCHAR2(30)              |
| 多语言表字段标准     | 源语言编码      | source_language_code      | 源语言编码                                                               | Varchar(30)  | NVARCHAR2(30)              |
| 来源字段标准       | 来源系统编码     | SOURCE_SYSTEM_CODE        | 来源系统                                                                | VARCHAR(30)  | NVARCHAR2(30)              |
| 来源字段标准       | 来源对象类型     | SOURCE_OBJECT_TYPE        | 来源对象类型                                                              | VARCHAR(30)  | NVARCHAR2(30)              |
| 来源字段标准       | 来源对象ID     | SOURCE_OBJECT_ID          | 来源对象ID                                                              | VARCHAR(100) | NVARCHAR2(100)             |
| 来源字段标准       | 来源对象行ID    | SOURCE_OBJECT_LINE_ID     | 来源对象行ID                                                             | VARCHAR(100) | NVARCHAR2(100)             |
| 来源字段标准       | 来源对象行明细ID  | SOURCE_LINE_DETAIL_ID     | 来源对象行ID                                                             | VARCHAR(100) | NVARCHAR2(100)             |
| 来源字段标准       | 来源对象编号     | SOURCE_OBJECT_NUMBER      | 来源对象编号                                                              | VARCHAR(100) | NVARCHAR2(100)             |
| 来源字段标准       | 来源对象行号     | SOURCE_OBJECT_LINE_NUMBER | 来源对象行号                                                              | VARCHAR(100) | NVARCHAR2(100)             |
| 来源字段标准       | 来源对象明细号    | SOURCE_LINE_DETAIL_NUMBER | 来源对象行号                                                              | VARCHAR(100) | NVARCHAR2(100)             |
| 来源字段标准       | 来源更新时间     | SOURCE_LAST_UPDATE_TIME   | 来源更新时间                                                              | DATETIME     | TIMESTAMP                  |
| 元数据多租字段标准    | 租户ID       | TENANTID                  | 租户ID                                                                | BIGINT       | INT8                       |
| 附件表字段标准      | 附件信息ID     | ATTACHMENT_ID             | 附件信息ID                                                              | BIGINT       | int8                       |
| 附件表字段标准      | 附件名称       | ATTACHMENT_NAME           | 附件名称                                                                | varchar(100) | NVARCHAR2(100)             |
| 附件表字段标准      | 附件大小       | ATTACHMENT_SIZE           | 附件大小                                                                | varchar(100) | NVARCHAR2(100)             |
| 附件表字段标准      | 附件类型       | ATTACHMENT_TYPE           | 附件类型                                                                | varchar(30)  | NVARCHAR2(30)              |
| 附件表字段标准      | 附件描述       | ATTACHMENT_DESCRIPTION    | 附件描述                                                                | varchar(500) | NVARCHAR2(500)             |
| 附件表字段标准      | 资源附件ID     | LOCATION_ATTACHMENT_ID    | 资源附件ID                                                              | varchar(100) | NVARCHAR2(100)             |
| 附件表字段标准      | 资源URL      | LOCATION_URL              | 资源URL                                                               | varchar(100) | NVARCHAR2(100)             |
| 基础数据&主数据相关字段 | 适用范围       | applicable_scope          | 描述基础数据和主数据的使用范围，SYSTEM（系统预置，不可修改）、EXTENSION（系统预置，租户可扩展）、USER（租户自定义） | varchar(30)  | NVARCHAR2(30)              |

### 强制通用字段（所有数据模型表必须包含）

> 为保证跨模块治理、审计与租户隔离一致，任意新增/编辑的“数据模型表字典”都必须包含以下字段（与 `@01_DataModel.md (614-623)` 对齐；字段名大小写按表内定义口径）：

| 字段中文名称 | 字段英文名称 | 物理数据类型约束（OpenGauss） |
|---|---|---|
| 有效标识 | `enable_flag` | NVARCHAR2(1) |
| 删除标识 | `delete_flag` | NVARCHAR2(1) |
| 创建人 | `created_by` | INT8 |
| 创建日期 | `creation_date` | TIMESTAMP |
| 最后修改人 | `last_updated_by` | INT8 |
| 最后修改日期 | `last_update_date` | TIMESTAMP |
| 对本条记录的说明 | `sys_description` | NVARCHAR2(500) |
| 最后修改追踪ID | `last_update_trace_id` | NVARCHAR2(100) |
| 租户ID | `tenantid` | INT8 |

### 人员字段外键口径（统一到 `tpl_user_t.user_id`）

> 系统存在人员主数据表 `tpl_user_t`（记录 user_id、员工工号/姓名/在职状态等）。
> 除组织/部门等非人员维度外，所有“人员字段”（包含：`created_by`、`last_updated_by`、`operated_by`、`requested_by`、`owner_id` 等所有 `*_by`、以及明确表示“操作人/创建人/发起人/归档责任人”等的字段）**均必须存储 `tpl_user_t.user_id`**，并在字段说明中标注“外键（逻辑）到 `tpl_user_t.user_id`”。


---

## 5. F03 文档查询（核心数据模型草案）

> 说明：你目前没有 `fdc_doc_t / fdc_doc_att_t / fdc_doc_op_log_t / fdc_doc_log_att_t` 等现成表结构，本节提供一版**可实现、可扩展**的起草方案。  
> 原则：字段命名/后缀/索引命名遵循本文件第 2~4 章；与 `/.docs/04_Glossary.md` 术语保持一致。

### 5.1 ER（文字版）

- `fdc_doc_t`（文档） 1 --- N `fdc_doc_att_t`（附件）
- `fdc_doc_t`（文档） 1 --- N `fdc_doc_op_log_t`（操作日志）
- `fdc_doc_op_log_t`（操作日志） 1 --- N `fdc_doc_log_att_t`（日志补充附件）
- `fdc_doc_t`（文档） 1 --- 1 `fdc_arch_t`（档案，建议：一文档一档案；若一文档多档案则改 1---N）
- `fdc_arch_t`（档案） 1 --- 1 `fdc_arch_storage_t`（档案物理信息，建议：一档案一份物理信息；如多册/多存放点则改 1---N）
- `fdc_arch_t`（档案） N --- 1 `fdc_volume_t`（册）
- `fdc_doc_export_task_t`（导出任务） 1 --- N `fdc_doc_export_task_item_t`（导出明细，可选）

### 5.2 表字典（草案）

#### 5.2.1 fdc_doc_t（文档主表）

用途：支撑文档查询、文档详情展示的核心事实表（Document）。


| 字段中文名    | 字段名                       | 数据类型           | 变更类型 | 主键(PK) | 非空(NOT NULL) | 唯一(UNIQUE) | 外键(FK)                      | 自增(Auto Increment) | 默认值(Default Value) | 备注                                                            |
| -------- | ------------------------- | -------------- | ---- | ------ | ------------ | ---------- | --------------------------- | ------------------ | ------------------ | ------------------------------------------------------------- |
| ID主键     | doc_id                    | BIGINT         |      | Y      | Y            |            |                             | Y                  |                    | IT 主键（序列）                                                     |
| 租户隔离     | tenantid                  | BIGINT         |      |        | Y            |            |                             |                    |                    | `idx_fdc_doc_tn1(tenantid)`                                   |
| 文档业务编码   | doc_busi_no               | VARCHAR(60)    |      |        | Y            | Y          |                             |                    |                    | `uk_fdc_doc_t(tenantid, doc_busi_no)`；源系统业务ID                 |
| 文档名称     | doc_name                  | VARCHAR(500)   |      |        | Y            |            |                             |                    |                    | `idx_fdc_doc_tn2(tenantid, doc_name)`（可选）                     |
| 文档类型ID   | document_type_id          | BIGINT         |      |        | Y            |            | fdc_document_type_t         |                    |                    | `idx_fdc_doc_tn3(tenantid, document_type_id)`                 |
| 归档主体ID   | archived_entity_unit_id   | BIGINT         |      |        | Y            |            | fdc_archived_entity_unit_t  |                    |                    | `idx_fdc_doc_tn4(tenantid, archived_entity_unit_id)`          |
| 业务模块ID   | business_module_id        | BIGINT         |      |        | Y            |            | fdc_business_module_t       |                    |                    | `idx_fdc_doc_tn5(tenantid, business_module_id)`               |
| 归档责任人ID  | owner_id                  | BIGINT         |      |        | N            |            |                             |                    |                    | 用于列表/详情“归档责任人（Owner）”展示与筛选（对应 `tpl_user_t.user_id`，逻辑外键）                |
| 文档责任部门编码 | dept_code                 | VARCHAR(60)    |      |        | N            |            |                             |                    |                    | 用于列表/详情“文档责任部门（Responsible Dept.）”展示与筛选（部门服务入参）               |
| 开始档期     | start_period              | DATE           |      |        | N            |            |                             |                    |                    | 旧规格字段；`idx_fdc_doc_tn6(tenantid, start_period)`（可选）           |
| 结束档期     | end_period                | DATE           |      |        | N            |            |                             |                    |                    | 旧规格字段                                                         |
| 文档状态     | doc_status                | VARCHAR(30)    |      |        | Y            |            |                             |                    |                    | LOOKUP：FDC_DOC_STATUS；`idx_fdc_doc_tn7(tenantid, doc_status)` |
| 载体类型     | carrier_type              | VARCHAR(30)    |      |        | N            |            |                             |                    |                    | LOOKUP：FDC_CARRIER_TYPE                                       |
| 密级       | security_level            | VARCHAR(30)    |      |        | N            |            |                             |                    |                    | LOOKUP：安全等级；`idx_fdc_doc_tn8(tenantid, security_level)`（可选）   |
| 系统来源     | source_system             | VARCHAR(30)    |      |        | N            |            |                             |                    |                    | LOOKUP：FDC_SOURCE_SYS                                         |
| 文档生成日期   | doc_generation_date       | DATE           |      |        | N            |            |                             |                    |                    | 旧规格字段；`idx_fdc_doc_tn9(tenantid, doc_generation_date)`（可选）    |
| 归档地编码    | arch_place_alpha2_code    | VARCHAR(100)   |      |        | N            |            |                             |                    |                    | 行政区划/地理码                                                      |
| 产生地编码    | origin_place_alpha2_code  | VARCHAR(100)   |      |        | N            |            |                             |                    |                    | 行政区划/地理码                                                      |
| 文档组织编码   | doc_organization_code     | VARCHAR(30)    |      |        | N            |            | fdc_document_organization_t |                    |                    |                                                               |
| 条码模块     | barcode_module_code       | VARCHAR(30)    |      |        | N            |            |                             |                    |                    | 旧规格字段                                                         |
| 份数       | copies_quantity           | DECIMAL(24,10) |      |        | N            |            |                             |                    |                    | 数量类默认精度                                                       |
| 归档流向规则ID | archive_rule_id           | BIGINT         |      |        | N            |            | fdc_archive_rule_t          |                    |                    | 用于 visible_flag 的可追溯（可选）                                      |
| 是否可见     | visible_flag              | CHAR(1)        |      |        | Y            |            |                             |                    | 'Y'                | Y/N；`idx_fdc_doc_tn10(tenantid, visible_flag)`（可选）            |
| 描述/      | description               | VARCHAR(500)   |      |        | N            |            |                             |                    |                    | 旧规格字段                                                         |
| 有效标识     | enable_flag               | CHAR(1)        |      |        | Y            |            |                             |                    | 'Y'                | Y/N                                                           |
| 删除标识     | delete_flag               | CHAR(1)        |      |        | Y            |            |                             |                    | 'N'                | Y/N                                                           |
| 创建人      | created_by                | BIGINT         |      |        | Y            |            |                             |                    |                    | 外键（逻辑）到 `tpl_user_t.user_id`（创建人）                  |
| 创建日期     | creation_date             | TIMESTAMP      |      |        | Y            |            |                             |                    |                    | 系统字段；`idx_fdc_doc_tn11(tenantid, creation_date)`（可选）          |
| 乐观锁版本    | last_update_version       | INT4           |      |        | Y            |            |                             |                    | 0                  | 系统字段                                                          |


#### 5.2.2 fdc_doc_att_t（文档附件表）

用途：支撑文档详情的附件列表、预览、下载、批量下载。


| 字段中文名      | 字段名                 | 数据类型         | 变更类型 | 主键(PK) | 非空(NOT NULL) | 唯一(UNIQUE) | 外键(FK) | 自增(Auto Increment) | 默认值(Default Value) | 备注                                      |
| ---------- | ------------------- | ------------ | ---- | ------ | ------------ | ---------- | ------ | ------------------ | ------------------ | --------------------------------------- |
| ID主键       | doc_att_id          | BIGINT       |      | Y      | Y            |            |        | Y                  |                    | IT 主键                                   |
| 租户         | tenantid            | BIGINT       |      |        | Y            |            |        |                    |                    | `idx_fdc_doc_att_tn1(tenantid, doc_id)` |
| 关联文档       | doc_id              | BIGINT       |      |        | Y            |            | FK（逻辑） |                    |                    |                                         |
| 文件名        | file_name           | VARCHAR(500) |      |        | Y            |            |        |                    |                    |                                         |
| 附件类型       | att_type            | VARCHAR(30)  |      |        | N            |            |        |                    |                    |                                         |
| 文件大小（字节）   | file_size           | BIGINT       |      |        | N            |            |        |                    |                    |                                         |
| 上传时间       | upload_time         | TIMESTAMP    |      |        | N            |            |        |                    |                    |                                         |
| 补充信息/唯一标识码 | additional_info     | VARCHAR(500) |      |        | N            |            |        |                    |                    |                                         |
| 存储地址/对象Key | location_url        | VARCHAR(500) |      |        | Y            |            |        |                    |                    | （受控）                                    |
| 有效标识       | enable_flag         | CHAR(1)      |      |        | Y            |            |        |                    | 'Y'                |                                         |
| 删除标识       | delete_flag         | CHAR(1)      |      |        | Y            |            |        |                    | 'N'                |                                         |
| 创建人        | created_by          | BIGINT       |      |        | Y            |            |        |                    |                    | 外键（逻辑）到 `tpl_user_t.user_id`（创建人） |
| 创建时间       | creation_date       | TIMESTAMP    |      |        | Y            |            |        |                    |                    |                                         |
| 版本号        | last_update_version | INT4         |      |        | Y            |            |        |                    | 0                  |                                         |


#### 5.2.3 fdc_doc_op_log_t（文档操作日志表）

用途：支撑文档详情的操作日志展示与审计。


| 字段中文名 | 字段名                 | 数据类型         | 变更类型 | 主键(PK) | 非空(NOT NULL) | 唯一(UNIQUE) | 外键(FK) | 自增(Auto Increment) | 默认值(Default Value) | 备注                                                         |
| ----- | ------------------- | ------------ | ---- | ------ | ------------ | ---------- | ------ | ------------------ | ------------------ | ---------------------------------------------------------- |
| ID主键  | doc_op_log_id       | BIGINT       |      | Y      | Y            |            |        | Y                  |                    | IT 主键                                                      |
| 租户    | tenantid            | BIGINT       |      |        | Y            |            |        |                    |                    | `idx_fdc_doc_op_log_tn1(tenantid, doc_id, operation_time)` |
| 文档ID  | doc_id              | BIGINT       |      |        | Y            |            |        |                    |                    |                                                            |
| 操作人   | operated_by         | BIGINT       |      |        | Y            |            |        |                    |                    | 外键（逻辑）到 `tpl_user_t.user_id`（操作人）               |
| 操作类型  | operation_type      | VARCHAR(30)  |      |        | Y            |            |        |                    |                    | 枚举/LOOKUP                                                  |
| 操作内容  | op_content          | VARCHAR(500) |      |        | Y            |            |        |                    |                    |                                                            |
| 操作时间  | operation_time      | TIMESTAMP    |      |        | Y            |            |        |                    |                    |                                                            |
| 备注    | remarks             | VARCHAR(500) |      |        | N            |            |        |                    |                    |                                                            |
| 有效标识  | enable_flag         | CHAR(1)      |      |        | Y            |            |        |                    | 'Y'                |                                                            |
| 删除标识  | delete_flag         | CHAR(1)      |      |        | Y            |            |        |                    | 'N'                |                                                            |
| 创建人   | created_by          | BIGINT       |      |        | Y            |            |        |                    |                    | 外键（逻辑）到 `tpl_user_t.user_id`（创建人）               |
| 创建时间  | creation_date       | TIMESTAMP    |      |        | Y            |            |        |                    |                    |                                                            |
| 版本号   | last_update_version | INT4         |      |        | Y            |            |        |                    | 0                  |                                                            |


#### 5.2.4 fdc_doc_log_att_t（日志补充附件表）

用途：支撑“操作日志-补充附件”下载。


| 字段中文名      | 字段名                 | 数据类型         | 变更类型 | 主键(PK) | 非空(NOT NULL) | 唯一(UNIQUE) | 外键(FK) | 自增(Auto Increment) | 默认值(Default Value) | 备注                                                 |
| ---------- | ------------------- | ------------ | ---- | ------ | ------------ | ---------- | ------ | ------------------ | ------------------ | -------------------------------------------------- |
| ID主键       | doc_log_att_id      | BIGINT       |      | Y      | Y            |            |        | Y                  |                    | IT 主键                                              |
| 租户         | tenantid            | BIGINT       |      |        | Y            |            |        |                    |                    | `idx_fdc_doc_log_att_tn1(tenantid, doc_op_log_id)` |
| 关联日志       | doc_op_log_id       | BIGINT       |      |        | Y            |            |        |                    |                    |                                                    |
| 附件名        | log_att_name        | VARCHAR(500) |      |        | Y            |            |        |                    |                    |                                                    |
| 存储地址/对象Key | edm_id              | VARCHAR(500) |      |        | Y            |            |        |                    |                    |                                                    |
| 有效标识       | enable_flag         | CHAR(1)      |      |        | Y            |            |        |                    | 'Y'                |                                                    |
| 删除标识       | delete_flag         | CHAR(1)      |      |        | Y            |            |        |                    | 'N'                |                                                    |
| 创建人        | created_by          | BIGINT       |      |        | Y            |            |        |                    |                    | 外键（逻辑）到 `tpl_user_t.user_id`（创建人）                 |
| 创建时间       | creation_date       | TIMESTAMP    |      |        | Y            |            |        |                    |                    |                                                    |
| 版本号        | last_update_version | INT4         |      |        | Y            |            |        |                    | 0                  |                                                    |


#### 5.2.5 fdc_doc_export_task_t（导出任务表）

用途：支撑“批量导出”异步任务与“我的导出”列表。


| 字段中文名        | 字段名                 | 数据类型          | 变更类型 | 主键(PK) | 非空(NOT NULL) | 唯一(UNIQUE) | 外键(FK) | 自增(Auto Increment) | 默认值(Default Value) | 备注                                                                        |
| ------------ | ------------------- | ------------- | ---- | ------ | ------------ | ---------- | ------ | ------------------ | ------------------ | ------------------------------------------------------------------------- |
| ID主键         | export_task_id      | BIGINT        |      | Y      | Y            |            |        | Y                  |                    | IT 主键                                                                     |
| 租户           | tenantid            | BIGINT        |      |        | Y            |            |        |                    |                    | `idx_fdc_doc_export_task_tn1(tenantid, requested_by, request_time)`       |
| 业务任务号        | export_task_no      | VARCHAR(60)   |      |        | Y            | Y          |        |                    |                    | `uk_fdc_doc_export_task_t(tenantid, export_task_no)`                      |
| 发起人          | requested_by        | BIGINT        |      |        | Y            |            |        |                    |                    | 外键（逻辑）到 `tpl_user_t.user_id`（发起人）                    |
| 发起时间         | request_time        | TIMESTAMP     |      |        | Y            |            |        |                    |                    |                                                                           |
| 幂等键          | idempotency_key     | VARCHAR(100)  |      |        | N            | Y          |        |                    |                    | 用于 documents/export 幂等（建议 tenant 粒度唯一）                                    |
| 导出范围类型       | export_scope_type   | VARCHAR(10)   |      |        | N            |            |        |                    |                    | 未勾选=导出当前筛选结果；勾选=导出勾选集合（后端约定）                                              |
| 勾选数量         | selected_count      | BIGINT        |      |        | N            |            |        |                    |                    | 仅当导出范围为“勾选集合”时有值（可选）                                                      |
| 筛选条件摘要       | filter_snapshot     | VARCHAR(2000) |      |        | N            |            |        |                    |                    | 用于审计与排障（可选：筛选/勾选数量/导出类型摘要）                                                |
| 状态           | export_status       | VARCHAR(30)   |      |        | Y            |            |        |                    |                    | 状态（处理中/已完成/失败/已取消）；`idx_fdc_doc_export_task_tn2(tenantid, export_status)` |
| 导出内容摘要       | export_content      | VARCHAR(500)  |      |        | Y            |            |        |                    |                    |                                                                           |
| 结果文件名        | file_name           | VARCHAR(500)  |      |        | N            |            |        |                    |                    |                                                                           |
| 结果文件大小       | file_size           | BIGINT        |      |        | N            |            |        |                    |                    |                                                                           |
| 结果文件地址/对象Key | location_url        | VARCHAR(500)  |      |        | N            |            |        |                    |                    |                                                                           |
| 失败原因         | error_message       | VARCHAR(500)  |      |        | N            |            |        |                    |                    |                                                                           |
| 有效标识         | enable_flag         | CHAR(1)       |      |        | Y            |            |        |                    | 'Y'                |                                                                           |
| 删除标识         | delete_flag         | CHAR(1)       |      |        | Y            |            |        |                    | 'N'                |                                                                           |
| 创建人          | created_by          | BIGINT        |      |        | Y            |            |        |                    |                    | 外键（逻辑）到 `tpl_user_t.user_id`（创建人）                    |
| 创建时间         | creation_date       | TIMESTAMP     |      |        | Y            |            |        |                    |                    |                                                                           |
| 版本号          | last_update_version | INT4          |      |        | Y            |            |        |                    | 0                  |                                                                           |


#### 5.2.6 fdc_doc_export_task_item_t（导出明细，可选）

用途：当“按勾选集合导出”需要审计到文档粒度时使用；若仅存筛选条件 JSON，也可不建此表。


| 字段中文名  | 字段名                 | 数据类型      | 变更类型 | 主键(PK) | 非空(NOT NULL) | 唯一(UNIQUE) | 外键(FK) | 自增(Auto Increment) | 默认值(Default Value) | 备注                                                           |
| ------ | ------------------- | --------- | ---- | ------ | ------------ | ---------- | ------ | ------------------ | ------------------ | ------------------------------------------------------------ |
| ID主键   | export_task_item_id | BIGINT    |      | Y      | Y            |            |        | Y                  |                    | IT 主键                                                        |
| 租户     | tenantid            | BIGINT    |      |        | Y            |            |        |                    |                    | `idx_fdc_doc_export_task_item_tn1(tenantid, export_task_id)` |
| 导出任务ID | export_task_id      | BIGINT    |      |        | Y            |            |        |                    |                    |                                                              |
| 文档ID   | doc_id              | BIGINT    |      |        | Y            |            |        |                    |                    |                                                              |
| 创建人    | created_by          | BIGINT    |      |        | Y            |            |        |                    |                    | 外键（逻辑）到 `tpl_user_t.user_id`（创建人）               |
| 创建时间   | creation_date       | TIMESTAMP |      |        | Y            |            |        |                    |                    |                                                              |
| 版本号    | last_update_version | INT4      |      |        | Y            |            |        |                    | 0                  |                                                              |

#### 5.2.7 fdc_arch_t（档案表）

用途：沉淀档案主信息，记录档案类型、档案条码、剩余份数、核销人、核销时间等。

| 字段中文名 | 字段名 | 数据类型 | 变更类型 | 主键(PK) | 非空(NOT NULL) | 唯一(UNIQUE) | 外键(FK) | 自增(Auto Increment) | 默认值(Default Value) | 备注 |
|---|---|---|---|---|---|---|---|---|---|---|
| ID主键 | arch_id | BIGINT |  | Y | Y |  |  | Y |  | IT 主键 |
| 租户ID | tenantid | BIGINT |  |  | Y |  |  |  |  |  |
| 关联文档ID | doc_id | BIGINT |  |  | Y |  |  |  |  | 关联 `fdc_doc_t.doc_id`（逻辑外键） |
| 档案类型 | archive_type | VARCHAR(60) |  |  | N |  |  |  |  | 档案类型（可对齐 LOOKUP） |
| 档案条码 | archive_barcode | VARCHAR(60) |  |  | N | Y |  |  |  | 建议同租户唯一 |
| 剩余份数 | remaining_copies_quantity | DECIMAL(24,10) |  |  | N |  |  |  |  |  |
| 核销人 | write_off_by | BIGINT |  |  | N |  |  |  |  | 外键（逻辑）到 `tpl_user_t.user_id`（核销人） |
| 核销时间 | write_off_time | TIMESTAMP |  |  | N |  |  |  |  |  |
| 有效标识 | enable_flag | CHAR(1) |  |  | Y |  |  |  | 'Y' |  |
| 删除标识 | delete_flag | CHAR(1) |  |  | Y |  |  |  | 'N' |  |
| 创建人 | created_by | BIGINT |  |  | Y |  |  |  |  | 外键（逻辑）到 `tpl_user_t.user_id`（创建人） |
| 创建日期 | creation_date | TIMESTAMP |  |  | Y |  |  |  |  |  |
| 最后修改人 | last_updated_by | BIGINT |  |  | N |  |  |  |  | 外键（逻辑）到 `tpl_user_t.user_id`（最后修改人） |
| 最后修改日期 | last_update_date | TIMESTAMP |  |  | N |  |  |  |  |  |
| 对本条记录的说明 | sys_description | NVARCHAR2(500) |  |  | N |  |  |  |  |  |
| 最后修改追踪ID | last_update_trace_id | NVARCHAR2(100) |  |  | N |  |  |  |  |  |
| 最后修改版本 | last_update_version | INT4 |  |  | Y |  |  |  | 0 |  |

#### 5.2.8 fdc_arch_storage_t（档案物理信息表）

用途：沉淀档案物理信息，记录册ID、册内编号等。

| 字段中文名 | 字段名 | 数据类型 | 变更类型 | 主键(PK) | 非空(NOT NULL) | 唯一(UNIQUE) | 外键(FK) | 自增(Auto Increment) | 默认值(Default Value) | 备注 |
|---|---|---|---|---|---|---|---|---|---|---|
| ID主键 | arch_storage_id | BIGINT |  | Y | Y |  |  | Y |  | IT 主键 |
| 租户ID | tenantid | BIGINT |  |  | Y |  |  |  |  |  |
| 档案ID | arch_id | BIGINT |  |  | Y |  |  |  |  | 关联 `fdc_arch_t.arch_id`（逻辑外键） |
| 册ID | volume_id | BIGINT |  |  | Y |  |  |  |  | 关联 `fdc_volume_t.volume_id`（逻辑外键） |
| 册内编号 | volume_seq_no | VARCHAR(60) |  |  | N |  |  |  |  |  |
| 备注 | remark | NVARCHAR2(500) |  |  | N |  |  |  |  |  |
| 有效标识 | enable_flag | CHAR(1) |  |  | Y |  |  |  | 'Y' |  |
| 删除标识 | delete_flag | CHAR(1) |  |  | Y |  |  |  | 'N' |  |
| 创建人 | created_by | BIGINT |  |  | Y |  |  |  |  | 外键（逻辑）到 `tpl_user_t.user_id`（创建人） |
| 创建日期 | creation_date | TIMESTAMP |  |  | Y |  |  |  |  |  |
| 最后修改人 | last_updated_by | BIGINT |  |  | N |  |  |  |  | 外键（逻辑）到 `tpl_user_t.user_id`（最后修改人） |
| 最后修改日期 | last_update_date | TIMESTAMP |  |  | N |  |  |  |  |  |
| 对本条记录的说明 | sys_description | NVARCHAR2(500) |  |  | N |  |  |  |  |  |
| 最后修改追踪ID | last_update_trace_id | NVARCHAR2(100) |  |  | N |  |  |  |  |  |
| 最后修改版本 | last_update_version | INT4 |  |  | Y |  |  |  | 0 |  |

#### 5.2.9 fdc_volume_t（册信息表）

用途：记录册信息，包含册ID、册号、册状态、成册人、成册时间、库位ID等。

| 字段中文名 | 字段名 | 数据类型 | 变更类型 | 主键(PK) | 非空(NOT NULL) | 唯一(UNIQUE) | 外键(FK) | 自增(Auto Increment) | 默认值(Default Value) | 备注 |
|---|---|---|---|---|---|---|---|---|---|---|
| ID主键 | volume_id | BIGINT |  | Y | Y |  |  | Y |  | IT 主键 |
| 租户ID | tenantid | BIGINT |  |  | Y |  |  |  |  |  |
| 册号 | volume_no | VARCHAR(60) |  |  | Y | Y |  |  |  | 建议同租户唯一 |
| 册条码 | volume_barcode | VARCHAR(60) |  |  | N |  |  |  |  | 成册后生成，可按租户建立唯一约束（可选） |
| 册状态 | volume_status | VARCHAR(30) |  |  | N |  |  |  |  | 可对齐 LOOKUP |
| 成册人 | volume_compiler | BIGINT |  |  | N |  |  |  |  | 外键（逻辑）到 `tpl_user_t.user_id`（成册人） |
| 成册时间 | volume_compiled_time | TIMESTAMP |  |  | N |  |  |  |  |  |
| 库位ID | location_id | BIGINT |  |  | N |  | fdc_warehouse_location_t |  |  | 外键（逻辑）到 `fdc_warehouse_location_t.warehouse_location_id` |
| 备注 | remark | NVARCHAR2(500) |  |  | N |  |  |  |  |  |
| 有效标识 | enable_flag | CHAR(1) |  |  | Y |  |  |  | 'Y' |  |
| 删除标识 | delete_flag | CHAR(1) |  |  | Y |  |  |  | 'N' |  |
| 创建人 | created_by | BIGINT |  |  | Y |  |  |  |  | 外键（逻辑）到 `tpl_user_t.user_id`（创建人） |
| 创建日期 | creation_date | TIMESTAMP |  |  | Y |  |  |  |  |  |
| 最后修改人 | last_updated_by | BIGINT |  |  | N |  |  |  |  | 外键（逻辑）到 `tpl_user_t.user_id`（最后修改人） |
| 最后修改日期 | last_update_date | TIMESTAMP |  |  | N |  |  |  |  |  |
| 对本条记录的说明 | sys_description | NVARCHAR2(500) |  |  | N |  |  |  |  |  |
| 最后修改追踪ID | last_update_trace_id | NVARCHAR2(100) |  |  | N |  |  |  |  |  |
| 最后修改版本 | last_update_version | INT4 |  |  | Y |  |  |  | 0 |  |


---

### 5.3 扩展信息模型（推荐：配置驱动，避免主表无限扩列）

> 目标：将“文档扩展信息”（如文号/发票号/金额/银行等）从 `fdc_doc_t` 解耦出来，做到**按文档类型配置字段**、可校验、可脱敏、可演进。

#### 5.3.1 设计要点

- **字段配置驱动**：每种“文档类型”可配置不同的扩展字段集合、必填/校验/展示顺序/脱敏策略。
- **存储解耦**：扩展值不再加列到 `fdc_doc_t`，避免扩列带来的迁移/索引/性能与治理成本。
- **查询策略**：
  - 默认查询列表不依赖扩展字段（扩展字段只在详情或高级筛选时加载）。
  - 对“高频筛选”的扩展字段，可在配置中标记为“可索引”，由后端选择落 `fdc_doc_ext_t.ext_value_text` 的索引或使用表达式/GIN（若使用 JSONB）。
  - **F03 字段归属口径**：`fdc_doc_t` 承载文档主维度字段（如 `doc_busi_no/doc_name/doc_status/carrier_type/source_system/arch_place_alpha2_code/origin_place_alpha2_code/doc_organization_code/barcode_module_code/owner_id/dept_code/visible_flag`）；档案信息由 `fdc_arch_t` 承载（如 `archive_type/archive_barcode/remaining_copies_quantity`）；档案物理信息由 `fdc_arch_storage_t` 承载（如 `volume_id/volume_seq_no`）；册信息由 `fdc_volume_t` 承载（如 `volume_no/volume_barcode/volume_status/location_id`）。其余“文号/票据/金额/交易对手/业务类型/子公司名称”等可配置扩展字段通过 `fdc_doc_ext_t` 与 `fdc_doc_field_config_t` 承载，并通过 `searchable_flag/indexable_flag` 决定是否参与筛选与索引。

#### 5.3.2 fdc_doc_field_config_t（文档扩展字段配置表，IT配置表）

用途：定义“某文档类型有哪些扩展字段”及其约束。


| 字段中文名     | 字段名                 | 数据类型         | 变更类型 | 主键(PK) | 非空(NOT NULL) | 唯一(UNIQUE) | 外键(FK)              | 自增(Auto Increment) | 默认值(Default Value) | 备注                                                                               |
| --------- | ------------------- | ------------ | ---- | ------ | ------------ | ---------- | ------------------- | ------------------ | ------------------ | -------------------------------------------------------------------------------- |
| ID主键      | doc_field_config_id | BIGINT       |      | Y      | Y            |            |                     | Y                  |                    | IT 主键                                                                            |
| 租户        | tenantid            | BIGINT       |      |        | Y            |            |                     |                    |                    | `idx_fdc_doc_field_config_tn1(tenantid, document_type_id)`                       |
| 文档类型ID    | document_type_id    | BIGINT       |      |        | Y            |            | fdc_document_type_t |                    |                    | 与 fdc_document_type_t 关联（逻辑）                                                     |
| 字段编码      | field_code          | VARCHAR(60)  |      |        | Y            | Y          |                     |                    |                    | `uk_fdc_doc_field_config_t(tenantid, document_type_id, field_code)`；字段编码（机器用，稳定） |
| 字段名称      | field_name          | VARCHAR(500) |      |        | Y            |            |                     |                    |                    |                                                                                  |
| 字段类型      | field_type          | VARCHAR(30)  |      |        | Y            |            |                     |                    |                    | TEXT/NUMBER/DATE/LOOKUP/USER/ORG…                                                |
| 格式        | value_format        | VARCHAR(60)  |      |        | N            |            |                     |                    |                    | 格式（如日期格式/正则key）                                                                  |
| 是否必填      | required_flag       | CHAR(1)      |      |        | Y            |            |                     |                    | 'N'                | Y/N                                                                              |
| 是否可用于查询筛选 | searchable_flag     | CHAR(1)      |      |        | Y            |            |                     |                    | 'N'                | Y/N                                                                              |
| 是否建议建立索引  | indexable_flag      | CHAR(1)      |      |        | Y            |            |                     |                    | 'N'                | Y/N                                                                              |
| 是否脱敏展示    | masked_flag         | CHAR(1)      |      |        | Y            |            |                     |                    | 'N'                | Y/N（规则在安全文档）                                                                     |
| 展示顺序      | display_order       | INT4         |      |        | Y            |            |                     |                    | 0                  |                                                                                  |
| 有效标识      | enable_flag         | CHAR(1)      |      |        | Y            |            |                     |                    | 'Y'                |                                                                                  |
| 删除标识      | delete_flag         | CHAR(1)      |      |        | Y            |            |                     |                    | 'N'                |                                                                                  |
| 创建人       | created_by          | BIGINT       |      |        | Y            |            |                     |                    |                    |                                                                                  |
| 创建时间      | creation_date       | TIMESTAMP    |      |        | Y            |            |                     |                    |                    |                                                                                  |
| 版本号       | last_update_version | INT4         |      |        | Y            |            |                     |                    | 0                  |                                                                                  |


#### 5.3.3 fdc_doc_ext_t（文档扩展值表，扩展信息表）

用途：存储某个文档的扩展字段值（KV 或 JSON）。

**KV 推荐（便于按字段精确索引与治理）**：


| 字段中文名       | 字段名                 | 数据类型           | 变更类型 | 主键(PK) | 非空(NOT NULL) | 唯一(UNIQUE) | 外键(FK) | 自增(Auto Increment) | 默认值(Default Value) | 备注                                               |
| ----------- | ------------------- | -------------- | ---- | ------ | ------------ | ---------- | ------ | ------------------ | ------------------ | ------------------------------------------------ |
| ID主键        | doc_ext_id          | BIGINT         |      | Y      | Y            |            |        | Y                  |                    | IT 主键                                            |
| 租户          | tenantid            | BIGINT         |      |        | Y            |            |        |                    |                    | `idx_fdc_doc_ext_tn1(tenantid, doc_id)`          |
| 文档ID        | doc_id              | BIGINT         |      |        | Y            |            |        |                    |                    |                                                  |
| 扩展字段编码      | field_code          | VARCHAR(60)    |      |        | Y            | Y          |        |                    |                    | `uk_fdc_doc_ext_t(tenantid, doc_id, field_code)` |
| 文本值（通用）     | ext_value_text      | VARCHAR(500)   |      |        | N            |            |        |                    |                    |                                                  |
| 数值值（金额/占比等） | ext_value_number    | DECIMAL(38,10) |      |        | N            |            |        |                    |                    |                                                  |
| 日期值         | ext_value_date      | DATE           |      |        | N            |            |        |                    |                    |                                                  |
| 时间值         | ext_value_time      | TIMESTAMP      |      |        | N            |            |        |                    |                    |                                                  |
| 有效标识        | enable_flag         | CHAR(1)        |      |        | Y            |            |        |                    | 'Y'                |                                                  |
| 删除标识        | delete_flag         | CHAR(1)        |      |        | Y            |            |        |                    | 'N'                |                                                  |
| 创建人         | created_by          | BIGINT         |      |        | Y            |            |        |                    |                    |                                                  |
| 创建时间        | creation_date       | TIMESTAMP      |      |        | Y            |            |        |                    |                    |                                                  |
| 版本号         | last_update_version | INT4           |      |        | Y            |            |        |                    | 0                  |                                                  |


**JSONB 备选（更灵活，但索引/治理更难）**：可改为 `ext_json`（JSONB）一行存所有字段；高频筛选字段用表达式索引或 GIN。

#### 5.3.4 约束与一致性规则（必须）

- `fdc_doc_ext_t.field_code` 必须存在于 `fdc_doc_field_config_t` 的对应 `(tenantid, document_type_id)` 范围内。
- 变更 `field_code` 禁止破坏历史（建议仅新增字段；废弃字段用 `enable_flag='N'`，不直接删）。
- 涉及脱敏的字段（`masked_flag='Y'`）在查询与导出时必须遵循 `/.docs/03_Security.md` 的规则与审计要求。

---

## 6. F01 基础数据管理（主数据模型草案，字段清单）

> 说明：以下表字段来自 `F01` 功能规格中使用的表字段清单，用于补齐 `/.docs/01_DataModel.md` 的字段真相来源。  
> 后续可按需要进一步补全“索引/唯一约束/外键（物理约束）”等信息。



