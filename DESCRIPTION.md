### I. Mô tả kiến trúc dự án

- Source code của dự án được tổ chức theo kiến trúc Clean Architecture gồm 5 module:

#### 1. Domain

- Chứa các domain model, các valueobject và các nghiệp vụ hệ thống.

#### 2. Application

- Implement project Domain

- Là nơi chứa các use case, port in, port out, command và result.

#### 3. Infrastructure

- Implement project Domain và Application

- Sử dụng các thư viện bên ngoài, là nơi chứa các adapter implements từ port in của Application

- Chứa Spring Data JPA, Entity

#### 4. Presentation

- Implement project Domain và Application

- Là nơi có DTOs, Controllers, GlobalExceptionHandler

#### 5. Bootstrap

- Implement project Domain, Application, Infrastructure, Presentation

- Chứa hàm main để chạy toàn bộ dự án, các file config như: Bean Config, S3, I18n, Spring Security...

- Chứa các file cấu hình application.yaml, application-dev.yaml, và application-prod.yaml

Trên đây là cấu trúc dự án hiện tại của tôi, giờ tôi muốn bạn triển khai cho tôi
chức năng login với google (tôi đã cấu hình các thứ cần thiết).
Luồng hoạt động sẽ là user gọi tới google sdk do spring boot phụ trách rồi xử lí các bước đăng nhập.
Ngoài ra tôi cũng phải làm sao để lấy được deviceId từ cookie của user, user agent và ip address từ header.
Bạn chỉ cần cho tôi các file code luồng chính (tôi đã có các model, entity, dto,... cần thiết)
Chỉ cho tôi cả nơi để đặt file.
Các file chứa đầy đủ code từ import đến hết.
