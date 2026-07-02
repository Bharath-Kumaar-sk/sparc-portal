package com.sparc.sparcscholarshipbackend.repository;

import com.sparc.sparcscholarshipbackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    //for login purposes. Tries to find email by the provided email in frontend.
    Optional<User> findByEmail(String email);
}