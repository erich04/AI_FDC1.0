-- 归档责任人自动带出：责任部门、工作国家（产生地）
ALTER TABLE public.tpl_user_t
    ADD COLUMN IF NOT EXISTS duty_department VARCHAR(200);

ALTER TABLE public.tpl_user_t
    ADD COLUMN IF NOT EXISTS work_country_code VARCHAR(32);
