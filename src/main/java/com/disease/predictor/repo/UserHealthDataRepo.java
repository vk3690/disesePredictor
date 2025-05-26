package com.disease.predictor.repo;

import com.disease.predictor.entity.UserHealthData;
import com.disease.predictor.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserHealthDataRepo extends JpaRepository<UserHealthData,Long> {
    List<UserHealthData> findByUserId(Users users);
}
