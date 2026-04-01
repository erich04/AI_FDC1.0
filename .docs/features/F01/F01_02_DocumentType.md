# F01_02 文档类型管理

> 按 `/.docs/features/Template.md` 补充完整规格。

## 1. 功能目标

- 维护文档类型主数据，支撑分类、归档、查询与权限策略。

## 2. 关键数据对象

- 建议主表：`fdc_document_type_t`
- 关键字段：`document_type_id`、`document_type_code`、`document_type_name`、`parent_type_id`、`enable_flag`

## 3. 关键规则

- 类型编码全局唯一。
- 支持父子层级时应避免循环依赖。

## 4. 待补充章节

- 页面与交互、接口定义、权限矩阵、验收标准、测试用例。
