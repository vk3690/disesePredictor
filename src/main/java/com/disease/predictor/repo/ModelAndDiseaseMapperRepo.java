package com.disease.predictor.repo;

import com.disease.predictor.entity.DiseaseMaster;
import com.disease.predictor.entity.ModelAndDiseaseMapper;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ModelAndDiseaseMapperRepo extends JpaRepository<ModelAndDiseaseMapper,Long> {
    List<ModelAndDiseaseMapper> findByDiseaseMasterId(DiseaseMaster diseaseMaster);

}
