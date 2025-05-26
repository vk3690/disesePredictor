package com.disease.predictor.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

@Entity
@Data
public class ModelMaster {

    @Id
    private Long modelId;
    private String modelName;
    private boolean active;
    private Date lastActiveAt;
    private Date deactivatedAt;

    public ModelMaster() {
    }

    public ModelMaster(Long modelId, String modelName, boolean active, Date lastActiveAt, Date deactivatedAt) {
        this.modelId = modelId;
        this.modelName = modelName;
        this.active = active;
        this.lastActiveAt = lastActiveAt;
        this.deactivatedAt = deactivatedAt;
    }
}
