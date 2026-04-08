package com.clinic.nhom12.service;

import com.clinic.nhom12.entity.Appointment;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Async // Chạy ngầm, không làm chậm quá trình phản hồi API duyệt lịch
    public void sendAppointmentConfirmationEmail(String toEmail, Appointment appointment) {
        if (toEmail == null || toEmail.trim().isEmpty()) {
            return;
        }

        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("Phòng Khám Nhóm 12 - Thông Báo Xác Nhận Lịch Hẹn");

            String serviceName = appointment.getMedicalService() != null ? appointment.getMedicalService().getName() : "Khám tổng quát";
            String doctorName = appointment.getDoctor() != null ? appointment.getDoctor().getFullName() : "Sẽ phân công sau";

            String htmlContent = "<h2>Xin chào " + appointment.getPatientName() + ",</h2>"
                    + "<p>Lịch hẹn khám bệnh của bạn đã được <b>XÁC NHẬN</b> thành công.</p>"
                    + "<table border='1' cellpadding='10' style='border-collapse: collapse;'>"
                    + "<tr><td><b>Ngày khám:</b></td><td>" + appointment.getAppointmentDate() + "</td></tr>"
                    + "<tr><td><b>Giờ khám:</b></td><td><span style='color: blue; font-weight: bold;'>" + appointment.getAppointmentTime() + "</span></td></tr>"
                    + "<tr><td><b>Dịch vụ:</b></td><td>" + serviceName + "</td></tr>"
                    + "<tr><td><b>Bác sĩ phụ trách:</b></td><td>" + doctorName + "</td></tr>"
                    + "</table>"
                    + "<p>Vui lòng đến phòng khám đúng giờ để được phục vụ tốt nhất.</p>"
                    + "<p><i>Cảm ơn bạn đã tin tưởng Phòng Khám Nhóm 12!</i></p>";

            helper.setText(htmlContent, true); // true = HTML

            javaMailSender.send(message);
            System.out.println("✅ Đã gửi email xác nhận lịch hẹn tới: " + toEmail);

        } catch (MessagingException e) {
            System.err.println("❌ Lỗi khi gửi email: " + e.getMessage());
        }
    }
}
