-- Alter leagues table to make max_point nullable
ALTER TABLE leagues
    MODIFY max_point DOUBLE NULL;

-- Insert SVG files for leagues into files table
INSERT INTO files (id, created_time, modified_time, object_key, original_name, content_type, size, comment_id,
                   report_id)
VALUES (1, NOW(), NULL, 'leagues/bronze.svg', 'bronze.svg', 'image/svg+xml', 974, NULL, NULL),
       (2, NOW(), NULL, 'leagues/silver.svg', 'silver.svg', 'image/svg+xml', 974, NULL, NULL),
       (3, NOW(), NULL, 'leagues/gold.svg', 'gold.svg', 'image/svg+xml', 970, NULL, NULL),
       (4, NOW(), NULL, 'leagues/sapphire.svg', 'sapphire.svg', 'image/svg+xml', 1268, NULL, NULL),
       (5, NOW(), NULL, 'leagues/ruby.svg', 'ruby.svg', 'image/svg+xml', 1260, NULL, NULL),
       (6, NOW(), NULL, 'leagues/emerald.svg', 'emerald.svg', 'image/svg+xml', 1266, NULL, NULL),
       (7, NOW(), NULL, 'leagues/amethyst.svg', 'amethyst.svg', 'image/svg+xml', 1442, NULL, NULL),
       (8, NOW(), NULL, 'leagues/pearl.svg', 'pearl.svg', 'image/svg+xml', 1436, NULL, NULL),
       (9, NOW(), NULL, 'leagues/obsidian.svg', 'obsidian.svg', 'image/svg+xml', 1442, NULL, NULL),
       (10, NOW(), NULL, 'leagues/diamond.svg', 'diamond.svg', 'image/svg+xml', 1164, NULL, NULL);

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
