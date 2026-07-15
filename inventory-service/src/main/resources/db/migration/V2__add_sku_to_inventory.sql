
-- Add SKU first as nullable because existing rows do not yet have values
ALTER TABLE inventory
    ADD COLUMN sku VARCHAR(100) NULL AFTER product_id;

-- Populate SKU for existing inventory records
-- Replace these sample values with the actual SKUs from Product Service if available
UPDATE inventory SET sku = 'SKU-001' WHERE product_id = 1;
UPDATE inventory SET sku = 'SKU-002' WHERE product_id = 2;
UPDATE inventory SET sku = 'SKU-003' WHERE product_id = 3;
UPDATE inventory SET sku = 'SKU-004' WHERE product_id = 4;
UPDATE inventory SET sku = 'SKU-005' WHERE product_id = 5;
UPDATE inventory SET sku = 'SKU-006' WHERE product_id = 6;
UPDATE inventory SET sku = 'SKU-007' WHERE product_id = 7;
UPDATE inventory SET sku = 'SKU-008' WHERE product_id = 8;
UPDATE inventory SET sku = 'SKU-009' WHERE product_id = 9;
UPDATE inventory SET sku = 'SKU-010' WHERE product_id = 10;

-- Make SKU mandatory after existing data has been populated
ALTER TABLE inventory
    MODIFY COLUMN sku VARCHAR(100) NOT NULL;

-- Replace the automatically generated unique constraint/index
-- with an explicitly named constraint
ALTER TABLE inventory
    DROP INDEX product_id,
    ADD CONSTRAINT uk_inventory_product_id UNIQUE (product_id);
