INSERT INTO chests (chest_type, description, min_point, max_point, created_time)
VALUES ('NONE', 'Nhận ngay 5 điểm mà không cần mở rương', 5, 5, NOW()),
       ('BRONZE', 'Mở rương đồng để nhận ngẫu nhiên từ 10 đến 20 điểm', 10, 20, NOW()),
       ('SLIVER', 'Mở rương bạc để nhận ngẫu nhiên từ 25 đến 50 điểm', 25, 50, NOW()),
       ('GOLD', 'Mở rương vàng để nhận ngẫu nhiên từ 60 đến 100 điểm', 60, 100, NOW());