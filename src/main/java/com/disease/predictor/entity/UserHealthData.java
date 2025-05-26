package com.disease.predictor.entity;

import com.disease.predictor.service.DataInjection;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
public class UserHealthData {

    @Id
    private Long id;
    private String healthParameters;
    @ManyToOne(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users userId;
    private Date createdAt;

    public UserHealthData() {
    }

    public UserHealthData(Long id, String healthParameters, Users userId,Date createdAt) {
        this.id = id;
        this.healthParameters = healthParameters;
        this.userId = userId;
        this.createdAt=createdAt;
    }
}
