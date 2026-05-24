package com.mycompany.flashcardapp.storage;

import com.mycompany.flashcardapp.model.Topic;
import java.util.List;

public class DefaultDataSeeder {

    public static void seed() {
        TopicDAO topicDAO = new TopicDAO();
        FlashcardDAO flashcardDAO = new FlashcardDAO();

        // Kiểm tra xem đã có dữ liệu global chưa (userId = 0)
        List<Topic> globalTopics = topicDAO.getAllTopics(0);
        if (globalTopics != null && !globalTopics.isEmpty()) {
            return; // Nếu đã có rồi thì nghỉ, không đẻ thêm
        }

        // Tạo 3 chủ đề mặc định
        topicDAO.addTopic(0, "Giao tiếp Tiếng Anh Cơ Bản");
        topicDAO.addTopic(0, "Từ vựng IELTS - Chủ đề Work");
        topicDAO.addTopic(0, "Từ vựng TOEIC - Office");

        List<Topic> newlyCreated = topicDAO.getAllTopics(0);
        if (newlyCreated.size() < 3) return; // fail safe

        int topic1 = newlyCreated.get(0).getId();
        int topic2 = newlyCreated.get(1).getId();
        int topic3 = newlyCreated.get(2).getId();

        // 50 từ vựng cho topic 1: Giao tiếp Cơ Bản
        String[] data1 = {
            "Hello|Xin chào", "Goodbye|Tạm biệt", "Thanks|Cảm ơn", "Sorry|Xin lỗi", "Please|Vui lòng",
            "Yes|Có, vâng", "No|Không", "Maybe|Có lẽ", "Help|Giúp đỡ", "Love|Tình yêu",
            "Family|Gia đình", "Friend|Bạn bè", "Food|Thức ăn", "Water|Nước", "Money|Tiền bạc",
            "Time|Thời gian", "Day|Ngày", "Night|Đêm", "Morning|Buổi sáng", "Evening|Buổi tối",
            "Happy|Vui vẻ", "Sad|Buồn bã", "Angry|Tức giận", "Tired|Mệt mỏi", "Hungry|Đói bụng",
            "Beautiful|Xinh đẹp", "Ugly|Xấu xí", "Big|To lớn", "Small|Nhỏ bé", "Fast|Nhanh",
            "Slow|Chậm chạp", "Hot|Nóng bức", "Cold|Lạnh giá", "Good|Tốt", "Bad|Xấu",
            "Easy|Dễ dàng", "Hard|Khó khăn", "Right|Đúng", "Wrong|Sai", "New|Mới",
            "Old|Cũ", "High|Cao", "Low|Thấp", "Long|Dài", "Short|Ngắn",
            "Buy|Mua", "Sell|Bán", "Open|Mở", "Close|Đóng", "Go|Đi"
        };
        for (String pair : data1) {
            String[] parts = pair.split("\\|");
            flashcardDAO.addFlashcard(0, parts[0], parts[1], topic1);
        }

        // 50 từ vựng cho topic 2: IELTS Work
        String[] data2 = {
            "Colleague|Đồng nghiệp", "Career|Sự nghiệp", "Promotion|Thăng tiến", "Salary|Mức lương", "Interview|Phỏng vấn",
            "Candidate|Ứng viên", "Employer|Người sử dụng lao động", "Employee|Người lao động", "Resume|Sơ yếu lý lịch", "Qualification|Bằng cấp",
            "Skill|Kỹ năng", "Experience|Kinh nghiệm", "Requirement|Yêu cầu", "Deadline|Hạn chót", "Project|Dự án",
            "Task|Nhiệm vụ", "Responsibility|Trách nhiệm", "Meeting|Cuộc họp", "Presentation|Bài thuyết trình", "Report|Báo cáo",
            "Strategy|Chiến lược", "Goal|Mục tiêu", "Achievement|Thành tựu", "Success|Thành công", "Failure|Thất bại",
            "Challenge|Thử thách", "Opportunity|Cơ hội", "Motivation|Động lực", "Productivity|Năng suất", "Efficiency|Hiệu quả",
            "Teamwork|Làm việc nhóm", "Leadership|Lãnh đạo", "Management|Quản lý", "Communication|Giao tiếp", "Negotiation|Đàm phán",
            "Conflict|Xung đột", "Resolution|Giải pháp", "Agreement|Thỏa thuận", "Contract|Hợp đồng", "Signature|Chữ ký",
            "Resign|Từ chức", "Retire|Nghỉ hưu", "Dismiss|Sa thải", "Hire|Thuê mướn", "Recruit|Tuyển dụng",
            "Workspace|Không gian làm việc", "Office|Văn phòng", "Equipment|Thiết bị", "Technology|Công nghệ", "Innovation|Sự đổi mới"
        };
        for (String pair : data2) {
            String[] parts = pair.split("\\|");
            flashcardDAO.addFlashcard(0, parts[0], parts[1], topic2);
        }

        // 50 từ vựng cho topic 3: TOEIC Office
        String[] data3 = {
            "Document|Tài liệu", "Folder|Thư mục", "File|Tệp tin", "Attachment|Tệp đính kèm", "Email|Thư điện tử",
            "Printer|Máy in", "Copier|Máy photocopy", "Scanner|Máy quét", "Shredder|Máy hủy tài liệu", "Stationery|Văn phòng phẩm",
            "Pen|Bút", "Pencil|Bút chì", "Paper|Giấy", "Notebook|Sổ tay", "Calendar|Lịch",
            "Schedule|Lịch trình", "Appointment|Cuộc hẹn", "Memo|Bản ghi nhớ", "Notice|Thông báo", "Bulletin board|Bảng thông báo",
            "Reception|Quầy lễ tân", "Lobby|Sảnh", "Elevator|Thang máy", "Stairs|Cầu thang", "Cafeteria|Quán ăn tự phục vụ",
            "Restroom|Nhà vệ sinh", "Conference room|Phòng họp", "Boardroom|Phòng họp ban quản trị", "Desk|Bàn làm việc", "Chair|Ghế",
            "Computer|Máy tính", "Monitor|Màn hình", "Keyboard|Bàn phím", "Mouse|Chuột", "Telephone|Điện thoại",
            "Extension|Số máy lẻ", "Message|Tin nhắn", "Voicemail|Hộp thư thoại", "Directory|Danh bạ", "Department|Phòng ban",
            "Manager|Người quản lý", "Supervisor|Người giám sát", "Assistant|Trợ lý", "Secretary|Thư ký", "Client|Khách hàng",
            "Customer|Khách hàng (mua hàng)", "Supplier|Nhà cung cấp", "Vendor|Người bán hàng", "Invoice|Hóa đơn", "Receipt|Biên lai"
        };
        for (String pair : data3) {
            String[] parts = pair.split("\\|");
            flashcardDAO.addFlashcard(0, parts[0], parts[1], topic3);
        }

        System.out.println("Default seed data created successfully.");
    }
}
