-- Create subscription_plans table
CREATE TABLE subscription_plans (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    description TEXT NULL,
    tier VARCHAR(20) NOT NULL,
    price_amount DECIMAL(19,4) NOT NULL,
    price_currency VARCHAR(3) NOT NULL,
    duration_days INT NOT NULL,
    monthly_assessment_limit INT NOT NULL,
    monthly_assessment_audio_seconds BIGINT NOT NULL,
    max_assessment_audio_seconds INT NOT NULL,
    monthly_conversation_seconds BIGINT NOT NULL,
    max_conversation_session_seconds INT NOT NULL,
    max_conversation_turns_per_session INT NOT NULL,
    full_curriculum_access BOOLEAN NOT NULL,
    progress_analytics_enabled BOOLEAN NOT NULL,
    sample_answer_enabled BOOLEAN NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_time datetime(6) NOT NULL,
    modified_time datetime(6) NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create payment_orders table
CREATE TABLE payment_orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_code VARCHAR(50) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    subscription_plan_id BIGINT NOT NULL,
    amount_amount DECIMAL(19,4) NOT NULL,
    amount_currency VARCHAR(3) NOT NULL,
    provider VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL,
    provider_transaction_id VARCHAR(100) NULL,
    created_time datetime(6) NOT NULL,
    expires_time datetime(6) NOT NULL,
    paid_time datetime(6) NULL,
    modified_time datetime(6) NULL,
    CONSTRAINT fk_po_subscription_plan FOREIGN KEY (subscription_plan_id) REFERENCES subscription_plans (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create payment_transactions table
CREATE TABLE payment_transactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    payment_order_id BIGINT NOT NULL,
    provider VARCHAR(20) NOT NULL,
    provider_transaction_id VARCHAR(100) NOT NULL,
    amount_amount DECIMAL(19,4) NOT NULL,
    amount_currency VARCHAR(3) NOT NULL,
    successful BOOLEAN NOT NULL,
    provider_transaction_time datetime(6) NULL,
    metadata TEXT NULL,
    created_time datetime(6) NOT NULL,
    modified_time datetime(6) NULL,
    CONSTRAINT fk_pt_payment_order FOREIGN KEY (payment_order_id) REFERENCES payment_orders (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create user_subscriptions table
CREATE TABLE user_subscriptions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    subscription_plan_id BIGINT NOT NULL,
    payment_order_id BIGINT NULL,
    status VARCHAR(20) NOT NULL,
    start_time datetime(6) NOT NULL,
    end_time datetime(6) NOT NULL,
    created_time datetime(6) NOT NULL,
    modified_time datetime(6) NULL,
    CONSTRAINT fk_us_subscription_plan FOREIGN KEY (subscription_plan_id) REFERENCES subscription_plans (id),
    CONSTRAINT fk_us_payment_order FOREIGN KEY (payment_order_id) REFERENCES payment_orders (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
