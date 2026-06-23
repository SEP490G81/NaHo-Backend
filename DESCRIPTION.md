# Mô tả dự án

- Dự án được thiết kế theo kiến trúc Clean Architecture

## Module common

- Thư mục này được sử dụng để chia sẻ cho các module khác
- Java thuần, không phụ thuộc vào công nghệ bên ngoài
- Nó chứa các thư mục như:

+ i18n: chứa các message constants (lưu ý: interface cổng ra `MessageService` định nghĩa ở module `application`, còn `MessageServiceAdapter` triển khai ở module `infrastructure`)
+ logging: chứa các key được sử dụng trong logging
+ resources: có các messages.properties files

## Module domain

- Implement module `common`
- Java thuần, không phụ thuộc vào công nghệ bên ngoài
- Module này tôi sẽ chia theo features ví dụ như user, file...
- Trong các features đó sẽ có các thư mục:

+ exception: chứa các error code của domain
+ model: chứa các model, nghiệp vụ, business rules
+ type: chứa các enum sử dụng cho model đó
+ valueobject: là các thuộc tính của model nhưng có kèm business rules (ví dụ email phải đúng định dạng)

- Thư mục shared là thư mục chia sẻ cho các modules khác và các package trong module này
- Trong đó có các thư mục:

+ exception:

* BaseException (toàn bộ các custom exception trong hệ thống phải kế thừa class này)
* DomainException (các lỗi trong domain sẽ phải throw ra exception này)
* ErrorCode (các ErrorCode của hệ thống phải kế thừa interface này)

## Module application

- Implement module `common`, `domain`
- Java thuần, không phụ thuộc vào công nghệ bên ngoài
- Module này tôi cũng chia theo các features, ví dụ như user, file...
- Trong các features đó sẽ có các thư mục:

+ command: là class chứa data đầu vào của các usecase
+ constant: chứa các hằng số sử dụng cho module
+ exception: chứa các error code được sử dụng trong module đó và các module implement
+ mapper: chuyển đổi giữa các object như command, domain model, result...
+ port:

* in: là các interface được chính module này implement
* out: là các interface được các module khác implement

+ result: là class chứa data đầu ra của các usecase
+ usecase: folder quan trọng nhất, là các usecase của dự án

- Thư mục shared là thư mục chia sẻ cho các modules khác và các package trong module này

## Module infrastructure

- Implement module `common`, `domain`, `application`
- Được sử dụng công nghệ bên ngoài
- Module này tôi cũng chia theo các features, ví dụ như user, file...
- Trong các features đó sẽ có các thư mục:

+ adapter: là nơi implement các port out của application
+ constant: chứa các hằng số sử dụng cho module
+ entity: các thực thể JPA để thao tác với database (MySQL)
+ mapper: chuyển đổi giữa các object như result, entity, domain model,...
+ mybatis: interface sử dụng mybatis để thao tác với database
+ repository: chứa các JPA repository để thao tác với database

- Thư mục shared là thư mục chia sẻ cho các modules khác và các package trong module này (bao gồm package `persistence` chứa `BaseEntity` và `BaseJpaRepository`)

## Module presentation

- Implement module `common`, `domain`, `application`
- Được sử dụng công nghệ bên ngoài
- Module này tôi cũng chia theo các features, ví dụ như user, file...
- Trong các features đó sẽ có các thư mục:

+ controller: chứa các controller (API)
+ dto: gồm request, response và mapper để map giữa các object như request, response, result, command...

- Thư mục shared là thư mục chia sẻ cho các modules khác và các package trong module này
- Trong đó có 1 số file đặc biệt như:

+ RequestLoggingFilter: là 1 lớp filter để trước khi request vào controller, nó sẽ lấy các thông tin từ request để log
  ra
+ ApiResponseHandler: là 1 lớp để format định dạng của Response trước khi trả ra cho client
+ CustomOAuth2SuccessHandler: có hàm onAuthenticationSuccess để thực hiện login với google
+ GlobalExceptionHandler: xử lí exception của toàn hệ thống
+ Response: ApiMeta, ApiResponse, PageMeta

## Module bootstrap

- Implement module `common`, `application`, `domain`, `infrastructure`, `presentation`
- Được sử dụng công nghệ bên ngoài
- Đây là nơi chứa hàm Main để chạy toàn bộ dự án
- Ngoài ra còn là nơi để cấu hình bean, i18n, redis, s3, spring security
- Chứa các file cấu hình `application.yaml` (ở thư mục gốc tài nguyên) và `application-dev.yaml`, `application-prod.yaml` (ở thư mục `config/`)

