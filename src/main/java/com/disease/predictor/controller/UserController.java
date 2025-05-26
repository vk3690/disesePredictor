package com.disease.predictor.controller;

import com.disease.predictor.dto.GetScore;
import com.disease.predictor.repo.UserDetailsRepo;
import com.disease.predictor.service.CalculateScore;
import com.disease.predictor.service.DataInjection;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    @Autowired
    private UserDetailsRepo userDetailsRepo;
    @Autowired
    public DataInjection dataInjection;
    @Autowired
    private CalculateScore calculateScore;
        private static final Logger logger = LoggerFactory.getLogger("service");



    @GetMapping("/injectData")
    public Object injectData() throws JsonProcessingException {
        dataInjection.insertData();
        return new ResponseEntity<>("Data injection successful", HttpStatus.OK);
    }

    @PostMapping(value = "/getScore",produces = MediaType.APPLICATION_JSON_VALUE)
    public Object getScoreOfTheDisease(@RequestBody GetScore getScore) {
        try {
           return calculateScore.getScore(getScore);
        }catch (Exception e)
        {
            logger.error("Error to get user details {}",e.getMessage());
            return new ResponseEntity<>("User not found",HttpStatus.NOT_FOUND);
        }
    }
}
