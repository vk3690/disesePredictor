package com.disease.predictor.entity.dto;

import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class ParameterCondition {

    private String conditionName;
    private Map<String,Integer> condition;


    public ParameterCondition() {
    }

    public ParameterCondition(String conditionName, Map<String,Integer> condition) {
        this.condition=  condition;
        this.conditionName=conditionName;
    }
}
