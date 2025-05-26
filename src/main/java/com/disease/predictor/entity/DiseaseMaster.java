package com.disease.predictor.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Data
public class DiseaseMaster {
    @Id
    public Long id;
    private String diseaseName;
    private int idealScore;
    private int averageScore;
    private int poorScore;


    public DiseaseMaster() {
    }

    public DiseaseMaster(Long id, String diseaseName, int idealScore, int averageScore, int poorScore) {
        this.id = id;
        this.diseaseName = diseaseName;
        this.idealScore = idealScore;
        this.averageScore = averageScore;
        this.poorScore = poorScore;
    }
}
