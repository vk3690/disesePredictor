package com.disease.predictor.entity;

import com.disease.predictor.entity.dto.ParameterCondition;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.Data;


import javax.persistence.*;

@Entity
@Data
public class ParameterMaster {

    @Id
    private Long id;
    private String parameterName;
    @Column(columnDefinition = "text",name = "parameter_condition")
    private String parameterCondition;
    private Integer deductScore;
    private Integer addScore;
    @ManyToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    @JoinColumn(name = "model_id")
    private ModelMaster modelId;

    public ParameterMaster() {
    }

    public ParameterMaster(Long id, String parameterName, String parameterCondition,
                           Integer deductScore, Integer addScore,ModelMaster modelMaster) {
        this.id = id;
        this.parameterName = parameterName;
        this.parameterCondition = parameterCondition;
        this.deductScore=deductScore;
        this.addScore=addScore;
        this.modelId=modelMaster;
    }
}
