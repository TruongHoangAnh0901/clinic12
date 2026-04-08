package com.clinic.nhom12.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ChatbotService {

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    // Thiết lập tính cách và định hướng thông tin cho AI
    private final String SYSTEM_PROMPT = 
        "Bạn là một trợ lý ảo tư vấn y tế xuất sắc của 'Phòng Khám Nhóm 12'. " +
        "Thông tin hệ thống: Phòng khám làm việc từ 07:00 đến 20:00 (Thứ 2 đến Chủ Nhật). " +
        "Hotline: 1900 1234. Địa chỉ: HUTECH, Khu Công Nghệ Cao, TP.HCM. " +
        "Dịch vụ gồm: Khám tổng quát, Nha khoa, Tim mạch, Xét nghiệm máu, Chụp X-Quang. " +
        "Nhiệm vụ của bạn: Tư vấn thân thiện, ngắn gọn (dưới 100 chữ), lịch sự. Nếu người dùng hỏi triệu chứng, " +
        "hãy khuyên họ nên đến gặp bác sĩ trực tiếp thay vì tự chữa. Không trả lời các câu hỏi không liên quan đến y tế hoặc phòng khám.";

    public String getChatResponse(String userMessage) {
        String url = apiUrl + apiKey;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Chuẩn bị Payload JSON cho Gemini
        String fullPrompt = SYSTEM_PROMPT + "\n\nBệnh nhân hỏi: " + userMessage + "\nTrợ lý trả lời:";
        
        Map<String, Object> requestBody = new HashMap<>();
        List<Map<String, Object>> contentsList = new ArrayList<>();
        Map<String, Object> contentMap = new HashMap<>();
        List<Map<String, Object>> partsList = new ArrayList<>();
        Map<String, Object> textMap = new HashMap<>();

        textMap.put("text", fullPrompt);
        partsList.add(textMap);
        contentMap.put("parts", partsList);
        contentsList.add(contentMap);
        requestBody.put("contents", contentsList);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);
            Map responseBody = response.getBody();
            if (responseBody != null && responseBody.containsKey("candidates")) {
                List<Map> candidates = (List<Map>) responseBody.get("candidates");
                if (!candidates.isEmpty()) {
                    Map content = (Map) candidates.get(0).get("content");
                    List<Map> parts = (List<Map>) content.get("parts");
                    return (String) parts.get(0).get("text");
                }
            }
        } catch (Exception e) {
            System.err.println("Lỗi gọi Gemini API: " + e.getMessage());
            return "Tôi đang gặp sự cố kết nối nội bộ. Bạn vui lòng gọi hotline 1900 1234 để được hỗ trợ nhé!";
        }
        
        return "Xin lỗi, tôi vẫn chưa hiểu rõ ý của bạn.";
    }
}
