package com.disease.predictor.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class ModelAndDiseaseMapper {

    @Id
    private Long mapperId;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "disease_master_id")
    private DiseaseMaster diseaseMasterId;
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "model_master_id")
    private ModelMaster modelMasterId;

    public ModelAndDiseaseMapper(Long mapperId) {
        this.mapperId = mapperId;
    }

    public ModelAndDiseaseMapper(Long mapperId, DiseaseMaster diseaseMasterId, ModelMaster modelMasterId) {
        this.mapperId = mapperId;
        this.diseaseMasterId = diseaseMasterId;
        this.modelMasterId = modelMasterId;
    }

    public ModelAndDiseaseMapper() {
    }
}
