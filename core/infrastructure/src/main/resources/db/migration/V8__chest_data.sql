INSERT INTO chests (chest_type, description, min_point, max_point, created_time)
VALUES ('NONE', 'Nhận ngay 1 đến 10 điểm.', 1, 10, NOW()),
       ('BRONZE', 'Mở rương đồng để nhận ngẫu nhiên từ 15 đến 25 điểm', 15, 25, NOW()),
       ('SLIVER', 'Mở rương bạc để nhận ngẫu nhiên từ 30 đến 50 điểm', 30, 50, NOW()),
       ('GOLD', 'Mở rương vàng để nhận ngẫu nhiên từ 60 đến 100 điểm', 60, 100, NOW());