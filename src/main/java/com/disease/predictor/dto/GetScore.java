package com.disease.predictor.dto;

import lombok.Data;

@Data
public class GetScore {

    public String username;
    private String disease;

    public GetScore(String username, String disease) {
        this.username = username;
        this.disease = disease;
    }
}
