# NaHo-Backend-AI Coding Guidelines (SKILLS)

Tài liệu này là **"Kim chỉ nam"** bắt buộc phải tuân thủ cho tất cả các tính năng mới trong dự án `NaHo-Backend-AI`. Dự án áp dụng **Clean Architecture** với sự phân chia ranh giới module cực kỳ nghiêm ngặt.

---

## 1. Kiến trúc Tổng thể (Multi-Module)
Dự án bao gồm 5 modules theo đúng thứ tự phụ thuộc (từ trong ra ngoài):
1. **`core/domain`**: Lõi nghiệp vụ. Không phụ thuộc vào bất kỳ module nào khác.
2. **`core/application`**: Logic ứng dụng (Use Cases). Chỉ phụ thuộc vào `domain`.
3. **`core/infrastructure`**: Tương tác với CSDL, API bên ngoài. Phụ thuộc vào `domain` và `application`.
4. **`core/presentation`**: Tầng giao tiếp (REST API). Phụ thuộc vào `domain` và `application`.
5. **`core/bootstrap`**: Nơi khởi chạy ứng dụng (Spring Boot Application) và cấu hình (Bean wiring). Phụ thuộc vào tất cả các module trên.

> **Quy tắc tuyệt đối:** Dependency chỉ được chĩa từ ngoài vào trong. Domain là trung tâm, không được biết về DB hay Web.

---

## 2. Quy tắc Đặt tên (Naming Conventions)
- **Tên Class (PascalCase):** `UpdateUserUseCase`, `UserController`.
- **Tên Biến / Hàm (camelCase):** `userId`, `getUserById()`.
- **Tên Hằng số (UPPER_SNAKE_CASE):** `public static final String USER_ID_NOT_FOUND = "user.id.not.found";`
- **Tên Package (lowercase):** Không viết hoa, không dùng dấu gạch dưới (VD: `org.naho.user.usecase`).

---

## 3. Tầng Domain (`core/domain`)
- **Quy tắc:** Là tầng thuần Java (Plain Old Java Objects). Tuyệt đối **không** dùng các framework ngoại vi như Spring, JPA (`@Entity`, `@Table`). Hạn chế tối đa dùng Lombok trên các Core Domain Models (nên tự viết Builder Pattern thủ công để đảm bảo tính bất biến - Immutable).
- **Thành phần chính:**
  - `model`: Các Entity cốt lõi (VD: `User.java` với các private final fields và static Builder).
  - `valueobject`: Đối tượng định danh (VD: `Email.java`, `Username.java`).
  - `type`: Enum định nghĩa các trạng thái (VD: `UserStatus`).

---

## 4. Tầng Application (`core/application`)
Là nơi điều phối Use Cases.
- **Ports (Interfaces):**
  - `port/in`: Giao diện nhận Request từ Controller (VD: `UpdateUserInputPort`).
  - **Port (In/Out):** Nơi định nghĩa interface (VD: `UserRepositoryPort`, `CreateUserUseCasePort`).
  - **Adapter:** 
    - Controller được tính là Input Adapter.
    - Output Adapter (ví dụ: JPA Repository) phải implement các `Port Out` từ tầng Application.
    - **BẮT BUỘC (Quy tắc đặt tên Adapter):** Đặt tên kết thúc bằng `Adapter`. Cách gọi tên thường dựa theo tên của Port mà nó implement (bỏ chữ `Port`). Ví dụ: Nếu Port là `TopicRepositoryPort` thì Adapter phải tên là `TopicRepositoryAdapter` (không đặt là `TopicDatabaseAdapter`). Nếu Port là `TokenServicePort` thì Adapter là `TokenServiceAdapter`.
- **Use Cases:**
  - Triển khai trực tiếp Inbound Port.
  - **KHÔNG dùng hậu tố `Impl`**. (Đúng: `class UpdateUserUseCase implements UpdateUserInputPort`).
  - Phải được inject các Outbound Ports qua Constructor.
- **DTOs & Mappers:**
  - Input gọi là `Command` (nếu có ghi dữ liệu) hoặc tham số rời.
  - Output gọi là `Result` (VD: `UserResult`).
  - **BẮT BUỘC:** Sử dụng `record` của Java 14+ cho toàn bộ `Command` và `Result` ở tầng Application để đảm bảo tính Immutable tinh khiết và ngắn gọn (không dùng `class` hay Lombok).
  - Sử dụng Mapper code tay ở tầng này để chuyển `Domain Model` -> `Result` (VD: `UserResultMapper`).

---

## 5. Tầng Presentation (`core/presentation`)
Là nơi giao tiếp với Client qua REST API.
- **Controllers:** Sử dụng `@RestController` và `@RequestMapping("/api/v1/...")`.
- **Dependency Injection:** Sử dụng `@RequiredArgsConstructor` của Lombok, tiêm các `InputPort` vào Controller.
- **DTOs:** Dữ liệu vào là `*Request`, dữ liệu ra là `*Response`.
- **Mappers (MapStruct):** 
  - Bắt buộc dùng MapStruct để map `Request -> Command` và `Result -> Response`.
  - Khai báo: `@Mapper(componentModel = "spring")`.
  - **BẮT BUỘC (Rule of Code Reuse):** Không được tạo `new ObjectMapper()` tràn lan trong các hàm `default` của Mapper. Các logic biến đổi phức tạp, hay dùng nhiều (như convert Object ↔ JSON String) **PHẢI** được tách ra thành một Spring `@Component` dùng chung (VD: `JsonMapper` đặt ở `core/presentation/.../shared/mapper`) và nhúng vào MapStruct thông qua thuộc tính `uses = {JsonMapper.class}`.
- **Response Format:** 
  - Return kiểu `ResponseEntity<*Response>`.
  - Kết hợp annotation `@ApiResponseMessage(message = DetailMessageKey...)` để chuẩn hoá thông báo trả về (thường dùng DetailMessageKey). Không cần tự bọc Response bằng `ApiResponse` vì hệ thống đã cấu hình sẵn `ApiResponseHandler` để tự động làm việc này.

---

## 6. Xử lý Lỗi và Messages (Exception Handling)
Dự án sử dụng cơ chế xử lý lỗi tuỳ chỉnh kết hợp với i18n (Internationalization).

- **Throw Exception:**
  Thay vì ném `RuntimeException` chung chung, bắt buộc ném `ApplicationException` (từ module `shared`).
  ```java
  throw new ApplicationException(
          UserErrorCode.USER_NOT_FOUND,
          UserDetailMessageKey.USER_ID_NOT_FOUND
  );
  ```
- **Tạo mã lỗi (Error Codes):**
  Tạo Enum kế thừa interface `ErrorCode` để định nghĩa mã lỗi. (VD: `USER_001`).
  - **BẮT BUỘC (Rule of Clean Architecture):** Tầng Domain và Application **TUYỆT ĐỐI KHÔNG ĐƯỢC PHÉP** import các thư viện của Spring Web (như `org.springframework.http.HttpStatus` hay `ResponseEntity`). Việc ánh xạ từ lỗi nghiệp vụ (`ErrorCode`) sang mã lỗi HTTP (400, 404, 500) là trách nhiệm của tầng Presentation (thông qua `GlobalExceptionHandler`). Tầng Core chỉ trả về mã String và Title Key (VD: `UserTitleMessageKey.USER_NOT_FOUND_TITLE`).
- **Message Keys (Module `core/common`):**
  Mọi thông báo (kể cả thành công hay thất bại) phải được hằng số hoá. KHÔNG hardcode chuỗi text trực tiếp trong UseCase hay Controller.
  - **BẮT BUỘC:** Các file thông báo (Message Keys) đã được chuyển sang module `core/common` để quản lý tập trung. Bạn phải chia Message Keys thành 2 loại: `TitleMessageKey` (dùng cho title của ErrorCode) và `DetailMessageKey` (dùng cho thông báo chi tiết, thành công).
  - **BẮT BUỘC:** Khai báo các class MessageKey này trong thư mục `core/common/src/main/java/org/naho/i18n/message/[module]/`.
  - **BẮT BUỘC:** Khai báo giá trị của Message Keys vào các file properties tương ứng trong thư mục `core/common/src/main/resources/i18n/[module]/[module].properties` và `[module]_vi.properties`.
  - **BẮT BUỘC:** Phải đăng ký đường dẫn file properties đó vào class `MessageSourceConfig.java` ở module `core/bootstrap` bằng hàm `setBasenames` để Spring có thể đọc được (VD: `"classpath:i18n/[module]/[module]"`).

---

## 7. Tầng Infrastructure & Bootstrap
- **Infrastructure (`core/infrastructure`):** 
  - Chứa DB Adapters. Các class `*Adapter` sẽ `implements *RepositoryPort`.
  - Chứa Spring Data JPA (`@Entity`, `@Table`, `JpaRepository`). Nơi duy nhất được phép có annotation của database.
- **Bootstrap (`core/bootstrap`):** 
  - Chứa các class cấu hình `@Configuration`.
  - Sử dụng `@Bean` để khởi tạo UseCase, tiêm Adapter tương ứng vào UseCase (VD: `FuriganaConfig.java`). Tầng Application không dùng `@Service` hay `@Component` để đảm bảo sạch bóng Spring.
