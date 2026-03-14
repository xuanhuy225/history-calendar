# History Calendar Android Project

Project Android mẫu cho app lịch lịch sử tiếng Việt.

## Đã có trong project
- Kotlin + Jetpack Compose
- Room Database
- Hilt DI
- Lịch tháng (month view) hiển thị ngày dương + ngày/tháng âm
- Màn hình thêm/sửa sự kiện
- Màn hình sự kiện hôm nay
- Notification gộp dạng list mỗi ngày
- Import/Export JSON bằng Storage Access Framework
- Seed dữ liệu mẫu

## Cần lưu ý
- Thuật toán âm lịch đã được tích hợp ở mức app MVP; bạn nên test thêm các mốc đặc biệt và tháng nhuận trước khi publish production.
- Exact alarm trên một số máy Android mới có thể cần người dùng cho phép thêm trong settings của máy.
- Notification hiện dùng icon hệ thống đơn giản.

## Cấu trúc chính
- `ui/navigation`: navigation Compose
- `ui/screen`: các màn hình
- `ui/viewmodel`: viewmodel cho từng màn hình
- `notification`: AlarmManager + BroadcastReceiver + grouped notification
- `data/backup`: import/export JSON
- `domain/LunarCalendarUtils.kt`: quy đổi dương -> âm


## Bổ sung ở bản này
- Widget màn hình chính: xem nhanh ngày hôm nay, âm lịch, số sự kiện và danh sách gọn.
- UI Material 3 đẹp hơn: hero card, calendar card bo góc, card sự kiện rõ hơn, form và settings đẹp hơn.
- Widget tự refresh khi mở app, sau khi sửa dữ liệu, sau khi import/export, sau khi reboot và sau lượt nhắc hằng ngày.
