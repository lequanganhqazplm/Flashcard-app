package com.mycompany.flashcardapp.storage;

import com.mycompany.flashcardapp.model.Topic;
import java.util.List;

public class DefaultDataSeeder {

    public static void seed() {
        TopicDAO topicDAO = new TopicDAO();
        FlashcardDAO flashcardDAO = new FlashcardDAO();

        // Kiểm tra xem đã có topic global nào chưa (userId = 0)
        List<Topic> globalTopics = topicDAO.getAllTopics(0);
        if (!globalTopics.isEmpty()) {
            return; // Nếu có rồi thì không sinh nữa để tránh lặp
        }

        System.out.println("Đang tự động sinh dữ liệu toàn cục (Global Seed Data)...");

        // Tạo 3 Topics
        topicDAO.addTopic(0, "Từ vựng IT cơ bản");
        topicDAO.addTopic(0, "Động từ bất quy tắc");
        topicDAO.addTopic(0, "Tiếng Anh giao tiếp");

        Topic topicIT = topicDAO.getTopicByName(0, "Từ vựng IT cơ bản");
        Topic topicVerbs = topicDAO.getTopicByName(0, "Động từ bất quy tắc");
        Topic topicDaily = topicDAO.getTopicByName(0, "Tiếng Anh giao tiếp");

        // 1. 50 Từ vựng IT
        String[] itWords = {
            "Algorithm|Thuật toán", "API|Giao diện lập trình ứng dụng", "Array|Mảng", "Bandwidth|Băng thông", "Binary|Nhị phân",
            "Bit|Đơn vị dữ liệu nhỏ nhất", "Bug|Lỗi phần mềm", "Byte|Đơn vị lưu trữ (8 bits)", "Cache|Bộ nhớ đệm", "Cloud|Đám mây",
            "Code|Mã nguồn", "Compiler|Trình biên dịch", "Database|Cơ sở dữ liệu", "Debug|Gỡ lỗi", "Deploy|Triển khai",
            "Domain|Tên miền", "Encryption|Mã hóa", "Endpoint|Điểm cuối (API)", "Ethernet|Mạng cục bộ", "Firewall|Tường lửa",
            "Framework|Khung phần mềm", "Frontend|Giao diện người dùng", "Backend|Hệ thống máy chủ", "Gateway|Cổng kết nối", "Hardware|Phần cứng",
            "Software|Phần mềm", "Hosting|Lưu trữ web", "HTML|Ngôn ngữ đánh dấu siêu văn bản", "HTTP|Giao thức truyền siêu văn bản", "IP|Địa chỉ mạng",
            "Iterate|Lặp lại", "JSON|Định dạng dữ liệu nhẹ", "Kernel|Nhân hệ điều hành", "Latency|Độ trễ", "Linux|Hệ điều hành mã nguồn mở",
            "Loop|Vòng lặp", "Malware|Phần mềm độc hại", "Network|Mạng lưới", "Node|Nút mạng", "OS|Hệ điều hành",
            "Packet|Gói dữ liệu", "Parameter|Tham số", "Password|Mật khẩu", "Phishing|Tấn công lừa đảo", "Ping|Kiểm tra kết nối",
            "Protocol|Giao thức", "Query|Truy vấn", "Repository|Kho chứa mã", "Router|Bộ định tuyến", "Server|Máy chủ"
        };
        for (String pair : itWords) {
            String[] parts = pair.split("\\|");
            flashcardDAO.addFlashcard(0, parts[0], parts[1], topicIT.getId());
        }

        // 2. 50 Động từ bất quy tắc
        String[] verbs = {
            "Be - Was/Were - Been|Thì, là, ở", "Beat - Beat - Beaten|Đánh", "Become - Became - Become|Trở nên", "Begin - Began - Begun|Bắt đầu", "Bite - Bit - Bitten|Cắn",
            "Blow - Blew - Blown|Thổi", "Break - Broke - Broken|Làm vỡ", "Bring - Brought - Brought|Mang đến", "Build - Built - Built|Xây dựng", "Buy - Bought - Bought|Mua",
            "Catch - Caught - Caught|Bắt lấy", "Choose - Chose - Chosen|Chọn lựa", "Come - Came - Come|Đến", "Cost - Cost - Cost|Có giá là", "Cut - Cut - Cut|Cắt",
            "Do - Did - Done|Làm", "Draw - Drew - Drawn|Vẽ", "Drink - Drank - Drunk|Uống", "Drive - Drove - Driven|Lái xe", "Eat - Ate - Eaten|Ăn",
            "Fall - Fell - Fallen|Ngã", "Feel - Felt - Felt|Cảm thấy", "Fight - Fought - Fought|Chiến đấu", "Find - Found - Found|Tìm thấy", "Fly - Flew - Flown|Bay",
            "Forget - Forgot - Forgotten|Quên", "Forgive - Forgave - Forgiven|Tha thứ", "Freeze - Froze - Frozen|Đóng băng", "Get - Got - Got/Gotten|Có được", "Give - Gave - Given|Cho",
            "Go - Went - Gone|Đi", "Grow - Grew - Grown|Mọc, trồng", "Have - Had - Had|Có", "Hear - Heard - Heard|Nghe", "Hide - Hid - Hidden|Giấu",
            "Hit - Hit - Hit|Đánh", "Hold - Held - Held|Giữ", "Hurt - Hurt - Hurt|Làm đau", "Keep - Kept - Kept|Giữ", "Know - Knew - Known|Biết",
            "Leave - Left - Left|Rời đi", "Lend - Lent - Lent|Cho mượn", "Let - Let - Let|Cho phép", "Lose - Lost - Lost|Làm mất", "Make - Made - Made|Làm, chế tạo",
            "Meet - Met - Met|Gặp gỡ", "Pay - Paid - Paid|Trả tiền", "Put - Put - Put|Đặt, để", "Read - Read - Read|Đọc", "Ride - Rode - Ridden|Cưỡi, lái"
        };
        for (String pair : verbs) {
            String[] parts = pair.split("\\|");
            flashcardDAO.addFlashcard(0, parts[0], parts[1], topicVerbs.getId());
        }

        // 3. 50 Tiếng Anh giao tiếp
        String[] daily = {
            "Hello|Xin chào", "Good morning|Chào buổi sáng", "Good afternoon|Chào buổi chiều", "Good evening|Chào buổi tối", "Good night|Chúc ngủ ngon",
            "How are you?|Bạn khỏe không?", "I'm fine, thank you|Tôi khỏe, cảm ơn", "And you?|Còn bạn thì sao?", "What is your name?|Bạn tên gì?", "My name is...|Tên tôi là...",
            "Nice to meet you|Rất vui được gặp bạn", "Where are you from?|Bạn từ đâu đến?", "I'm from...|Tôi đến từ...", "How old are you?|Bạn bao nhiêu tuổi?", "I am... years old|Tôi... tuổi",
            "Do you speak English?|Bạn có nói tiếng Anh không?", "Yes, a little|Có, một chút", "No, I don't|Không, tôi không", "I don't understand|Tôi không hiểu", "Please say that again|Vui lòng nói lại lần nữa",
            "Can you speak slower?|Bạn có thể nói chậm hơn được không?", "What does this mean?|Cái này có nghĩa là gì?", "How much is this?|Cái này giá bao nhiêu?", "I would like...|Tôi muốn...", "Excuse me|Xin lỗi (khi muốn hỏi/làm phiền)",
            "I'm sorry|Tôi xin lỗi", "Thank you|Cảm ơn", "You're welcome|Không có chi", "Yes, please|Vâng, vui lòng", "No, thank you|Không, cảm ơn",
            "Where is the restroom?|Nhà vệ sinh ở đâu?", "I need help|Tôi cần giúp đỡ", "Call the police|Gọi cảnh sát", "Call an ambulance|Gọi xe cứu thương", "I'm lost|Tôi bị lạc",
            "What time is it?|Bây giờ là mấy giờ?", "It is... o'clock|Bây giờ là... giờ", "Today is...|Hôm nay là...", "Yesterday|Hôm qua", "Tomorrow|Ngày mai",
            "Monday|Thứ Hai", "Tuesday|Thứ Ba", "Wednesday|Thứ Tư", "Thursday|Thứ Năm", "Friday|Thứ Sáu",
            "Saturday|Thứ Bảy", "Sunday|Chủ Nhật", "See you later|Hẹn gặp lại sau", "See you tomorrow|Hẹn gặp lại ngày mai", "Goodbye|Tạm biệt"
        };
        for (String pair : daily) {
            String[] parts = pair.split("\\|");
            flashcardDAO.addFlashcard(0, parts[0], parts[1], topicDaily.getId());
        }

        System.out.println("Khởi tạo xong Dữ liệu Toàn cục! (3 Topics, 150 Flashcards)");
    }
}
