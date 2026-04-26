package com.wcl.user.repository;

import com.wcl.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    // Spring Data JPA is magic. Just by naming the method "findByEmail",
    // it will automatically write the "SELECT * FROM users WHERE email = ?" SQL for us!
    Optional<User> findByEmail(String email);

}