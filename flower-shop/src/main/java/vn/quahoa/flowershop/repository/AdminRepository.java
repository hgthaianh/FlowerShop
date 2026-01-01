package vn.quahoa.flowershop.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import vn.quahoa.flowershop.model.Admin;

public interface AdminRepository extends JpaRepository<Admin, Long> {

  Optional<Admin> findByUsername(String username);

  boolean existsByUsername(String username);
}
