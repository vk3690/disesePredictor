package com.disease.predictor.repo;

import com.disease.predictor.entity.DiseaseMaster;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DiseaseMasterRepo extends JpaRepository<DiseaseMaster,Long> {
    DiseaseMaster findByDiseaseName(String disease);
}
