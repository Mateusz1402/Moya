package com.ms.backend.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.ms.backend.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
