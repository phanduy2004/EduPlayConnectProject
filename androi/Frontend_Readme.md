1. Overview
  Education Play Connect là ứng dụng chat thời gian thực được thiết kế để hỗ trợ giao tiếp giữa người dùng thông qua các cuộc trò chuyện cá nhân và nhóm. Frontend được phát triển bằng Java trên nền tảng Android, sử dụng các thư viện như Retrofit để gọi API, Glide để tải và hiển thị ảnh, và RecyclerView để hiển thị danh sách cuộc trò chuyện. Ứng dụng tập trung vào giao diện người dùng thân thiện, cho phép người dùng tạo nhóm, chọn ảnh đại diện, thêm thành viên, và xem danh sách liên hệ.
2. Công nghệ trong Frontend
- Ngôn ngữ lập trình: Java
- Nền tảng: Android
- Thư viện giao tiếp API: Retrofit (gọi RESTful API từ backend)
- Thư viện tải ảnh: Glide (tải và hiển thị ảnh đại diện)
- Giao diện người dùng:
+ RecyclerView (hiển thị danh sách cuộc trò chuyện)
+ BottomNavigationView (điều hướng giữa các màn hình)
+ Quản lý JSON: Gson (chuyển đổi dữ liệu JSON)
+ Xử lý quyền truy cập: Android Permissions (ví dụ: READ_MEDIA_IMAGES)
