package vn.quahoa.flowershop.service;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import lombok.RequiredArgsConstructor;
import vn.quahoa.flowershop.dto.admin.AdminCreateRequest;
import vn.quahoa.flowershop.dto.admin.AdminLoginRequest;
import vn.quahoa.flowershop.dto.admin.AdminUpdateRequest;
import vn.quahoa.flowershop.exception.ResourceNotFoundException;
import vn.quahoa.flowershop.exception.ValidationException;
import vn.quahoa.flowershop.model.Admin;
import vn.quahoa.flowershop.repository.AdminRepository;

@Service
@RequiredArgsConstructor
public class AdminService {

  private final AdminRepository adminRepository;
  private final PasswordEncoder passwordEncoder;

  public Admin createAdmin(AdminCreateRequest request) {
    validateUsernameUnique(request.getUsername(), null);
    Admin admin = new Admin();
    admin.setUsername(request.getUsername());
    admin.setPassword(passwordEncoder.encode(request.getPassword()));
    return adminRepository.save(admin);
  }

  public List<Admin> findAll() {
    return adminRepository.findAll();
  }

  public Admin getById(Long id) {
    return adminRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Admin", id));
  }

  public Admin updateAdmin(Long id, AdminUpdateRequest request) {
    Admin admin = getById(id);
    validateUsernameUnique(request.getUsername(), id);
    admin.setUsername(request.getUsername());
    if (StringUtils.hasText(request.getPassword())) {
      admin.setPassword(passwordEncoder.encode(request.getPassword()));
    }
    return adminRepository.save(admin);
  }

  public void deleteAdmin(Long id) {
    Admin admin = getById(id);
    adminRepository.delete(admin);
  }

  public Admin authenticate(AdminLoginRequest request) {
    Admin admin = adminRepository.findByUsername(request.getUsername())
        .orElseThrow(() -> new ValidationException("credentials", "Invalid username or password"));

    if (!passwordEncoder.matches(request.getPassword(), admin.getPassword())) {
      throw new ValidationException("credentials", "Invalid username or password");
    }

    return admin;
  }

  private void validateUsernameUnique(String username, Long currentId) {
    adminRepository.findByUsername(username).ifPresent(existing -> {
      if (currentId == null || !existing.getId().equals(currentId)) {
        throw new ValidationException("username", "Username already exists");
      }
    });
  }
}
