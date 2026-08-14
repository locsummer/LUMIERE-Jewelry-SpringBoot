# TÀI LIỆU AGILE - DỰ ÁN ĐẶT MÓN ĂN TRỰC TUYẾN

## 1. Product Goal

Xây dựng website giúp khách hàng tìm và đặt sản phẩm trực tuyến nhanh chóng; giúp nhân viên xử lý đơn theo quy trình rõ ràng; giúp quản trị viên quản lý bộ sưu tập, tài khoản và doanh thu trên một hệ thống thống nhất.

## 2. Stakeholder

| Stakeholder | Nhu cầu chính |
|---|---|
| Khách hàng | Xem sản phẩm, đặt hàng, thanh toán và theo dõi đơn |
| Nhân viên | Tiếp nhận, xác nhận, chuẩn bị và cập nhật giao hàng |
| Quản trị viên | Quản lý sản phẩm, danh mục, người dùng, phân quyền và thống kê |
| Chủ cửa hàng | Theo dõi số đơn, doanh thu và hoạt động bán hàng |

## 3. Product Backlog

| ID | User Story | Ưu tiên | Story Point | Tiêu chí chấp nhận chính |
|---|---|---:|---:|---|
| US01 | Là khách hàng, tôi muốn đăng ký tài khoản | High | 3 | Email hợp lệ, không trùng, mật khẩu ít nhất 6 ký tự |
| US02 | Là người dùng, tôi muốn đăng nhập/đăng xuất | High | 3 | Đúng tài khoản thì chuyển theo vai trò; sai thì báo lỗi |
| US03 | Là khách hàng, tôi muốn cập nhật hồ sơ và địa chỉ | Medium | 3 | Lưu được tên, số điện thoại, địa chỉ mặc định |
| US04 | Là khách hàng, tôi muốn xem sản phẩm theo danh mục | High | 3 | Chỉ hiển thị sản phẩm và danh mục đang hoạt động |
| US05 | Là khách hàng, tôi muốn tìm kiếm sản phẩm | High | 3 | Tìm theo tên/mô tả, kết hợp được danh mục |
| US06 | Là khách hàng, tôi muốn xem chi tiết sản phẩm | High | 2 | Có ảnh, giá, mô tả, tồn kho, thời gian chuẩn bị |
| US07 | Là khách hàng, tôi muốn quản lý giỏ hàng | High | 5 | Thêm, đổi số lượng, xóa sản phẩm, không vượt tồn kho |
| US08 | Là khách hàng, tôi muốn đặt sản phẩm | High | 8 | Kiểm tra thông tin nhận hàng, tính phí, trừ tồn kho |
| US09 | Là khách hàng, tôi muốn chọn cách thanh toán | High | 5 | Có COD, chuyển khoản demo và VNPay demo |
| US10 | Là khách hàng, tôi muốn theo dõi/hủy đơn | High | 5 | Xem lịch sử, chi tiết; chỉ hủy khi chưa chuẩn bị; đơn hủy không thể thanh toán |
| US11 | Là nhân viên, tôi muốn xử lý trạng thái đơn | High | 5 | Cập nhật đúng chuỗi trạng thái và thanh toán |
| US12 | Là nhân viên, tôi muốn quản lý sản phẩm/tồn kho | High | 5 | Thêm, sửa, xóa, bật/tắt sản phẩm và cập nhật tồn kho |
| US13 | Là quản trị viên, tôi muốn quản lý danh mục | Medium | 3 | CRUD; danh mục có sản phẩm chỉ được ẩn |
| US14 | Là quản trị viên, tôi muốn quản lý tài khoản theo chức vụ | High | 5 | Lọc theo chức vụ; tạo/sửa tài khoản; đặt lại mật khẩu; phân quyền và khóa/mở |
| US15 | Là quản trị viên, tôi muốn xem dashboard | Medium | 5 | Có số đơn, đơn chờ, sản phẩm, khách, danh mục, doanh thu |
| US16 | Là hệ thống, tôi muốn gửi xác nhận email | Low | 3 | Có cấu hình thì gửi; lỗi mail không làm mất đơn |
| US17 | Là khách hàng, tôi muốn yêu cầu hoàn tiền | High | 5 | Đơn đã hủy và đã thanh toán được gửi yêu cầu; nhân viên duyệt/từ chối |

Tổng Story Point dự kiến: **76**.

## 4. Release Backlog và Sprint Backlog

| Sprint | Mục tiêu Sprint | User Story | Kết quả có thể bàn giao |
|---|---|---|---|
| Sprint 1 | Hoàn thiện tài khoản và bộ sưu tập | US01–US06 | Người dùng đăng ký, đăng nhập, xem/tìm sản phẩm và hồ sơ |
| Sprint 2 | Hoàn thiện quy trình mua hàng | US07–US10 | Giỏ hàng, checkout, thanh toán demo, lịch sử đơn |
| Sprint 3 | Hoàn thiện vận hành cửa hàng | US11–US13 | Nhân viên xử lý đơn/sản phẩm; admin quản lý danh mục |
| Sprint 4 | Hoàn thiện quản trị và chất lượng | US14–US17 | Tài khoản, dashboard, hoàn tiền, email tùy chọn, test và hướng dẫn |

## 5. Quy tắc nghiệp vụ chính

- Email đăng ký không được trùng.
- Mỗi tài khoản có một vai trò: CUSTOMER, STAFF hoặc ADMIN.
- CUSTOMER chỉ sử dụng chức năng mua hàng; STAFF quản lý sản phẩm và đơn; ADMIN quản trị toàn hệ thống.
- Chỉ ADMIN được tạo tài khoản nội bộ, sửa thông tin và thay đổi chức vụ.
- Bộ sưu tập mẫu có 46 sản phẩm thuộc 11 danh mục; mỗi sản phẩm có ảnh riêng và bản cập nhật tự bổ sung vào database cũ một lần.
- Số lượng trong giỏ và đơn không vượt tồn kho.
- Đơn từ 200.000đ được miễn phí giao hàng; đơn thấp hơn có phí 15.000đ.
- Khi đặt hàng, hệ thống trừ tồn kho; khi hủy hợp lệ, hệ thống hoàn tồn kho.
- Khách hàng chỉ hủy khi trạng thái là Chờ xác nhận hoặc Đã xác nhận.
- Khi hủy đơn chưa thanh toán, thanh toán chuyển sang Đã hủy thanh toán và mọi đường dẫn thanh toán bị khóa.
- Khi hủy đơn đã thanh toán, khách hàng có thể gửi một yêu cầu hoàn tiền kèm lý do.
- Nhân viên hoặc quản trị viên duyệt/từ chối yêu cầu; hoàn tiền trong bài tập là quy trình mô phỏng.
- Đơn đã hoàn thành hoặc đã hủy không đổi lại trạng thái.
- COD được đánh dấu đã thanh toán khi đơn hoàn thành.
- Email tắt hoặc lỗi không được làm thất bại giao dịch lưu đơn.

## 6. Definition of Done

- Mã biên dịch bằng Java 17 và Maven.
- Chức năng có validation và thông báo rõ ràng.
- Phân quyền đúng cho ba vai trò.
- Dữ liệu lưu được bằng H2; có cấu hình MySQL tùy chọn.
- Giao diện responsive và dùng tiếng Việt.
- Kiểm thử tự động khởi động context, trang chủ/bộ sưu tập và phân quyền CUSTOMER / STAFF / ADMIN.
- Có tài khoản mẫu, README và hướng dẫn chạy IntelliJ.

## 7. Kết quả kiểm thử bàn giao

- `mvn test`: 5/5 test đạt.
- Khởi động file JAR: thành công trên cổng kiểm thử.
- Trang công khai, giỏ hàng, checkout, hồ sơ, lịch sử đơn: HTTP 200.
- Trang bộ sưu tập hiển thị đủ sản phẩm mới; database kiểm thử có tối thiểu 11 danh mục, 46 sản phẩm, 46 đường dẫn ảnh không trùng và không thiếu tệp ảnh.
- Đăng nhập khách hàng và admin: chuyển hướng đúng vai trò.
- Tạo đơn COD từ giỏ hàng: thành công.
- Admin mở được dashboard, danh sách theo chức vụ và form tạo tài khoản: HTTP 200.
- Nhân viên mở được quản lý đơn/sản phẩm nhưng bị từ chối truy cập quản lý tài khoản.
- Tạo tài khoản Nhân viên bằng form quản trị và lưu đúng chức vụ: đạt.
- Hủy đơn chưa thanh toán: đơn chuyển Đã hủy, thanh toán chuyển Đã hủy thanh toán và URL thanh toán bị chặn.
- Hủy đơn đã thanh toán: khách gửi yêu cầu hoàn tiền, admin duyệt và trạng thái chuyển Đã hoàn tiền.
