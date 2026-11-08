# Phone Mobile - Ứng Dụng Danh Bạ & Cuộc Gọi Android

> Ứng dụng quản lý Danh bạ & Quay số điện thoại hiện đại dành cho Android, được phát triển bằng **Jetpack Compose** và **Material 3**, thiết kế bám sát 100% mẫu giao diện từ **Figma (Giao diện Danh bạ)**.

---

## 📱 Giới thiệu tổng quan

**Phone Mobile** là ứng dụng di động cho phép người dùng quản lý danh bạ cá nhân, tìm kiếm thông minh, thực hiện cuộc gọi, gửi tin nhắn và theo dõi lịch sử cuộc gọi một cách trực quan, mượt mà và tối ưu hiệu năng.

### ✨ Tính năng nổi bật

1. **Quản lý Danh bạ (Contacts):**
   - Tự động gom nhóm danh bạ theo bảng chữ cái tiếng Việt (**A, B, C, D, ...**).
   - **Tìm kiếm không dấu thông minh:** Cho phép gõ tìm kiếm tiếng Việt không cần gõ dấu (VD: gõ `nguyen` tìm thấy `Nguyễn`, gõ `dang` tìm thấy `Đặng`, `le` tìm thấy `Lê`).
   - Hỗ trợ tìm kiếm theo cả họ tên và số điện thoại theo thời gian thực.
   - Hiển thị ngôi sao vàng ⭐ trực tiếp trên danh bạ cho các liên hệ thuộc danh sách yêu thích.
   - Nút gọi điện nhanh ngay trên từng dòng danh bạ.

2. **Màn hình Danh bạ trống (Empty State):**
   - Tự động hiển thị thẻ minh họa profile card bo góc màu xanh pastel khi máy chưa có liên hệ hoặc đã xóa hết danh bạ.
   - Kèm tiêu đề, thông điệp hướng dẫn và nút bấm `+ Thêm liên hệ` nhanh theo đúng thiết kế Figma (Node `5:1791`).

3. **Xem Chi tiết liên hệ (Contact Details):**
   - Hiển thị avatar đại diện cỡ lớn cùng họ tên đầy đủ.
   - Thẻ thông tin gồm: Số điện thoại, Email, Địa chỉ, Ngày sinh, Ghi chú.
   - **Cụm 2 nút hành động chính:** Nút `[📞 Gọi điện]` và `[📹 Gọi video]`.
   - Các hành động mở rộng: Gửi tin nhắn, Bật/tắt yêu thích, Nút chỉnh sửa và Nút xóa.
   - **Hộp thoại xác nhận xóa chuẩn Figma:** Hiển thị icon thùng rác đỏ trên đỉnh hộp thoại, thông điệp căn giữa và 2 nút bấm `Hủy` / `Xóa` viền bo tròn.

4. **Thêm mới & Chỉnh sửa liên hệ (Add / Edit Contact):**
   - Form nhập liệu trực quan với biểu tượng đại diện cho từng trường thông tin.
   - Chọn / đổi ảnh đại diện với icon máy ảnh.
   - **Hệ thống xác thực & bắt lỗi chuẩn Figma (Node `5:1608`):**
     - Đổi viền ô sang màu đỏ (`#EA4335`).
     - Hiển thị icon cảnh báo đỏ chứa dấu chấm than trắng (`!`).
     - Báo lỗi để trống: *"Họ tên không được để trống"*, *"Số điện thoại không được để trống"*.
     - Báo lỗi định dạng: *"Số điện thoại không hợp lệ"*.
     - **Báo lỗi trùng lặp:** Tự động kiểm tra danh bạ và báo *"Số điện thoại đã tồn tại trong danh bạ"*.
     - Tự động xóa lỗi ngay khi người dùng chỉnh sửa lại dữ liệu.

5. **Bàn phím quay số thông minh (Keypad / Dialpad):**
   - Bàn phím số 3x4 bo tròn (1-9, \*, 0, #) kèm ký tự chữ cái (ABC, DEF...).
   - **Nhận diện danh bạ thời gian thực:** Tự động dò tìm trong danh bạ và hiển thị ngay họ tên người nhận kèm ngôi sao yêu thích trên màn hình bàn phím khi số khớp.

6. **Giao diện cuộc gọi toàn màn hình phong cách iPhone (In-Call Screen):**
   - **Trực quan & Hiện đại:** Khi bấm gọi từ bất kỳ màn hình nào (Danh bạ, Chi tiết, Yêu thích, Lịch sử hay Bàn phím), ứng dụng mở trực tiếp màn hình cuộc gọi riêng biệt phong cách iOS, không chuyển vào tab bàn phím.
   - **Thông tin cuộc gọi:** Tên liên hệ to rõ nét, số điện thoại phụ, avatar profile bo tròn và trạng thái kết nối chuyển tiếp mượt mà từ "đang gọi..." sang bộ đếm thời lượng cuộc gọi (`00:01`, `00:02`...).
   - **Lưới 6 nút chức năng chuẩn iOS:** Tắt tiếng (Mute - đổi trạng thái sáng/tối), Bàn phím (mở bàn phím số DTMF trong cuộc gọi với nút Ẩn), Loa ngoài (Speaker), Thêm cuộc gọi, FaceTime, Danh bạ.
   - **Nút kết thúc cuộc gọi đỏ tròn:** Nút cúp máy lớn 75dp màu đỏ (`#FF3B30`), tự động lưu thời lượng cuộc gọi vào nhật ký và quay lại màn hình trước đó.

7. **Danh bạ yêu thích (Favorites):**
   - Danh sách gom các liên hệ quan trọng được đánh dấu sao.
   - Cung cấp **bộ đôi nút thao tác nhanh** ở mỗi dòng: Gọi điện thoại (`ic_call`) và Nhắn tin nhanh (`ic_chat_bubble`) chuẩn Figma Group 71.

8. **Nhật ký cuộc gọi (Recents / Call History):**
   - Phân loại rõ ràng các loại cuộc gọi với icon và màu sắc riêng:
     - 🟢 **Cuộc gọi đến (Incoming):** Mũi tên xanh lá
     - 🔵 **Cuộc gọi đi (Outgoing):** Mũi tên xanh dương
     - 🔴 **Cuộc gọi nhỡ (Missed):** Mũi tên đỏ và tên hiển thị màu đỏ
   - Tự động ghi lại nhật ký mỗi khi người dùng thực hiện cuộc gọi trong ứng dụng.

9. **Menu "Thêm" - Chia sẻ danh thiếp & Quét mã QR:**
   - **Giao diện chuẩn thiết kế:** Avatar tròn lớn có huy hiệu Camera xanh tròn ở góc dưới bên phải, Họ tên (**Nguyễn Thị Hồng Ngọc**), Chức danh (**Sinh viên**), thẻ thông tin bo tròn màu xanh nhạt gồm **SĐT** (`0987 654 321`) và **Email** (`ngocnguyen@gmail.com`).
   - **Nút "Tạo mã QR":** Hiển thị mã QR danh thiếp cá nhân dạng ma trận trực quan để người khác quét lưu danh bạ tức thì, hỗ trợ chia sẻ link.
   - **Nút "Quét mã QR":** Giao diện khung ngắm Camera scanner kèm hiệu ứng tia quét laser di chuyển lên xuống và tính năng quét/thêm liên hệ mới vào danh bạ.
   - **Tùy chỉnh thông tin:** Bấm vào thẻ thông tin hoặc avatar để cập nhật danh thiếp của bạn.

---

## 🛠 Công nghệ sử dụng (Tech Stack)

| Thành phần | Phiên bản / Thư viện |
| :--- | :--- |
| **Ngôn ngữ** | Kotlin 2.2.10 |
| **Giao diện (UI)** | Jetpack Compose + Material 3 (BOM 2026.02.01) |
| **Android SDK** | Compile SDK: 37, Min SDK: 24, Target SDK: 37 |
| **Build Tool** | Gradle 9.6.0 + Android Gradle Plugin 9.5.0 |
| **Kiến trúc** | Phân tách UI Screens, Data Model, Components & Utilities rõ ràng |
| **Testing** | JUnit 4 (Unit testing cho chuẩn hóa không dấu và kiểm tra trùng lặp) |

---

## 📂 Cấu trúc thư mục dự án

```
phone-mobile/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/phone_mobile/
│   │   │   │   ├── data/model/
│   │   │   │   │   └── Contact.kt              # Data class Contact, CallLog & 20+ liên hệ mẫu
│   │   │   │   ├── ui/
│   │   │   │   │   ├── components/
│   │   │   │   │   │   └── PhoneBottomBar.kt   # Thanh điều hướng 4 tab dùng chung
│   │   │   │   │   ├── screens/
│   │   │   │   │   │   ├── call/
│   │   │   │   │   │   │   ├── CallHistoryScreen.kt    # Màn hình Lịch sử cuộc gọi
│   │   │   │   │   │   │   └── InCallScreen.kt         # Màn hình Cuộc gọi toàn màn hình chuẩn iPhone
│   │   │   │   │   │   ├── contact/
│   │   │   │   │   │   │   ├── ContactListScreen.kt    # Màn hình Danh bạ & Danh bạ trống
│   │   │   │   │   │   │   ├── ContactDetailScreen.kt  # Chi tiết liên hệ & Hộp thoại xóa
│   │   │   │   │   │   │   └── AddEditContactScreen.kt # Thêm mới / Sửa liên hệ & Bắt lỗi
│   │   │   │   │   │   ├── favorite/
│   │   │   │   │   │   │   └── FavoritesScreen.kt      # Màn hình Danh bạ yêu thích
│   │   │   │   │   │   ├── keypad/
│   │   │   │   │   │   │   └── KeypadScreen.kt         # Bàn phím số (Dialpad)
│   │   │   │   │   │   └── more/
│   │   │   │   │   │       └── ShareCardScreen.kt      # Màn hình Chia sẻ danh thiếp & Quét mã QR
│   │   │   │   │   ├── theme/
│   │   │   │   │   │   ├── Color.kt        # Hệ thống màu thiết kế Figma
│   │   │   │   │   │   ├── Theme.kt        # Cấu hình MaterialTheme
│   │   │   │   │   │   └── Type.kt         # Kiểu chữ
│   │   │   │   │   └── MainApp.kt          # Điều hướng toàn bộ màn hình ứng dụng
│   │   │   │   └── MainActivity.kt         # Điểm khởi chạy Activity
│   │   │   └── res/
│   │   │       └── drawable/               # Toàn bộ vector icon XML trích xuất từ Figma
│   │   └── test/java/com/example/phone_mobile/
│   │       └── ExampleUnitTest.kt          # Unit test tìm kiếm không dấu & kiểm tra trùng số
│   └── build.gradle.kts
├── gradle/
│   └── libs.versions.toml
├── build.gradle.kts
├── settings.gradle.kts
├── README.md                               # Giới thiệu dự án
└── BUILD_AND_RUN.md                        # Hướng dẫn chi tiết cách build và chạy
```

---

## 📖 Hướng dẫn Build & Chạy

Vui lòng xem tài liệu hướng dẫn đầy đủ tại: **[BUILD_AND_RUN.md](./BUILD_AND_RUN.md)**.
