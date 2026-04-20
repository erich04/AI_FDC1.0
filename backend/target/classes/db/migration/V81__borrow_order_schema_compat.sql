ALTER TABLE IF EXISTS fdc_borrow_order_t
    ADD COLUMN IF NOT EXISTS approval_comment VARCHAR(500);

ALTER TABLE IF EXISTS fdc_borrow_order_t
    ADD COLUMN IF NOT EXISTS demand_analyst VARCHAR(100);
