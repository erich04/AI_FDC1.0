# F01_07 库位维护

> 按 `/.docs/features/Template.md` 补充完整规格。

## 1. 功能目标

- 维护库位主数据，支撑成册入库、定位检索、转库与盘点。

## 2. 关键数据对象

- 建议主表：`fdc_location_t`
- 关键字段：`location_id`、`location_code`、`location_name`、`warehouse_id`、`enable_flag`

## 3. 关键规则

- 同一库房内库位编码唯一。
- 已绑定档案的库位不允许删除，仅允许禁用。

## 4. 待补充章节

- 页面与交互、接口定义、权限矩阵、验收标准、测试用例。
