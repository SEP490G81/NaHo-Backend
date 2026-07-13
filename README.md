# Hướng Dẫn Cài Đặt & Phát Triển Backend (naho-be)

Dự án Backend của ứng dụng **AI-Powered Web Application for Japanese Speaking Support** được xây dựng trên ngôn ngữ *
*Java 21**, framework **Spring Boot 4.0.6**, sử dụng công cụ quản lý dự án **Gradle (Kotlin DSL)** và cơ sở dữ liệu *
*MySQL**.

Dự án tuân theo kiến trúc **Clean Architecture / DDD (Domain-Driven Design)**, tách biệt rõ ràng giữa Business Logic và
Framework/Database giúp dự án dễ bảo trì và mở rộng.

---

## 1. Cấu Trúc Thư Mục & Thiết Kế Kiến Trúc

Dự án được chia thành cấu trúc multi-module gồm 5 tầng chính nằm dưới thư mục `core`:

```text
NaHo-Backend/
│
├── core/
│   ├── domain/            # Tầng nghiệp vụ lõi (Entities thuần Java, Value Objects, Domain Rules)
│   ├── application/       # Tầng ứng dụng (Use Cases, Services, DTOs, Ports/Interfaces)
│   ├── infrastructure/    # Tầng hạ tầng (JPA Entities, MyBatis Mappers, Flyway Migrations, DB Connections)
│   ├── presentation/      # Tầng giao tiếp (REST Controllers, API Request/Response, Validation, Swagger)
│   └── bootstrap/         # Tầng khởi chạy (Spring Boot EntryPoint, Application Configuration, Profiles)
│
├── gradlew & gradlew.bat  # Gradle Wrapper script để build dự án
├── build.gradle.kts       # Cấu hình Gradle chung cho toàn bộ dự án
└── settings.gradle.kts    # Khai báo các module của dự án
```

### Chi tiết các module:

* **`:core:domain`**: Độc lập hoàn toàn, không phụ thuộc vào bất kỳ thư viện hay cơ sở dữ liệu nào của Spring. Chứa các
  quy tắc nghiệp vụ cốt lõi, ví dụ như thực thể `User` hay `Role`.
* **`:core:application`**: Phụ thuộc vào `:core:domain`. Định nghĩa các Use Case và giao tiếp với tầng ngoài thông qua
  Interfaces (Ports).
* **`:core:infrastructure`**: Chứa cài đặt chi tiết cho các Interfaces ở tầng trên. Tầng này sử dụng cơ chế hybrid độc
  đáo: **Spring Data JPA** cho các tác vụ CRUD chuẩn và **MyBatis** cho các câu truy vấn hiệu năng cao hoặc phức tạp.
  Tầng này cũng chứa script cấu trúc DB **Flyway**.
* **`:core:presentation`**: Cung cấp các REST API. Định nghĩa Request, Response, kiểm tra dữ liệu đầu vào (Validation)
  và cấu hình tài liệu API bằng Swagger (OpenAPI 3).
* **`:core:bootstrap`**: Kết nối tất cả các tầng lại với nhau, chứa file main khởi chạy ứng dụng (
  `ApplicationBootstrap.java`) và cấu hình config (`application.yaml`).

---

## 2. Yêu Cầu Hệ Thống (Prerequisites)

Trước khi bắt đầu cài đặt, hãy đảm bảo máy tính của bạn đã được cài đặt các công cụ sau:

1. **JDK 21** (Khuyên dùng Eclipse Temurin hoặc Oracle JDK 21).
2. **MySQL Server** (Phiên bản 8.0 trở lên).
3. **IntelliJ IDEA** (Bản Ultimate hoặc Community đều được).
4. **Docker Desktop** (Tùy chọn - hữu ích nếu muốn chạy DB thông qua Docker hoặc chạy các integration test sử dụng
   Testcontainers).

---

## 3. Hướng Dẫn Setup IntelliJ IDEA

Để import dự án vào IntelliJ IDEA một cách chính xác nhất, bạn hãy làm theo các bước sau:

### Bước 3.1: Mở dự án

1. Mở IntelliJ IDEA.
2. Chọn **File** -> **Open...**
3. Tìm tới thư mục lưu dự án `NaHo-Backend` và chọn
   file [build.gradle.kts](file:///e:/Documents/Ky_9/SEP490/NaHo-Backend/build.gradle.kts).
4. Chọn **Open as Project**.

### Bước 3.2: Cấu hình JDK 21 cho dự án

1. Vào **File** -> **Project Structure...** (phím tắt `Ctrl + Alt + Shift + S`).
2. Ở mục **Project**, đảm bảo **SDK** được chọn là **JDK 21** (nếu chưa có, hãy ấn **Add SDK** -> **Download JDK...** và
   chọn bản Java 21).
3. Phần **Language level**, chọn **21 - Key status...** hoặc **SDK Default**.
4. Nhấn **Apply**.

### Bước 3.3: Cấu hình JDK cho Gradle

1. Vào **File** -> **Settings...** (phím tắt `Ctrl + Alt + S`).
2. Tìm tới **Build, Execution, Deployment** -> **Build Tools** -> **Gradle**.
3. Tại mục **Gradle JVM**, chọn đúng **JDK 21** mà bạn đã cấu hình ở bước trên.
4. Nhấn **Apply**.

### Bước 3.4: Bật Annotation Processing (Bắt buộc cho Lombok)

Dự án sử dụng thư viện Lombok ở tầng infrastructure và presentation để sinh code tự động (getter, setter, builder). Bạn
cần bật tính năng biên dịch Lombok trong IntelliJ:

1. Vẫn ở bảng **Settings** (`Ctrl + Alt + S`).
2. Tìm tới mục **Build, Execution, Deployment** -> **Compiler** -> **Annotation Processors**.
3. Tích chọn **Enable annotation processing**.
4. Nhấn **OK** để lưu toàn bộ cài đặt.

---

## 4. Hướng Dẫn Setup Database & Kết Nối

Dự án backend cấu hình kết nối database thông qua tệp cấu hình profile `dev` mặc định tại:

* [application-dev.yaml](file:///e:/Documents/Ky_9/SEP490/NaHo-Backend/core/bootstrap/src/main/resources/config/application-dev.yaml)

### Thông số kết nối mặc định:

* **Database URL**:
  `jdbc:mysql://localhost:3306/naho?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true&createDatabaseIfNotExist=true`
* **Username**: `root`
* **Password**: `1234`
* **Port**: `3306`
* **Tên Database**: `naho`

### Các bước thực hiện:

1. **Khởi động MySQL Server** trên máy cục bộ của bạn.
2. **Tạo database**: Bạn có thể dùng MySQL Workbench, DBeaver hoặc Command Line đăng nhập vào tài khoản `root` và tạo
   database tên là `naho`:
   ```sql
   CREATE DATABASE IF NOT EXISTS naho CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```
   *(Lưu ý: Connection string của dự án đã có option `createDatabaseIfNotExist=true`, tuy nhiên tự tạo thủ công giúp đảm
   bảo phân quyền chính xác).*
3. **Thay đổi thông tin đăng nhập (nếu cần)**:
   Nếu tài khoản MySQL cục bộ của bạn không phải là `root`/`1234`, hãy mở
   tệp [application-dev.yaml](file:///e:/Documents/Ky_9/SEP490/NaHo-Backend/core/bootstrap/src/main/resources/config/application-dev.yaml)
   và sửa lại thông số `username` và `password` cho khớp với máy của bạn.
4. **Database Migration (Flyway)**:
   Bạn **không cần** tự chạy bất cứ script SQL nào để tạo bảng. Dự án tích hợp **Flyway**.
   Khi bạn chạy ứng dụng lần đầu tiên, Flyway sẽ tự động quét thư
   mục [db/migration](file:///e:/Documents/Ky_9/SEP490/NaHo-Backend/core/infrastructure/src/main/resources/db/migration)
   và chạy:
    * `V1__tables.sql`: Khởi tạo cấu trúc các bảng (`users`, `roles`, `permissions`, `files`,...)
    * `V2__users_data.sql`: Tự động chèn (seed) dữ liệu ban đầu cho các vai trò gồm: `ADMIN`, `TEACHER`, `STUDENT`.

---

## 5. Hướng Dẫn Build & Chạy Ứng Dụng

### Cách 5.1: Build ứng dụng

Bạn có thể build ứng dụng để kiểm tra lỗi biên dịch thông qua Terminal bằng cách đứng từ thư mục gốc của dự án:

* **Trên Windows (PowerShell/CMD)**:
  ```powershell
  .\gradlew.bat build -x test
  ```
  *(Tham số `-x test` dùng để bỏ qua các bài test nếu bạn chưa muốn chạy chúng trong lúc build).*
* **Trên macOS/Linux**:
  ```bash
  ./gradlew build -x test
  ```

### Cách 5.2: Khởi chạy ứng dụng từ IntelliJ

1. Trong IntelliJ, mở rộng thư mục `core` -> `bootstrap` -> `src` -> `main` -> `java` -> `org` -> `naho`.
2. Nhấp đúp chuột để mở
   tệp [ApplicationBootstrap.java](file:///e:/Documents/Ky_9/SEP490/NaHo-Backend/core/bootstrap/src/main/java/org/naho/ApplicationBootstrap.java).
3. Nhấp chuột phải vào biểu tượng Run màu xanh lá bên cạnh tên class hoặc trong phương thức `main` và chọn **Run '
   ApplicationBootstrap.main()'**.
4. Spring Boot sẽ khởi động ở cổng `8386` (cấu hình
   trong [application.yaml](file:///e:/Documents/Ky_9/SEP490/NaHo-Backend/core/bootstrap/src/main/resources/application.yaml)).

### Cách 5.3: Chạy ứng dụng qua dòng lệnh Gradle

```powershell
.\gradlew.bat :core:bootstrap:bootRun
```

---

## 6. Kiểm Tra Sau Khi Khởi Chạy Thành Công

Khi ứng dụng chạy thành công, console log sẽ hiển thị dòng chữ báo Spring Boot đã sẵn sàng. Bạn có thể mở trình duyệt và
truy cập các đường dẫn sau để kiểm tra:

* **Giao diện tài liệu API (Swagger UI)**:
    * [http://localhost:8386/swagger-ui.html](http://localhost:8386/swagger-ui.html)
    * Tại đây bạn có thể kiểm tra danh sách các API và thử nghiệm gọi trực tiếp.
* **Đường dẫn API JSON (OpenAPI Spec)**:
    * [http://localhost:8386/v3/api-docs](http://localhost:8386/v3/api-docs)

---

## 7. Bắt đầu code tính năng mới như thế nào?

Khi muốn thêm một tính năng mới (ví dụ: Quản lý khóa học - Course):

1. **Domain Layer (`:core:domain`)**: Tạo thực thể nghiệp vụ `Course.java`, các enum liên quan, định nghĩa interface
   `CourseRepository` (không có annotation JPA).
2. **Application Layer (`:core:application`)**: Viết service xử lý nghiệp vụ (`CreateCourseUseCase.java`,
   `GetCourseUseCase.java`), định nghĩa DTO.
3. **Infrastructure Layer (`:core:infrastructure`)**:
    * Tạo thực thể lưu trữ database `CourseEntity.java` có các JPA annotations (`@Entity`, `@Table`).
    * Viết repository kế thừa `JpaRepository` để thao tác DB.
    * Implement interface `CourseRepository` từ tầng Domain bằng cách gọi JPA Repository này để lưu/đọc dữ liệu (và map
      giữa `Course` domain và `CourseEntity` infra).
    * (Nếu cần) Viết MyBatis Mapper cho các câu query phức tạp.
    * Tạo file migration Flyway mới dưới dạng SQL (ví dụ: `V3__courses.sql`) để tạo bảng.
4. **Presentation Layer (`:core:presentation`)**: Tạo `CourseController.java` để công khai endpoint REST API.

---

## 8. Hướng Dẫn Kiểm Thử API Đánh Giá Phát Âm (Speech Assessment API Test)

Hệ thống đã hỗ trợ endpoint API xử lý Speech To Text (STT) kết hợp đánh giá phát âm chi tiết (Pronunciation Assessment)
thông qua Azure Speech AI tại endpoint `POST /api/v1/speech/assess`.

### 8.1. Các bước kiểm thử

1. **Khởi chạy dự án:** Chạy ứng dụng Spring Boot từ IntelliJ hoặc bằng dòng lệnh:
   ```powershell
   .\gradlew.bat :core:bootstrap:bootRun
   ```
2. **Truy cập Swagger UI:** Xem và thử nghiệm trực tiếp
   tại [http://localhost:8386/swagger-ui.html](http://localhost:8386/swagger-ui.html) (Tag **Speech AI**).
3. **Chuẩn bị file âm thanh:** Sử dụng file ghi âm tiếng Nhật định dạng `.wav` (tần số khuyên dùng 16kHz, mono) để thử
   nghiệm.

### 8.2. Gọi API kiểm thử bằng lệnh cURL

#### Trường hợp 1: Đánh giá Tự do (Unscripted / Free-talk)

Không cần truyền script mẫu, hệ thống sẽ tự động chuyển giọng nói thành văn bản và đánh giá điểm phát âm:

```bash
curl -X POST "http://localhost:8386/api/v1/speech/assess" \
     -H "accept: application/json" \
     -H "Content-Type: multipart/form-data" \
     -F "file=@/path/to/japanese_test.wav"
```

#### Trường hợp 2: Đọc theo mẫu (Scripted Assessment)

Đánh giá độ chuẩn xác phát âm bằng cách so khớp giọng nói với văn bản mẫu truyền vào:

```bash
curl -X POST "http://localhost:8386/api/v1/speech/assess" \
     -H "accept: application/json" \
     -H "Content-Type: multipart/form-data" \
     -F "file=@/path/to/japanese_test.wav" \
     -F "referenceText=こんにちは"
```

### 8.3. Gọi API kiểm thử bằng HTTP Client (IntelliJ IDEA)

IntelliJ IDEA tích hợp sẵn công cụ test API rất thuận tiện (không cần cài thêm phần mềm ngoài):

1. **Khởi tạo file:** Click chuột phải vào bất kỳ thư mục nào trong project, chọn **New** -> **HTTP Request** (hoặc tạo
   file bất kỳ đặt tên là `test.http`).
2. **Cấu hình Request:** Sao chép đoạn mã dưới đây và dán vào tệp tin đó (hãy nhớ điều chỉnh đường dẫn tệp `.wav` khớp
   với file thật trên máy bạn):
    ```http
    POST http://localhost:8386/api/v1/speech/assess
    Accept: application/json
    Content-Type: multipart/form-data; boundary=boundary

    --boundary
    Content-Disposition: form-data; name="file"; filename="japanese_test.wav"
    Content-Type: audio/wav

    < C:\path\to\your\japanese_test.wav
    --boundary--
    ```
3. **Thực thi:** Bạn sẽ thấy một nút Play màu xanh lá cây (▶️) xuất hiện ở lề bên trái (ngang hàng dòng `POST`). Nhấn
   nút Play này để chạy test. Kết quả phản hồi từ API sẽ hiển thị trực tiếp ở cửa sổ kết quả phía dưới.

### 8.4. Kết quả phản hồi thực tế (Response JSON)

Response trả về thành công đã được bọc trong lớp `ApiResponse` chuẩn của dự án:

```json
{
  "meta": {
    "traceId": "124eea2c-8abc-4160-84ae-79d40cedf50e",
    "timestamp": "2026-05-23T13:57:26.101860Z",
    "pageMeta": null
  },
  "message": "Speech assessment completed successfully",
  "data": {
    "transcript": "皆さんこんにちは.私はこうあです.FBT大学 của 四年生です.どうぞよろしくお願いします。",
    "accuracyScore": 88.0,
    "fluencyScore": 85.0,
    "completenessScore": 100.0,
    "pronunciationScore": 85.6,
    "words": [
      {
        "word": "皆さん",
        "accuracyScore": 91.0,
        "errorType": "None"
      },
      {
        "word": "こんにちは",
        "accuracyScore": 91.0,
        "errorType": "None"
      },
      {
        "word": "私",
        "accuracyScore": 94.0,
        "errorType": "None"
      },
      {
        "word": "は",
        "accuracyScore": 97.0,
        "errorType": "None"
      },
      {
        "word": "こう",
        "accuracyScore": 97.0,
        "errorType": "None"
      },
      {
        "word": "あ",
        "accuracyScore": 100.0,
        "errorType": "None"
      },
      {
        "word": "です",
        "accuracyScore": 79.0,
        "errorType": "None"
      },
      {
        "word": "f",
        "accuracyScore": 97.0,
        "errorType": "None"
      },
      {
        "word": "b",
        "accuracyScore": 97.0,
        "errorType": "None"
      },
      {
        "word": "t",
        "accuracyScore": 97.0,
        "errorType": "None"
      },
      {
        "word": "大学",
        "accuracyScore": 60.0,
        "errorType": "None"
      },
      {
        "word": "の",
        "accuracyScore": 100.0,
        "errorType": "None"
      },
      {
        "word": "四年生",
        "accuracyScore": 91.0,
        "errorType": "None"
      },
      {
        "word": "es",
        "accuracyScore": 43.0,
        "errorType": "Mispronunciation"
      },
      {
        "word": "どうぞ",
        "accuracyScore": 91.0,
        "errorType": "None"
      },
      {
        "word": "よろしく",
        "accuracyScore": 76.0,
        "errorType": "None"
      },
      {
        "word": "お願い",
        "accuracyScore": 91.0,
        "errorType": "None"
      },
      {
        "word": "します",
        "accuracyScore": 94.0,
        "errorType": "None"
      }
    ]
  }
}
```
