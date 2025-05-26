package com.disease.predictor.repo;

import com.disease.predictor.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDetailsRepo  extends JpaRepository<Users,Long> {

    Users findByUsername(String username);
}
