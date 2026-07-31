# Hướng Dẫn Test API Postman & Kết Nối Frontend (Speaking AI 1-1 & Resume Session)

Tài liệu này cung cấp chi tiết các API Postman, định dạng Request, Response kỳ vọng và luồng test thực tế cho tính năng **Nói chuyện AI 1-1 với Persona** và **Tiếp tục phiên nói dở dang (Resume Session)**.

---

## 📌 1. Cấu hình Chung trên Postman

- **Base URL**: `http://localhost:8080` (hoặc domain môi trường Dev/Staging)
- **Header Mặc Định**:
  - `Authorization`: `Bearer {{JWT_TOKEN}}` *(Bắt buộc cho tất cả API)*
  - `Content-Type`: `application/json` *(Trừ API gửi Audio dùng `multipart/form-data`)*

---

## 🚀 2. Danh Sách Chi Tiết Các API

### API 1: Bắt đầu phiên nói chuyện AI 1-1
- **Method**: `POST`
- **URL**: `{{BASE_URL}}/api/v1/speaking/session/{personaId}`
- **Path Variable**: `personaId` = `1` (Long ID của Persona)
- **Query Parameters (Tuỳ chọn)**:
  - `formalityLevelOverride`: `TEINEIGO` / `SONKEIGO` / `KENJOOGO` / `FUTSUUGO`
  - `marugotoLevelOverride`: `A1` / `A2_1` / `A2_2` / `B1_1` / `B1_2`
- **Request Body**: *None (Để rỗng)*
- **Response Kỳ Vọng (200 OK)**:
```json
{
  "code": 200,
  "message": "Bắt đầu cuộc trò chuyện thành công",
  "data": {
    "sessionId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
    "audioBase64": "UklGRi...",
    "reply": "こんにちは！田中です。今日はどんなお話をしましょうか？",
    "replyTranslation": "Xin chào! Tôi là Tanaka. Hôm nay chúng ta sẽ nói về chuyện gì nào?",
    "grammarNote": "Lời chào hỏi thân mật cơ bản trong tiếng Nhật."
  }
}
```

---

### API 2: Kiểm tra phiên nói chuyện dở dang (Active Session)
- **Method**: `GET`
- **URL**: `{{BASE_URL}}/api/v1/speaking/session/active`
- **Query Parameters (Tuỳ chọn)**: `personaId` = `1`
- **Request Body**: *None*
- **Response Kỳ Vọng (200 OK - Khi có session IN_PROGRESS)**:
```json
{
  "code": 200,
  "message": "Lấy thông tin phiên nói chuyện dở dang thành công.",
  "data": {
    "id": 15,
    "sessionCode": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
    "personaId": 1,
    "topic": "Conversation with Tanaka",
    "marugotoLevel": "A1",
    "formalityLevel": "TEINEIGO",
    "totalTurns": 2,
    "startedAt": "2026-07-30T16:00:00Z",
    "messages": [
      {
        "turnIndex": 0,
        "senderType": "assistant",
        "content": "こんにちは！田中です。よろしくお願いします。",
        "correctedText": null,
        "correctionExplanation": null,
        "grammarNote": "Lời chào bắt đầu.",
        "hintForLearner": null
      },
      {
        "turnIndex": 1,
        "senderType": "user",
        "content": "田中さん、こんにちは！",
        "correctedText": null,
        "correctionExplanation": null,
        "grammarNote": null,
        "hintForLearner": null
      }
    ]
  }
}
```
*Lưu ý: Nếu không có session dở dang, API trả về Status `204 No Content`.*

---

### API 3: Khôi phục phiên nói dở (Resume Session)
- **Method**: `POST`
- **URL**: `{{BASE_URL}}/api/v1/speaking/session/{sessionCode}/resume`
- **Path Variable**: `sessionCode` = `a1b2c3d4-e5f6-7890-abcd-ef1234567890`
- **Request Body**: *None*
- **Response Kỳ Vọng (200 OK)**:
```json
{
  "code": 200,
  "message": "Khôi phục phiên nói chuyện thành công.",
  "data": {
    "sessionId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
    "audioBase64": "UklGRi...",
    "reply": "はい、元気ですよ！今日はどんな仕事をしましたか？",
    "replyTranslation": "Vâng, tôi khỏe! Hôm nay bạn đã làm công việc gì thế?",
    "grammarNote": "Dùng câu hỏi đuôi か trong tiếng Nhật."
  }
}
```

---

### API 4: Gửi tin nhắn văn bản (Text Message)
- **Method**: `POST`
- **URL**: `{{BASE_URL}}/api/v1/speaking/session/{sessionId}/message`
- **Path Variable**: `sessionId` = `a1b2c3d4-e5f6-7890-abcd-ef1234567890`
- **Request Body (JSON)**:
```json
{
  "transcript": "今日は日本語を勉強しました。"
}
```
- **Response Kỳ Vọng (200 OK)**:
```json
{
  "code": 200,
  "message": "Xử lý tin nhắn văn bản thành công.",
  "data": {
    "reply": "素晴らしいですね！何時間勉強しましたか？",
    "replyTranslation": "Tuyệt vời quá! Bạn đã học trong bao nhiêu tiếng vậy?",
    "grammarNote": "何時間 dùng để hỏi khoảng thời gian.",
    "correctedUserText": "今日は日本語を勉強しました。",
    "correctionExplanation": "Câu của bạn rất chính xác và tự nhiên!",
    "aiAudio": "UklGRi..."
  }
}
```

---

### API 5: Gửi tin nhắn âm thanh (Audio Message + Assessment)
- **Method**: `POST`
- **URL**: `{{BASE_URL}}/api/v1/speaking/session/{sessionId}/audio`
- **Header**: `Content-Type`: `multipart/form-data`
- **Path Variable**: `sessionId` = `a1b2c3d4-e5f6-7890-abcd-ef1234567890`
- **Form Data**:
  - `file`: *(Binary file audio .wav / .m4a / .webm)*
  - `reference-text`: `はい、2時間勉強しました。` *(String, Tuỳ chọn)*
- **Response Kỳ Vọng (200 OK)**:
```json
{
  "code": 200,
  "message": "Xử lý tin nhắn thoại thành công.",
  "data": {
    "userText": "はい、２時間勉強しました。",
    "reply": "よく頑張りましたね！明日も頑張りましょう。",
    "replyTranslation": "Bạn đã rất nỗ lực đấy! Ngày mai cũng cố gắng nhé.",
    "grammarNote": "よく頑張りました là lời khen ngợi động viên.",
    "correctedUserText": "はい、2時間勉強しました。",
    "correctionExplanation": "Chuẩn xác!",
    "aiAudio": "UklGRi...",
    "accuracyScore": 92.5,
    "fluencyScore": 88.0,
    "completenessScore": 100.0,
    "pronunciationScore": 90.2
  }
}
```

---

### API 6: Kết thúc phiên nói & Chấm điểm AI (End Session)
- **Method**: `POST`
- **URL**: `{{BASE_URL}}/api/v1/speaking/session/{sessionId}/end`
- **Path Variable**: `sessionId` = `a1b2c3d4-e5f6-7890-abcd-ef1234567890`
- **Request Body (JSON - Tuỳ chọn)**:
```json
{
  "topic": "Trò chuyện hàng ngày với Tanaka",
  "speechMetadata": "Total turns: 5",
  "asrConfidence": "0.95"
}
```
- **Response Kỳ Vọng (200 OK)**:
```json
{
  "code": 200,
  "message": "Kết thúc phiên nói thành công và đã chấm điểm.",
  "data": {
    "overallScore": 85,
    "jlptEstimate": "N4",
    "summary": "Bạn đã có phản xạ giao tiếp rất tốt với Tanaka-san, tự tin mở lời và phát âm rõ ràng.",
    "strengths": [
      "Sử dụng thể lịch sự (Teineigo) nhất quán và chuẩn xác.",
      "Phát âm các từ vựng thời gian và hoạt động tự nhiên."
    ],
    "weaknesses": [
      "Cần chú ý nối trợ từ を khi dùng động từ 勉強する."
    ],
    "improvedExpressions": [
      {
        "original": "はい、２時間勉強しました。",
        "improved": "はい、2時間ほど勉強いたしました。",
        "explanation": "Dùng ほど để chỉ khoảng thời gian và いたし để hạ mình khi đối thoại lịch sự."
      }
    ]
  }
}
```

---

### API 7: Lấy danh sách lịch sử các phiên nói (Session History)
- **Method**: `POST`
- **URL**: `{{BASE_URL}}/api/v1/speaking/session/history`
- **Request Body (JSON - Tuỳ chọn)**:
```json
{
  "personaId": 1,
  "search": "Tanaka",
  "page": 0,
  "size": 10,
  "sortColumn": "CREATED_TIME",
  "sortDirection": "DESC"
}
```
- **Response Kỳ Vọng (200 OK)**:
```json
{
  "code": 200,
  "message": "Lấy danh sách lịch sử hội thoại thành công.",
  "data": {
    "content": [
      {
        "id": 15,
        "sessionCode": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
        "topic": "Conversation with Tanaka",
        "personaId": 1,
        "marugotoLevel": "A1",
        "formalityLevel": "TEINEIGO",
        "overallScore": 85,
        "jlptEstimate": "N4",
        "totalTurns": 5,
        "durationSeconds": 120,
        "startedAt": "2026-07-30T16:00:00Z",
        "endedAt": "2026-07-30T16:02:00Z"
      }
    ],
    "meta": {
      "page": 0,
      "size": 10,
      "totalElements": 1,
      "totalPages": 1,
      "hasNext": false,
      "hasPrevious": false
    }
  }
}
```

---

### API 8: Xem chi tiết nhận xét AI của 1 phiên nói cũ
- **Method**: `GET`
- **URL**: `{{BASE_URL}}/api/v1/speaking/session/history/{sessionCode}`
- **Path Variable**: `sessionCode` = `a1b2c3d4-e5f6-7890-abcd-ef1234567890`
- **Response Kỳ Vọng (200 OK)**:
```json
{
  "code": 200,
  "message": "Lấy chi tiết lịch sử hội thoại thành công.",
  "data": {
    "id": 15,
    "sessionCode": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
    "personaId": 1,
    "topic": "Conversation with Tanaka",
    "overallScore": 85,
    "jlptEstimate": "N4",
    "summary": "Bạn đã có phản xạ giao tiếp rất tốt...",
    "strengths": [
      "Sử dụng thể lịch sự nhất quán..."
    ],
    "weaknesses": [
      "Cần chú ý nối trợ từ..."
    ],
    "improvedExpressions": [
      {
        "original": "はい、２時間勉強しました。",
        "improved": "はい、2時間ほど勉強いたしました。",
        "explanation": "Dùng ほど..."
      }
    ],
    "totalTurns": 5,
    "durationSeconds": 120,
    "startedAt": "2026-07-30T16:00:00Z",
    "endedAt": "2026-07-30T16:02:00Z"
  }
}
```

---

### API 9: Lấy danh sách gợi ý chủ đề (Suggested Topics)
- **Method**: `GET`
- **URL**: `{{BASE_URL}}/api/v1/speaking/topics`
- **Response Kỳ Vọng (200 OK)**:
```json
{
  "code": 200,
  "message": "Lấy danh sách gợi ý chủ đề thành công.",
  "data": [
    {
      "id": 1,
      "title": "Du lịch Nhật Bản",
      "description": "Hội thoại hỏi đường và mua vé tàu",
      "level": "A1"
    }
  ]
}
```

---

## 🔄 3. Kịch Bản Test Thực Tế Cho Tester / Frontend Dev

1. **Bước 1**: Gọi `API 1` để bắt đầu session -> Nhận được `sessionId`.
2. **Bước 2**: Gọi `API 4` (Text) hoặc `API 5` (Audio) 2-3 lượt hội thoại với AI.
3. **Bước 3**: Giả định người dùng thoát khỏi app hoặc refresh trang web.
4. **Bước 4**: Gọi `API 2` (`GET /session/active`) -> Kiểm tra hệ thống trả về thông tin session `IN_PROGRESS` và mảng `messages` các câu đã nói trước đó.
5. **Bước 5**: Gọi `API 3` (`POST /session/{sessionCode}/resume`) -> Kiểm tra hệ thống nạp lại audio/câu thoại gần nhất của AI để tiếp tục nói chuyện.
6. **Bước 6**: Người dùng hoàn thành cuộc nói chuyện -> Gọi `API 6` (`POST /session/{sessionId}/end`) -> Nhận bảng tổng kết chấm điểm AI.
7. **Bước 7**: Mở giao diện Lịch sử -> Gọi `API 7` (`history`) và `API 8` (`history/{sessionCode}`) để kiểm tra thông tin hiển thị chính xác.
