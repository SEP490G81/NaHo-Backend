INSERT INTO books (
    title,
    `description`,
    jlpt_level,
    cefr_level,
    order_index,
    cover_image_file_id,
    created_time,
    modified_time
)
VALUES
    (
        'Marugoto A1',
        'Giáo trình Marugoto trình độ A1 dành cho người mới bắt đầu học tiếng Nhật.',
        'N5',
        'A1',
        1.0,
        NULL,
        NOW(),
        NULL
    ),
    (
        'Marugoto A2',
        'Giáo trình Marugoto trình độ A2 dành cho người học ở trình độ sơ cấp.',
        'N4',
        'A2',
        2.0,
        NULL,
        NOW(),
        NULL
    ),
    (
        'Marugoto A2/B1',
        'Giáo trình Marugoto trình độ chuyển tiếp từ A2 lên B1.',
        'N3',
        'A2B1',
        3.0,
        NULL,
        NOW(),
        NULL
    ),
    (
        'Marugoto B1',
        'Giáo trình Marugoto trình độ B1 dành cho người học có khả năng giao tiếp độc lập.',
        'N3',
        'B1',
        4.0,
        NULL,
        NOW(),
        NULL
    ),
    (
        'Marugoto B2',
        'Giáo trình Marugoto trình độ B2 dành cho người học trung cao cấp.',
        'N2',
        'B2',
        5.0,
        NULL,
        NOW(),
        NULL
    ),
    (
        'Marugoto C1',
        'Giáo trình Marugoto trình độ C1 dành cho người học nâng cao.',
        'N1',
        'C1',
        6.0,
        NULL,
        NOW(),
        NULL
    ),
    (
        'Marugoto C2',
        'Giáo trình Marugoto trình độ C2 dành cho người học sử dụng tiếng Nhật thành thạo.',
        'N1',
        'C2',
        7.0,
        NULL,
        NOW(),
        NULL
    );