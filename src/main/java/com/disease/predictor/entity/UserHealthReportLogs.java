package com.disease.predictor.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
public class UserHealthReportLogs {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(columnDefinition = "text")
    private String scoreReport;
    @ManyToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private Users userId;
    @ManyToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    @JoinColumn(name = "model_disease_mapper_id")
    @JsonIgnore
    private ModelAndDiseaseMapper modelAndDiseaseMapperId;

    private Date createdAt;


    public UserHealthReportLogs() {
    }

    public UserHealthReportLogs(Users userId, ModelAndDiseaseMapper modelAndDiseaseMapperId, Date createdAt) {
        this.userId = userId;
        this.modelAndDiseaseMapperId = modelAndDiseaseMapperId;
        this.createdAt = createdAt;
    }

    public UserHealthReportLogs(Long id, String scoreReport,Users userId, ModelAndDiseaseMapper modelAndDiseaseMapperId, Date createdAt) {
        this.id = id;
        this.scoreReport=scoreReport;
        this.userId = userId;
        this.modelAndDiseaseMapperId = modelAndDiseaseMapperId;
        this.createdAt = createdAt;
    }
}
