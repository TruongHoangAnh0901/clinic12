# PROJECT OVERVIEW

## 1. Phân tích Kiến trúc
- **Mô hình kiến trúc:** Ứng dụng được xây dựng theo mô hình Monolithic, áp dụng chuẩn thiết kế đa tầng MVC (Model - View - Controller). Code được chia thành các package rõ ràng: `controller`, `service`, `repository`, `entity`, `dto`, `config`.
- **Tech Stack:** 
  - **Backend:** Java 21, Spring Boot 3.4.4.
  - **Data Access:** Spring Data JPA, Hibernate.
  - **Database:** MySQL.
  - **Security:** Spring Security, OAuth2 Client (đăng nhập bằng Google).
  - **Tooling:** Maven, Lombok.
- **Entry Point:** 
  - File bắt đầu khởi chạy ứng dụng là `e:\nhom12\nhom12\src\main\java\com\clinic\nhom12\Nhom12Application.java` (`@SpringBootApplication`).

## 2. Phân tích Dependency (từ `pom.xml`)
Các thư viện/framework quan trọng nền tảng:
- `spring-boot-starter-web`: Cung cấp môi trường chạy web app, RESTful API và container Tomcat tích hợp.
- `spring-boot-starter-data-jpa`: Giao tiếp với cơ sở dữ liệu MySQL thông qua JPA/Hibernate.
- `spring-boot-starter-security` & `spring-boot-starter-oauth2-client`: Xử lý phân quyền, xác thực và tích hợp đăng nhập qua Google.
- `mysql-connector-j`: Driver kết nối cơ sở dữ liệu MySQL.
- `spring-boot-starter-validation`: Cung cấp annotation để xác thực dữ liệu đầu vào.
- `lombok`: Giảm thiểu boilerplate code (Getter, Setter, Constructor...).

## 3. Tóm tắt Luồng (Logic Flow)
Ứng dụng hoạt động theo mô hình yêu cầu - phản hồi (Request - Response). Cụ thể với luồng đặt khám (chức năng chính):
- **Luồng dữ liệu (Data Flow):** 
  - Client (Frontend) gửi các HTTP Request (GET, POST, PUT, DELETE) đến hệ thống.
  - Các yêu cầu này được tiếp nhận trực tiếp bởi tầng `Controller` (VD: `AppointmentController`).
  - `Controller` điều hướng yêu cầu qua tầng `Service` (`AppointmentService`), nơi chứa các quy tắc nghiệp vụ quan trọng (Business Logic).
  - Cuối cùng, tầng `Service` gọi tới `Repository` (`AppointmentRepository`) để giao tiếp trực tiếp với cơ sở dữ liệu (lưu trữ, tìm kiếm, cập nhật).
- **Ví dụ tương tác:** Khi có lịch hẹn mới, trạng thái mặc định được gán "CHỜ DUYỆT". Khi bác sĩ hoặc quản trị viên duyệt lịch sang trạng thái "ĐÃ XÁC NHẬN", tầng Service sẽ kích hoạt `EmailService` để gửi email thông báo cho bệnh nhân.

## 4. Code Review (Các vị trí cần tối ưu)
Dựa vào việc rà soát mã nguồn (ví dụ ở module `AppointmentController` và `AppointmentService`), có một số Code Smells và rủi ro được phát hiện:

- **Lỗi bảo mật (Mass Assignment Vulnerability) / Không dùng DTO:** `AppointmentController` truyền trực tiếp Entity `Appointment` vào và ra trong các phương thức (ví dụ `@RequestBody Appointment appointment`). Việc phơi bày Entity trực tiếp có thể giúp kẻ tấn công cập nhật các trường nhạy cảm bằng cách gửi kèm request. Thay vào đó nên sử dụng các đối tượng DTO (Data Transfer Object).
- **Code Smell (Hardcode chuỗi Status):** Trong `AppointmentService`, các logic kiểm tra trạng thái (`"CHỜ DUYỆT"`, `"ĐÃ XÁC NHẬN"`) đang được gõ trực tiếp (magic strings). Việc này dễ dẫn đến lỗi chính tả và khó bảo trì sau này. Nên áp dụng `Enum` (VD: `AppointmentStatus.CONFIRMED`).
- **Thiếu Data Validation / Kiểm tra nghiệp vụ:** Phương thức tạo lịch khám không có bước xác thực ngày khám hợp lệ (VD: không được đặt ngày trong quá khứ) hoặc có cơ chế chống trùng lặp giờ giữa các bệnh nhân của cùng một Bác sĩ trước khi insert thẳng xuống database.

## 5. Task Plan (Các bước cần làm tiếp theo)
Dưới đây là các đề xuất để cải thiện dự án:
- [ ] **Tạo DTO & Mapper:** Áp dụng Request DTO & Response DTO thay vì map trực tiếp với Entity trong toàn bộ API (áp dụng kèm `MapStruct` hoặc `ModelMapper`).
- [ ] **Refactor Constants/Enum:** Thay thế các chuỗi trạng thái bị hardcode bằng Enum (`AppointmentStatus`, `UserRole`, `RoleStatus`...).
- [ ] **Bổ sung Business Validation:** Cập nhật Service layer, thêm các annotation `@Valid` tại các Payload vào Controller. Validation ngày, giờ bị trùng lịch.
- [ ] **Tạo Custom Global Exception Handler:** Sử dụng `@ControllerAdvice` để bắt các ngoại lệ chung (Exception) và trả về định dạng response HTTP thống nhất thay vì stack traces lỗi mặc định của Spring.
- [ ] **Cấu trúc lại Update logic:** Trong Service `updateAppointment` hiện đang dùng nhiều câu lệnh `if... != null`. Việc này nên được thu gọn lại bằng Reflection hay các thư viện mapping model để code sáng sủa hơn.
