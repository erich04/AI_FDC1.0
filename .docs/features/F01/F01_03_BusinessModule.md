# F01_03 业务模块管理

> 按 `/.docs/features/Template.md` 补充完整规格。

## 1. 功能目标

- 维护业务模块主数据，作为分类、流程路由和权限配置基础。

## 2. 关键数据对象

- 建议主表：`fdc_business_module_t`
- 关键字段：`business_module_id`、`business_module_code`、`business_module_name`、`enable_flag`

## 3. 关键规则

- 模块编码唯一。
- 禁用模块不得被新规则引用。

## 4. 待补充章节

- 页面与交互、接口定义、权限矩阵、验收标准、测试用例。
