-- Alter leagues table to make max_point nullable
ALTER TABLE leagues
    MODIFY max_point DOUBLE NULL;

-- Insert SVG files for leagues into files table
INSERT INTO files (id,
                   created_time,
                   object_key,
                   bucket_name,
                   original_name,
                   content_type,
                   size,
                   operation_type,
                   operation_status,
                   retry_count)
VALUES (1, NOW(), 'leagues/bronze.svg', 'naho-public-bucket', 'bronze.svg', 'image/svg+xml', 974, 'UPLOAD', 'COMPLETED', 0),
       (2, NOW(), 'leagues/silver.svg', 'naho-public-bucket', 'silver.svg', 'image/svg+xml', 974, 'UPLOAD', 'COMPLETED', 0),
       (3, NOW(), 'leagues/gold.svg', 'naho-public-bucket', 'gold.svg', 'image/svg+xml', 970, 'UPLOAD', 'COMPLETED', 0),
       (4, NOW(), 'leagues/sapphire.svg', 'naho-public-bucket', 'sapphire.svg', 'image/svg+xml', 1268, 'UPLOAD', 'COMPLETED', 0),
       (5, NOW(), 'leagues/ruby.svg', 'naho-public-bucket', 'ruby.svg', 'image/svg+xml', 1260, 'UPLOAD', 'COMPLETED', 0),
       (6, NOW(), 'leagues/emerald.svg', 'naho-public-bucket', 'emerald.svg', 'image/svg+xml', 1266, 'UPLOAD', 'COMPLETED', 0),
       (7, NOW(), 'leagues/amethyst.svg', 'naho-public-bucket', 'amethyst.svg', 'image/svg+xml', 1442, 'UPLOAD', 'COMPLETED', 0),
       (8, NOW(), 'leagues/pearl.svg', 'naho-public-bucket', 'pearl.svg', 'image/svg+xml', 1436, 'UPLOAD', 'COMPLETED', 0),
       (9, NOW(), 'leagues/obsidian.svg', 'naho-public-bucket', 'obsidian.svg', 'image/svg+xml', 1442, 'UPLOAD', 'COMPLETED', 0),
       (10, NOW(), 'leagues/diamond.svg', 'naho-public-bucket', 'diamond.svg', 'image/svg+xml', 1164, 'UPLOAD', 'COMPLETED', 0);

-- Insert league levels into leagues table
INSERT INTO leagues (id, created_time, modified_time, name, description, min_point, max_point, icon_file_id)
VALUES (1, NOW(), NULL, 'Bronze', 'Hạng Đồng - Bước khởi đầu trên hành trình chinh phục tri thức.', 0, 49, 1),
       (2, NOW(), NULL, 'Silver', 'Hạng Bạc - Nỗ lực không ngừng, khẳng định bản thân.', 50, 149, 2),
       (3, NOW(), NULL, 'Gold', 'Hạng Vàng - Đỉnh cao phong độ, sẵn sàng bứt phá.', 150, 299, 3),
       (4, NOW(), NULL, 'Sapphire', 'Hạng Lam Ngọc - Trí tuệ ngời sáng, tự tin tiến bước.', 300, 549, 4),
       (5, NOW(), NULL, 'Ruby', 'Hạng Hồng Ngọc - Đam mê rực cháy, chinh phục đỉnh cao.', 550, 899, 5),
       (6, NOW(), NULL, 'Emerald', 'Hạng Lục Bảo - Tài năng nở rộ, vươn tầm cao mới.', 900, 1399, 6),
       (7, NOW(), NULL, 'Amethyst', 'Hạng Thạch Anh Tím - Tinh hoa hội tụ, khẳng định đẳng cấp.', 1400, 2099, 7),
       (8, NOW(), NULL, 'Pearl', 'Hạng Ngọc Trai - Sự kiên trì tỏa sáng qua thời gian.', 2100, 2999, 8),
       (9, NOW(), NULL, 'Obsidian', 'Hạng Đá Hắc Diệu - Sức mạnh tiềm ẩn, vượt mọi giới hạn.', 3000, 4499, 9),
       (10, NOW(), NULL, 'Diamond', 'Hạng Kim Cương - Đỉnh cao tối thượng, tỏa sáng vĩnh cửu.', 4500, NULL, 10);
