package com.disease.predictor.repo;

import com.disease.predictor.entity.ModelAndDiseaseMapper;
import com.disease.predictor.entity.ModelMaster;
import com.disease.predictor.entity.ParameterMaster;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ParameterMasterRepo extends JpaRepository<ParameterMaster,Long> {
    List<ParameterMaster> findByModelId(ModelMaster modelMaster);
}
