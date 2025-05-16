1. Overview
Education Play Connect là một ứng dụng chat thời gian thực được phát triển nhằm cung cấp nền tảng giao tiếp trực tuyến hiệu quả cho người dùng. Backend của ứng dụng được xây dựng bằng Java và Spring Boot, sử dụng kiến trúc RESTful API để giao tiếp với frontend. Hệ thống hỗ trợ quản lý người dùng, tạo và quản lý cuộc trò chuyện (bao gồm cả nhóm và cá nhân), tải lên và lưu trữ ảnh đại diện nhóm, cũng như lưu trữ lịch sử trò chuyện. Dữ liệu được lưu trong cơ sở dữ liệu quan hệ (MySQL hoặc PostgreSQL) thông qua JPA/Hibernate, với các API được thiết kế tương thích với thư viện Retrofit trên phía client.

Backend tập trung vào việc xử lý các yêu cầu từ ứng dụng Android (client), đảm bảo tính ổn định và bảo mật trong việc quản lý dữ liệu người dùng và cuộc trò chuyện. Hệ thống cũng hỗ trợ lưu trữ tệp (ảnh đại diện nhóm) trên hệ thống tệp cục bộ, với khả năng mở rộng trong tương lai để tích hợp các dịch vụ lưu trữ đám mây
2. Các chức năng chính:
- Xem bảng tin newfeed
- Kết bạn giữa các người dùng
- Nhắn tin realtime
- Sửa đổi thông tin người dùng
- Học chữ cái
- Làm bài tập tính toán và xếp hạng
- Làm bài quiz online cùng với các người dùng khác
- Tra cứu từ điển
- Tham gia, thoát, sãn sàng chơi ở phòng quiz
- Học tập qua video
