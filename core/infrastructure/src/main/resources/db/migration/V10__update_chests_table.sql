ALTER TABLE chests ADD COLUMN chest_type VARCHAR(50) NULL AFTER modified_time;
ALTER TABLE chests ADD COLUMN min_point DOUBLE NULL AFTER `description`;
ALTER TABLE chests ADD COLUMN max_point DOUBLE NULL AFTER min_point;

UPDATE chests SET chest_type = 'BRONZE', min_point = 5.0, max_point = 15.0 WHERE title = 'Rương Nhỏ';
UPDATE chests SET chest_type = 'SLIVER', min_point = 20.0, max_point = 30.0 WHERE title = 'Rương Vàng';
UPDATE chests SET chest_type = 'GOLD', min_point = 40.0, max_point = 60.0 WHERE title = 'Rương Vàng Lớn';

UPDATE chests SET chest_type = 'BRONZE', min_point = 5.0, max_point = 15.0 WHERE chest_type IS NULL;

ALTER TABLE chests MODIFY COLUMN chest_type VARCHAR(50) NOT NULL;
ALTER TABLE chests MODIFY COLUMN min_point DOUBLE NOT NULL;
ALTER TABLE chests MODIFY COLUMN max_point DOUBLE NOT NULL;
ALTER TABLE chests DROP COLUMN title;
ALTER TABLE chests DROP COLUMN point;
