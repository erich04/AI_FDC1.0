# F01_01 归档主体管理

> 按 `/.docs/features/Template.md` 补充完整规格。

## 1. 功能目标

- 管理归档主体主数据，供归档流程与检索统一引用。

## 2. 关键数据对象

- 建议主表：`fdc_archive_entity_t`
- 关键字段：`archive_entity_id`、`archive_entity_code`、`archive_entity_name`、`enable_flag`

## 3. 关键规则

- 代码唯一，不可重复。
- 禁用主体不可用于新业务单据。

## 4. 待补充章节

- 页面与交互、接口定义、权限矩阵、验收标准、测试用例。
