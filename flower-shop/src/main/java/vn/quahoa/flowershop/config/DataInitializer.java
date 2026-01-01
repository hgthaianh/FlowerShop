package vn.quahoa.flowershop.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import vn.quahoa.flowershop.model.Admin;
import vn.quahoa.flowershop.repository.AdminRepository;

/**
 * Data Initializer - Tạo dữ liệu mặc định khi khởi động ứng dụng
 */
@Configuration
public class DataInitializer {
    
    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public CommandLineRunner initData(AdminRepository adminRepository) {
        return args -> {
            // Kiểm tra xem đã có admin nào chưa
            if (adminRepository.count() == 0) {
                // Tạo admin mặc định
                Admin defaultAdmin = new Admin();
                defaultAdmin.setUsername("admin");
                defaultAdmin.setPassword(passwordEncoder.encode("admin123"));
                
                adminRepository.save(defaultAdmin);
                
                logger.info("✅ Đã tạo tài khoản admin mặc định:");
                logger.info("   Username: admin");
                logger.info("   Password: admin123");
                logger.info("   ⚠️  Vui lòng đổi mật khẩu sau khi đăng nhập lần đầu!");
            } else {
                logger.info("ℹ️  Tài khoản admin đã tồn tại. Bỏ qua việc tạo admin mặc định.");
            }
        };
    }
}
