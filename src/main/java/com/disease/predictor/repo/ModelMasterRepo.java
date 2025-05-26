package com.disease.predictor.repo;

import com.disease.predictor.entity.ModelMaster;
import lombok.Data;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ModelMasterRepo extends JpaRepository<ModelMaster,Long> {
}
