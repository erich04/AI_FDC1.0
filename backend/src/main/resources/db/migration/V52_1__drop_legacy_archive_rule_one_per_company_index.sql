-- V7 在 md_archive_flow_rule 上创建了 uk_md_archive_flow_rule_cp_active（每公司仅一条启用规则）。
-- V8 改为业务联合唯一 uk_md_archive_flow_rule_business，但未删除该索引；表已更名为 fdc_archive_rule_t 后索引名仍保留。
-- 不删除则无法为同一子公司插入多条归档流向（如演示种子数据）。

DROP INDEX IF EXISTS uk_md_archive_flow_rule_cp_active;
