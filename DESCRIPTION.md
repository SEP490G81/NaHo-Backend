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

### II. Tổ chức team trên AWS

- Hiện tại nhóm tôi gồm 5 người
- Tôi sử dụng tài khoản root để tạo ra 5 IAM Users cho chính tôi và 4 người còn lại
- Tôi đã add cả nhóm vào 1 group là S3Developers, giờ tôi muốn những IAM trong group này có policies sau:

+ Có full quyền đối với S3 storage: arn:aws:s3:::naho-bucket
+ Có quyền tạo, xem, quản lí access key của chính họ
+ Phải bật MFA thì mới được có các quyền trên, nếu chưa bật thì bị cấm toàn bộ quyền, nhưng mọi quyền liên quan đến MFA
  phải luôn được allow
