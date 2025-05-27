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

import javax.websocket.server.PathParam;

@RestController
public class UserController {

    @Autowired
    private UserDetailsRepo userDetailsRepo;
    @Autowired
    public DataInjection dataInjection;
    @Autowired
    private CalculateScore calculateScore;
    private static final Logger logger = LoggerFactory.getLogger("userController");



    @GetMapping("/injectData")
    public Object injectData() throws JsonProcessingException {
        try {
          return   dataInjection.insertData();
        }catch (Exception e)
        {
            logger.error("Error to inject data {}",e.getMessage());
            return new ResponseEntity<>("Data injection failed",HttpStatus.INTERNAL_SERVER_ERROR);
        }
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


    @GetMapping(value = "/getUserReport/{username}",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getUserReportOfScores(@PathVariable("username") String username) {
        try {
            return calculateScore.getuserScoreReport(username);
        }catch (Exception e)
        {
            logger.error("Error to get user health report {}",e.getMessage());
            return new ResponseEntity<>("Health report data not found",HttpStatus.NOT_FOUND);
        }
    }
}
