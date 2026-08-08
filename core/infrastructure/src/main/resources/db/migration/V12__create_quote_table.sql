CREATE TABLE IF NOT EXISTS quote (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    kanji VARCHAR(500) NOT NULL,
    hiragana VARCHAR(500) NOT NULL,
    romaji VARCHAR(500) NOT NULL,
    translation TEXT NOT NULL,
    kanji_detail TEXT
);
