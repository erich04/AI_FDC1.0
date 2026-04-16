-- 归档责任人自动带出：责任部门、工作国家（产生地）
-- 兼容两种表名：历史库可能仍为 user_t，新库可能已重命名为 tpl_user_t。
ALTER TABLE IF EXISTS public.tpl_user_t
    ADD COLUMN IF NOT EXISTS duty_department VARCHAR(200);
ALTER TABLE IF EXISTS public.user_t
    ADD COLUMN IF NOT EXISTS duty_department VARCHAR(200);

ALTER TABLE IF EXISTS public.tpl_user_t
    ADD COLUMN IF NOT EXISTS work_country_code VARCHAR(32);
ALTER TABLE IF EXISTS public.user_t
    ADD COLUMN IF NOT EXISTS work_country_code VARCHAR(32);
