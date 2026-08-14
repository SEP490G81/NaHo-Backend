-- Migration V13: Create azure_daily_costs table for Azure Cost Management Caching
CREATE TABLE IF NOT EXISTS azure_daily_costs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_time DATETIME DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
    record_date DATE NOT NULL UNIQUE,
    cost_amount DECIMAL(18, 12) NOT NULL DEFAULT 0.000000000000,
    currency VARCHAR(10) NOT NULL DEFAULT 'USD',

    INDEX idx_record_date (record_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
