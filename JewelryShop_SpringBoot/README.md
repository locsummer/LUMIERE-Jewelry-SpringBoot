# LUMIÈRE Jewelry — Website bán trang sức trực tuyến

Dự án được chuyển đổi từ project project cũ sang website thương mại điện tử bán trang sức, vẫn giữ kiến trúc Spring Boot + Thymeleaf + JPA + Spring Security.

## Công nghệ
- Java 17
- Spring Boot 3.3.5
- Spring MVC + Thymeleaf
- Spring Data JPA / Hibernate
- Spring Security
- H2 mặc định, hỗ trợ MySQL
- Bootstrap + CSS giao diện luxury

## Chức năng
### Khách hàng
- Trang chủ LUMIÈRE Jewelry
- Xem bộ sưu tập trang sức
- Lọc theo danh mục: Nhẫn, Dây chuyền, Bông tai, Vòng tay, Bộ trang sức, Quà tặng
- Tìm kiếm sản phẩm
- Xem chi tiết sản phẩm
- Giỏ hàng
- Thanh toán và chọn phương thức thanh toán
- Theo dõi đơn hàng
- Quản lý hồ sơ
- Gửi yêu cầu hỗ trợ

### Quản trị viên / nhân viên
- Dashboard
- Quản lý sản phẩm
- Quản lý danh mục
- Quản lý đơn hàng
- Quản lý tài khoản
- Chăm sóc khách hàng
- Phân quyền ADMIN / STAFF / CUSTOMER

## Tài khoản demo
- Admin: `admin@lumiere.vn` / `admin123`
- Staff: `staff@lumiere.vn` / `staff123`
- Customer: `user@lumiere.vn` / `user123`

## Chạy project

### IntelliJ IDEA
1. Mở thư mục `JewelryShop_SpringBoot`.
2. Chọn JDK 17.
3. Reload Maven.
4. Chạy `JewelryShopApplication`.
5. Truy cập `http://localhost:8080`.

### Maven
```bash
./mvnw spring-boot:run
```

H2 console: `http://localhost:8080/h2-console`

## Cấu trúc chính
- `src/main/java/com/agile/jewelryshop/model` — entity
- `src/main/java/com/agile/jewelryshop/controller` — controller khách hàng
- `src/main/java/com/agile/jewelryshop/controller/admin` — quản trị
- `src/main/java/com/agile/jewelryshop/service` — nghiệp vụ
- `src/main/resources/templates` — giao diện Thymeleaf
- `src/main/resources/static/images/jewelry` — hình ảnh trang sức minh họa
- `src/main/resources/static/css` — giao diện luxury

> Lưu ý: môi trường hiện tại không có Maven cache và không thể tải Maven Wrapper từ Internet, nên chưa thể chạy build tự động trong môi trường tạo file. Source đã được kiểm tra tĩnh về package/import và mapping view/template.
