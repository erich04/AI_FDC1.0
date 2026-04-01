# F01_06 库房维护

> 按 `/.docs/features/Template.md` 补充完整规格。

## 1. 功能目标

- 维护库房主数据，为文档入库、转库、盘点提供基础信息。

## 2. 关键数据对象

- 建议主表：`fdc_warehouse_t`
- 关键字段：`warehouse_id`、`warehouse_code`、`warehouse_name`、`warehouse_type`、`enable_flag`

## 3. 关键规则

- 库房编码唯一。
- 已被在用库位引用的库房不允许直接删除。

## 4. 待补充章节

- 页面与交互、接口定义、权限矩阵、验收标准、测试用例。
