package com.example.PRJWEB.Repository;

import com.example.PRJWEB.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    boolean existsByUsername(String username);
    List<User> findByRolesContaining(String role);
    Optional<User>findByUsername(String username);
    Optional<User> findById(Long id);

    Optional<User> findByUid(String uid);
    Optional<User> findByEmail(String email);
}
