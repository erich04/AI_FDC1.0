# F01_04 条码生成规则

> 按 `/.docs/features/Template.md` 补充完整规格。

## 1. 功能目标

- 配置条码生成规则，统一文档条码生成与校验行为。

## 2. 关键数据对象

- 建议主表：`fdc_barcode_rule_t`
- 关键字段：`barcode_rule_id`、`rule_code`、`rule_expression`、`effective_date`、`enable_flag`

## 3. 关键规则

- 同一业务范围内规则编码唯一。
- 规则启用前必须通过样例校验。

## 4. 待补充章节

- 规则引擎细节、接口定义、异常处理、验收标准、测试用例。
