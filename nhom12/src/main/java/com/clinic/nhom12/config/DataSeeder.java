package com.clinic.nhom12.config;

import com.clinic.nhom12.entity.MedicalService;
import com.clinic.nhom12.entity.Medicine;
import com.clinic.nhom12.entity.Role;
import com.clinic.nhom12.entity.Specialty;
import com.clinic.nhom12.entity.User;
import com.clinic.nhom12.repository.MedicalServiceRepository;
import com.clinic.nhom12.repository.MedicineRepository;
import com.clinic.nhom12.repository.RoleRepository;
import com.clinic.nhom12.repository.SpecialtyRepository;
import com.clinic.nhom12.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private SpecialtyRepository specialtyRepository;

    @Autowired
    private MedicineRepository medicineRepository;

    @Autowired
    private MedicalServiceRepository medicalServiceRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; // ✅ Inject BCrypt để hash mật khẩu mặc định

    @Override
    public void run(String... args) throws Exception {
        // 1. Tự động tạo 3 Quyền (Roles) mặc định vào Database nếu chưa có
        if (roleRepository.findByName("ROLE_PATIENT").isEmpty()) {
            roleRepository.save(new Role(null, "ROLE_PATIENT", "Bệnh nhân"));
        }
        if (roleRepository.findByName("ROLE_DOCTOR").isEmpty()) {
            roleRepository.save(new Role(null, "ROLE_DOCTOR", "Bác sĩ"));
        }
        if (roleRepository.findByName("ROLE_ADMIN").isEmpty()) {
            roleRepository.save(new Role(null, "ROLE_ADMIN", "Quản trị viên"));
        }

        // 2. Tạo sẵn các Chuyên Khoa mẫu
        Specialty khoatim = specialtyRepository.findByName("Khoa Tim Mạch");
        if (khoatim == null) {
            khoatim = new Specialty(null, "Khoa Tim Mạch", "Chuyên chẩn đoán và điều trị các bệnh lý liên quan đến tim và mạch máu.", "https://images.unsplash.com/photo-1579684385127-1ef15d508118?w=500", true);
            khoatim = specialtyRepository.save(khoatim);
        }
        
        Specialty khoatamly = specialtyRepository.findByName("Khoa Tâm Lý");
        if (khoatamly == null) {
            khoatamly = new Specialty(null, "Khoa Tâm Lý", "Tư vấn và điều trị các rối loạn tâm lý học và tâm thần.", "https://images.unsplash.com/photo-1527613426441-4da17471b66d?w=500", true);
            khoatamly = specialtyRepository.save(khoatamly);
        }

        Specialty khoamat = specialtyRepository.findByName("Khoa Nhãn Khoa");
        if (khoamat == null) {
            khoamat = new Specialty(null, "Khoa Nhãn Khoa", "Chăm sóc, phẫu thuật và điều trị các bệnh lý về mắt.", "https://images.unsplash.com/photo-1549484949-ad77140e6cd9?w=500", true);
            khoamat = specialtyRepository.save(khoamat);
        }

        // 3. Tự động tạo Tài khoản Bác sĩ mặc định và gán cho Chuyên khoa
        if (!userRepository.existsByUsername("bacsi123@gmail.com")) {
            User doctor1 = new User();
            doctor1.setUsername("bacsi123@gmail.com");
            doctor1.setPassword(passwordEncoder.encode("bacsi123"));
            doctor1.setFullName("Nguyễn Trường Lâm");
            doctor1.setAvatarUrl("https://images.unsplash.com/photo-1622253692010-333f2da6031d?w=300");
            doctor1.setDoctorInfo("Tiến sĩ Y Khoa. Hơn 15 năm kinh nghiệm trong phẫu thuật nội soi tim mạch. Từng công tác tại Bệnh viện Mount Elizabeth, Singapore.");
            doctor1.setSpecialty(khoatim);
            doctor1.setActive(true);
            doctor1.setRoles(new HashSet<>(Collections.singletonList(roleRepository.findByName("ROLE_DOCTOR").get())));
            userRepository.save(doctor1);

            User doctor2 = new User();
            doctor2.setUsername("lyly@gmail.com");
            doctor2.setPassword(passwordEncoder.encode("bacsi123"));
            doctor2.setFullName("Lý Thảo Ly");
            doctor2.setAvatarUrl("https://images.unsplash.com/photo-1594824419918-62a26569ec18?w=300");
            doctor2.setDoctorInfo("Thạc sĩ Tâm lý học lâm sàng. Chuyên gia tư vấn trị liệu trầm cảm và giảm stress cộng đồng.");
            doctor2.setSpecialty(khoatamly);
            doctor2.setActive(true);
            doctor2.setRoles(new HashSet<>(Collections.singletonList(roleRepository.findByName("ROLE_DOCTOR").get())));
            userRepository.save(doctor2);

            User doctor3 = new User();
            doctor3.setUsername("drdavid@gmail.com");
            doctor3.setPassword(passwordEncoder.encode("bacsi123"));
            doctor3.setFullName("David Vu");
            doctor3.setAvatarUrl("https://images.unsplash.com/photo-1612349317150-e413f6a5b16d?w=300");
            doctor3.setDoctorInfo("BS. Nhãn Khoa. Các chứng nhận quốc tế về mổ Lasik và Relex Smile. Đã thực hiện hơn 10,000 ca phẫu thuật cận thị thành công.");
            doctor3.setSpecialty(khoamat);
            doctor3.setActive(true);
            doctor3.setRoles(new HashSet<>(Collections.singletonList(roleRepository.findByName("ROLE_DOCTOR").get())));
            userRepository.save(doctor3);

            System.out.println("Đã khởi tạo tài khoản Bác sĩ mẫu!");
        }

        // 4. Tự động tạo Tài khoản Admin mặc định
        if (!userRepository.existsByUsername("admin123@gmail.com")) {
            User admin = new User();
            admin.setUsername("admin123@gmail.com");
            // ✅ Lỗi 4 đã sửa: Hash mật khẩu mặc định bằng BCrypt
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setFullName("Quản trị viên Hệ thống");
            admin.setActive(true);

            Role adminRole = roleRepository.findByName("ROLE_ADMIN").get();
            admin.setRoles(new HashSet<>(Collections.singletonList(adminRole)));

            userRepository.save(admin);
            System.out.println("Đã khởi tạo tài khoản Admin mặc định!");
        }

        // 5. Khởi tạo Thuốc mẫu (nếu chưa có)
        if (medicineRepository.count() == 0) {
            medicineRepository.saveAll(Arrays.asList(
                    new Medicine(null, "Paracetamol 500mg", "L01-2025", 100, LocalDate.of(2025, 12, 31), 5000.0, "/images/thuoc/paracetamol.jpg", true),
                    new Medicine(null, "Amoxicillin 500mg", "L02-2026", 50, LocalDate.of(2026, 6, 30), 12000.0, "/images/thuoc/amoxicillin.jpg", true),
                    new Medicine(null, "Vitamin C 1000mg", "L03-2025", 200, LocalDate.of(2025, 10, 15), 3000.0, "/images/thuoc/vitaminc.jpg", true),
                    new Medicine(null, "Ibuprofen 400mg", "L04-2026", 80, LocalDate.of(2026, 2, 28), 8000.0, "/images/thuoc/ibuprofen.jpg", true)
            ));
            System.out.println("Đã tạo dữ liệu Thuốc mẫu.");
        }

        // 6. Khởi tạo Dịch vụ khám bệnh mẫu (nếu chưa có)
        if (medicalServiceRepository.count() == 0) {
            medicalServiceRepository.saveAll(Arrays.asList(
                    new MedicalService(null, "Khám tổng quát", "Khám lâm sàng toàn diện, đo huyết áp, nhịp tim", 150000.0, "/images/dichvu/kham-tong-quat.jpg", true),
                    new MedicalService(null, "Siêu âm 4D", "Siêu âm thai nhi 4D sắc nét", 300000.0, "/images/dichvu/sieu-am-4d.jpg", true),
                    new MedicalService(null, "Xét nghiệm máu cơ bản", "Bao gồm công thức máu, đường huyết, mỡ máu", 200000.0, "/images/dichvu/xet-nghiem-mau.jpg", true),
                    new MedicalService(null, "Khám chuyên khoa Tim Mạch", "Điện tâm đồ, khám lâm sàng với bác sĩ chuyên khoa", 250000.0, "/images/dichvu/tim-mach.jpg", true)
            ));
            System.out.println("Đã tạo dữ liệu Dịch vụ y tế mẫu.");
        }
    }
}