package com.clinic.nhom12.service;

import com.clinic.nhom12.dto.AuthResponse;
import com.clinic.nhom12.dto.LoginRequest;
import com.clinic.nhom12.dto.RegisterRequest;
import com.clinic.nhom12.entity.Role;
import com.clinic.nhom12.entity.User;
import com.clinic.nhom12.entity.Specialty;
import com.clinic.nhom12.repository.SpecialtyRepository;
import com.clinic.nhom12.repository.RoleRepository;
import com.clinic.nhom12.repository.UserRepository;
import com.clinic.nhom12.dto.DoctorDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private SpecialtyRepository specialtyRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; // ✅ Inject BCryptPasswordEncoder

    // --- CHỨC NĂNG ĐĂNG KÝ BỆNH NHÂN ---
    public String registerPatient(RegisterRequest request) {
        // 1. Kiểm tra trùng username
        if (userRepository.existsByUsername(request.getUsername())) {
            return "Lỗi: Tên đăng nhập này đã được sử dụng!";
        }

        // 2. Kiểm tra trùng email (nếu có)
        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            if (userRepository.existsByEmail(request.getEmail())) {
                return "Lỗi: Địa chỉ email này đã được đăng ký!";
            }
        }

        // 3. Kiểm tra trùng số điện thoại (nếu có)
        if (request.getPhone() != null && !request.getPhone().isBlank()) {
            if (userRepository.existsByPhone(request.getPhone())) {
                return "Lỗi: Số điện thoại này đã được đăng ký!";
            }
        }

        // 4. Tạo đối tượng User mới
        User newUser = new User();
        newUser.setUsername(request.getUsername());
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        newUser.setFullName(request.getFullName());
        newUser.setEmail(request.getEmail());
        newUser.setPhone(request.getPhone());
        newUser.setActive(true);

        // 3. Tìm quyền ROLE_PATIENT trong Database để gán cho người dùng này
        Optional<Role> roleOptional = roleRepository.findByName("ROLE_PATIENT");
        if (roleOptional.isPresent()) {
            newUser.setRoles(Collections.singleton(roleOptional.get()));
        } else {
            return "Lỗi: Hệ thống chưa có quyền ROLE_PATIENT.";
        }

        // 4. Lưu vào Database
        userRepository.save(newUser);
        return "Đăng ký tài khoản Bệnh nhân thành công!";
    }

    // --- CHỨC NĂNG ĐĂNG NHẬP ---
    // Hỗ trợ đăng nhập bằng username HOẶC email
    public AuthResponse login(LoginRequest request) {
        AuthResponse response = new AuthResponse();
        String identifier = request.getUsername(); // field này chứa username hoặc email

        // Tìm theo username trước
        Optional<User> userOptional = userRepository.findByUsername(identifier);

        // Nếu không thấy, thử tìm theo email
        if (userOptional.isEmpty()) {
            userOptional = userRepository.findByEmail(identifier);
        }

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            if (passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                response.setMessage("SUCCESS");
                response.setUserId(user.getId()); // Gán ID
                response.setFullName(user.getFullName());
                response.setAvatarUrl(user.getAvatarUrl()); // Lấy ảnh đại diện

                // Lấy role đầu tiên của user
                if (!user.getRoles().isEmpty()) {
                    response.setRole(user.getRoles().iterator().next().getName());
                } else {
                    response.setRole("ROLE_PATIENT");
                }
                return response;
            } else {
                response.setMessage("Lỗi: Sai mật khẩu!");
                return response;
            }
        } else {
            response.setMessage("Lỗi: Tài khoản không tồn tại!");
            return response;
        }
    }

    // --- LẤY DANH SÁCH BÁC SĨ ---
    public List<User> getDoctors() {
        return userRepository.findByRoles_Name("ROLE_DOCTOR");
    }

    public Page<User> getDoctors(String keyword, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return userRepository.findByRoles_Name("ROLE_DOCTOR", pageable);
        } else {
            return userRepository.findByFullNameContainingIgnoreCaseAndRoles_Name(keyword, "ROLE_DOCTOR", pageable);
        }
    }

    public Optional<User> getDoctorById(Long id) {
        return userRepository.findById(id).filter(u -> u.getRoles().stream().anyMatch(r -> r.getName().equals("ROLE_DOCTOR")));
    }

    // --- TẠO MỚI BÁC SĨ ---
    public String createDoctor(DoctorDTO dto) {
        if (userRepository.existsByUsername(dto.getUsername())) return "Lỗi: Tên đăng nhập này đã được sử dụng!";
        if (dto.getEmail() != null && !dto.getEmail().isBlank() && userRepository.existsByEmail(dto.getEmail())) {
            return "Lỗi: Email này đã được đăng ký!";
        }
        if (dto.getPhone() != null && !dto.getPhone().isBlank() && userRepository.existsByPhone(dto.getPhone())) {
            return "Lỗi: Số điện thoại này đã được đăng ký!";
        }

        User doctor = new User();
        doctor.setUsername(dto.getUsername());
        doctor.setPassword(passwordEncoder.encode(dto.getPassword()));
        doctor.setFullName(dto.getFullName());
        doctor.setEmail(dto.getEmail());
        doctor.setPhone(dto.getPhone());
        doctor.setAvatarUrl(dto.getAvatarUrl());
        doctor.setDoctorInfo(dto.getDoctorInfo());
        doctor.setActive(dto.isActive());

        if (dto.getSpecialtyId() != null) {
            Optional<Specialty> specOpt = specialtyRepository.findById(dto.getSpecialtyId());
            specOpt.ifPresent(doctor::setSpecialty);
        }

        Optional<Role> roleOpt = roleRepository.findByName("ROLE_DOCTOR");
        if (roleOpt.isPresent()) {
            doctor.setRoles(Collections.singleton(roleOpt.get()));
        }

        userRepository.save(doctor);
        return "SUCCESS";
    }

    // --- CẬP NHẬT BÁC SĨ ---
    public String updateDoctor(Long id, DoctorDTO dto) {
        Optional<User> docOpt = userRepository.findById(id);
        if (docOpt.isEmpty()) return "Lỗi: Không tìm thấy bác sĩ!";

        User doctor = docOpt.get();

        // Cập nhật thông tin cơ bản
        doctor.setFullName(dto.getFullName());
        doctor.setEmail(dto.getEmail());
        doctor.setPhone(dto.getPhone());
        doctor.setAvatarUrl(dto.getAvatarUrl());
        doctor.setDoctorInfo(dto.getDoctorInfo());
        doctor.setActive(dto.isActive());

        // Nếu có nhập mật khẩu mới thì cập nhật
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            doctor.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        if (dto.getSpecialtyId() != null) {
            Optional<Specialty> specOpt = specialtyRepository.findById(dto.getSpecialtyId());
            specOpt.ifPresent(doctor::setSpecialty);
        }

        userRepository.save(doctor);
        return "SUCCESS";
    }

    // --- XÓA CỨNG/MỀM BÁC SĨ ---
    public boolean deleteDoctor(Long id) {
        Optional<User> opt = userRepository.findById(id);
        if (opt.isPresent()) {
            User user = opt.get();
            user.setActive(false);
            userRepository.save(user);
            return true;
        }
        return false;
    }

    // --- BẬT/TẮT TRẠNG THÁI BÁC SĨ ---
    public boolean toggleDoctorStatus(Long id) {
        Optional<User> opt = userRepository.findById(id);
        if (opt.isPresent()) {
            User user = opt.get();
            user.setActive(!user.isActive());
            userRepository.save(user);
            return true;
        }
        return false;
    }

    // --- CHỨC NĂNG XỬ LÝ ĐĂNG NHẬP GOOGLE ---
    public User processOAuthPostLogin(String email, String name) {
        Optional<User> existUser = userRepository.findByEmail(email);
        if (existUser.isEmpty()) {
            User newUser = new User();
            newUser.setUsername(email); // Dùng email làm username mặc định
            newUser.setEmail(email);
            newUser.setFullName(name);
            newUser.setPassword(passwordEncoder.encode("OAUTH2_DUMMY_PASSWORD")); // Mật khẩu ảo cho tài khoản Google
            newUser.setActive(true);
            
            // Mặc định gán người dùng từ Google là bệnh nhân theo yêu cầu "chỉ user"
            Optional<Role> roleOptional = roleRepository.findByName("ROLE_PATIENT");
            if (roleOptional.isPresent()) {
                newUser.setRoles(Collections.singleton(roleOptional.get()));
            }
            return userRepository.save(newUser);
        }
        return existUser.get();
    }
}