# Hướng Dẫn Test Postman Cho Luồng Reaction & Comment

Tài liệu này tổng hợp toàn bộ các Request và Response mẫu cho luồng **Comment (Bình luận)** và **Reaction (Cảm xúc)** trong dự án NaHo Backend. 

Hệ thống hỗ trợ 2 phương thức giao tiếp chính:
1. **REST API (HTTP)**: Sử dụng các HTTP method tiêu chuẩn (`GET`, `POST`).
2. **WebSocket / STOMP**: Sử dụng để gửi và nhận bình luận, cảm xúc realtime.

---

## 1. Cấu hình Môi trường (Postman Environment)

Tạo các biến môi trường (Environment Variables) trong Postman:

| Variable | Example Value | Description |
| :--- | :--- | :--- |
| `baseUrl` | `http://localhost:8080` | URL của Server Backend |
| `wsUrl` | `ws://localhost:8080/ws` | Endpoint WebSocket (STOMP) |
| `accessToken` | `eyJhbGciOi...` | JWT Access Token sau khi Đăng nhập |
| `speakingQuestionId` | `1` | ID của bài tập nói / câu hỏi |
| `commentId` | `10` | ID của bình luận |

---

## 2. Luồng Comment (Bình luận)

### 2.1. Lấy danh sách bình luận theo Câu hỏi (REST API)

* **Method**: `GET`
* **URL**: `{{baseUrl}}/api/v1/comments?speakingQuestionId={{speakingQuestionId}}`
* **Headers**:
  * `Authorization`: `Bearer {{accessToken}}` *(Tùy chọn: Nếu có token sẽ nhận biết thêm cảm xúc của user hiện tại `myReaction`)*

#### Request Header
```http
GET /api/v1/comments?speakingQuestionId=1 HTTP/1.1
Host: localhost:8080
Authorization: Bearer eyJhbGciOi...
```

#### Response Mẫu (`200 OK`)
```json
{
  "meta": {
    "traceId": "9b1deb4d-3b7d-4bad-9bdd-2b0d7b3dcb6d",
    "timestamp": "2026-08-02T23:50:00Z"
  },
  "message": "Lấy danh sách bình luận thành công",
  "data": {
    "speakingQuestionId": 1,
    "comments": [
      {
        "commentId": 10,
        "questionId": 1,
        "userId": 5,
        "parentId": null,
        "content": "Bài nói này khá phát âm chuẩn và diễn đạt tự nhiên!",
        "createdTime": "2026-08-02T10:15:30",
        "modifiedTime": "2026-08-02T10:15:30",
        "reactionSummary": {
          "total": 3,
          "counts": {
            "LIKE": 2,
            "LOVE": 1
          },
          "myReaction": "LIKE"
        },
        "children": [
          {
            "commentId": 11,
            "questionId": 1,
            "userId": 8,
            "parentId": 10,
            "content": "Cảm ơn bạn nhé!",
            "createdTime": "2026-08-02T10:20:00",
            "modifiedTime": "2026-08-02T10:20:00",
            "reactionSummary": {
              "total": 0,
              "counts": {},
              "myReaction": null
            },
            "children": []
          }
        ]
      }
    ]
  }
}
```

---

### 2.2. Tạo mới bình luận (WebSocket / STOMP)

Trong Postman, tạo một **WebSocket (STOMP)** Request:
* **URL**: `{{wsUrl}}`
* **Subscribe Topic**: `/topic/comments`
* **Send Destination (App Destination)**: `/app/comments/create`
* **Headers**: `Authorization`: `Bearer {{accessToken}}`

#### Request Payload Body (STOMP Frame Payload)
```json
{
  "speakingQuestionId": 1,
  "content": "Bài phát âm này nghe rất tự nhiên!",
  "parentId": null
}
```
*Lưu ý: Nếu trả lời cho một bình luận khác (reply comment), điền `parentId` là ID của bình luận cha.*

#### Response Broadcast Mẫu (STOMP Message trên `/topic/comments`)
```json
{
  "comment_id": 12,
  "user_id": 5,
  "content": "Bài phát âm này nghe rất tự nhiên!"
}
```

---

### 2.3. Cập nhật nội dung bình luận (WebSocket / STOMP)

* **Subscribe Topic**: `/topic/comments`
* **Send Destination**: `/app/comments/update`

#### Request Payload Body
```json
{
  "commentId": 12,
  "questionId": 1,
  "newContent": "Bài phát âm này nghe rất tự nhiên và chuẩn giọng bản ngữ!"
}
```

#### Response Broadcast Mẫu (STOMP Message trên `/topic/comments`)
```json
{
  "comment_id": 12,
  "user_id": 5,
  "content": "Bài phát âm này nghe rất tự nhiên và chuẩn giọng bản ngữ!"
}
```

---

### 2.4. Xóa bình luận (WebSocket / STOMP)

* **Subscribe Topic**: `/topic/comments`
* **Send Destination**: `/app/comments/delete`

#### Request Payload Body
```json
{
  "commentId": 12
}
```

#### Response Broadcast Mẫu (STOMP Message trên `/topic/comments`)
```json
{
  "comment_id": 12,
  "user_id": 5,
  "content": "Bình luận này đã bị xóa"
}
```

---

## 3. Luồng Reaction (Cảm xúc)

### Danh sách Enum Cảm xúc & Hành động

* **`reactionType`**: `LIKE`, `LOVE`, `HAHA`, `WOW`, `SAD`, `ANGRY`
* **`reactionAction`**: `ADDED`, `UPDATED`, `REMOVED`

---

### 3.1. Lấy chi tiết cảm xúc của 1 Bình luận (REST API)

* **Method**: `GET`
* **URL**: `{{baseUrl}}/api/v1/reactions?commentId={{commentId}}`

#### Request Header
```http
GET /api/v1/reactions?commentId=10 HTTP/1.1
Host: localhost:8080
```

#### Response Mẫu (`200 OK`)
```json
{
  "meta": {
    "traceId": "d8e7f6a5-1b2c-3d4e-5f6a-7b8c9d0e1f2a",
    "timestamp": "2026-08-02T23:51:00Z"
  },
  "message": "Lấy chi tiết cảm xúc thành công",
  "data": {
    "commentId": 10,
    "total": 3,
    "counts": {
      "LIKE": 2,
      "LOVE": 1
    },
    "users": {
      "LIKE": [
        {
          "userId": 5,
          "username": "john_doe"
        },
        {
          "userId": 6,
          "username": "alice_smith"
        }
      ],
      "LOVE": [
        {
          "userId": 8,
          "username": "bob_wilson"
        }
      ]
    }
  }
}
```

---

### 3.2. Thả / Thay đổi / Gỡ cảm xúc (REST API)

* **Method**: `POST`
* **URL**: `{{baseUrl}}/api/v1/reactions/toggle`
* **Headers**:
  * `Authorization`: `Bearer {{accessToken}}`
  * `Content-Type`: `application/json`

#### Scenario A: Thêm cảm xúc mới (`ADDED`)
```json
{
  "commentId": 10,
  "reactionType": "LIKE",
  "reactionAction": "ADDED"
}
```

#### Scenario B: Đổi loại cảm xúc (`UPDATED`)
```json
{
  "commentId": 10,
  "reactionType": "LOVE",
  "reactionAction": "UPDATED"
}
```

#### Scenario C: Gỡ bỏ cảm xúc (`REMOVED`)
```json
{
  "commentId": 10,
  "reactionType": "LOVE",
  "reactionAction": "REMOVED"
}
```

#### Response Mẫu (`200 OK`)
```json
{
  "meta": {
    "traceId": "1a2b3c4d-5e6f-7a8b-9c0d-1e2f3a4b5c6d",
    "timestamp": "2026-08-02T23:52:00Z"
  },
  "message": "Thao tác cảm xúc thành công",
  "data": {
    "reactionType": "LIKE",
    "username": "john_doe"
  }
}
```

*Lưu ý: Ngay sau khi gọi API REST `POST /api/v1/reactions/toggle` thành công, hệ thống tự động phát tin nhắn WebSocket realtime tới channel `/topic/reaction/10`.*

---

### 3.3. Thả cảm xúc Realtime qua WebSocket (STOMP)

Ngoài việc gọi REST API ở mục 3.2, client cũng có thể gửi action react trực tiếp qua WebSocket:

* **Subscribe Topic**: `/topic/reaction`
* **Send Destination**: `/app/reaction/toggle`
* **Headers**: `Authorization`: `Bearer {{accessToken}}`

#### Request Payload Body
```json
{
  "commentId": 10,
  "reactionType": "HAHA",
  "reactionAction": "ADDED"
}
```

#### Response Broadcast Mẫu (STOMP Message trên `/topic/reaction`)
```json
{
  "reactionType": "HAHA",
  "username": "john_doe"
}
```

---

## 4. Các trường hợp lỗi thường gặp (Error Responses)

### 4.1. Thiếu thông tin bắt buộc hoặc sai Validation (`400 Bad Request`)

Khi gửi thiếu `commentId` hoặc `reactionType` trong request POST Reaction:

```json
{
  "type": "about:blank",
  "title": "Bad Request",
  "status": 400,
  "detail": "Invalid request content.",
  "instance": "/api/v1/reactions/toggle",
  "invalidParams": [
    {
      "name": "reactionType",
      "reason": "reaction.type.blank"
    }
  ]
}
```

### 4.2. Chưa xác thực Token (`401 Unauthorized`)

Khi không truyền header `Authorization` hoặc token hết hạn:

```json
{
  "type": "about:blank",
  "title": "Unauthorized",
  "status": 401,
  "detail": "Full authentication is required to access this resource",
  "instance": "/api/v1/reactions/toggle"
}
```

---

## 5. Hướng dẫn Test nhanh trên Postman Workflow

1. **Đăng nhập**: Lấy `accessToken` từ API Login và set vào Postman Variable `accessToken`.
2. **Lấy bình luận**: Gọi `GET /api/v1/comments?speakingQuestionId=1` để xem cấu trúc danh sách comment và lấy `commentId`.
3. **Test WebSocket STOMP**:
   - Kết nối tới `ws://localhost:8080/ws` (hoặc cấu hình STOMP client trong Postman WebSocket mode).
   - Subscribe các channel `/topic/comments` và `/topic/reaction/{commentId}`.
   - Bắn message tới `/app/comments/create` để tạo comment và kiểm tra realtime broadcast nhận về.
4. **Test Reaction**:
   - Gọi `POST /api/v1/reactions/toggle` với body `{"commentId": 10, "reactionType": "LIKE", "reactionAction": "ADDED"}`.
   - Kiểm tra kết quả trả về REST API và kiểm tra tin nhắn realtime đẩy về client trên WebSocket.
