package com.disease.predictor.service;

import com.disease.predictor.dto.GetScore;
import com.disease.predictor.entity.*;
import com.disease.predictor.entity.dto.ParameterCondition;
import com.disease.predictor.exception.CustomException;
import com.disease.predictor.repo.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CalculateScore {
    @Autowired
    UserDetailsRepo userDetailsRepo;
    @Autowired
    private ModelAndDiseaseMapperRepo modelAndDiseaseMapperRepo;
    @Autowired
    private DiseaseMasterRepo diseaseMasterRepo;
    @Autowired
    private ModelMasterRepo modelMasterRepo;

    @Autowired
    private ParameterMasterRepo parameterMasterRepo;
    @Autowired
    private UserHealthDataRepo userHealthDataRepo;
    @Autowired
    private UserHealthReportLogsRepo userHealthReportLogsRepo;

    private static final Logger logger = LoggerFactory.getLogger("CalculateScore");


    public ResponseEntity<Object> getScore(GetScore score) {
        try {
            Users users = userDetailsRepo.findByUsername(score.getUsername());

            if (users != null) {
                DiseaseMaster diseaseMaster = diseaseMasterRepo.findByDiseaseName(score.getDisease());
                if (diseaseMaster != null) {
                    return this.calculateDiseaseScore(diseaseMaster, users);
                } else {
                    throw new CustomException("Disease Not found to calculate score ::" + score.getDisease());
                }
            } else {
                throw new CustomException("User Not found ::" + score.username);
            }

        } catch (Exception e) {
            logger.error("Error {}", e.getLocalizedMessage());
            return new ResponseEntity<>("Error " + e.getLocalizedMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    protected ResponseEntity<Object> calculateDiseaseScore(DiseaseMaster diseaseMaster, Users users) {
        try {
            List<ModelAndDiseaseMapper> modelAndDiseaseMapperList = new ArrayList<>();

                modelAndDiseaseMapperList.addAll(modelAndDiseaseMapperRepo.findByDiseaseMasterId(diseaseMaster));

            List<ModelAndDiseaseMapper> modelMaster = modelAndDiseaseMapperList.stream().filter(i -> i.getModelMasterId().isActive()).collect(Collectors.toList());
            if (!modelMaster.isEmpty()) {
                List<ParameterMaster> parameterMastersList = parameterMasterRepo.findByModelId(modelMaster.get(0).getModelMasterId());

                List<UserHealthData> userHealthDataList = userHealthDataRepo.findByUserId(users);
                Map<String, List<Integer>> valuesCollected = new HashMap<>();
                for (UserHealthData userHealthData : userHealthDataList) {
                    this.collectUserDataParameters(parameterMastersList, userHealthData, valuesCollected);
                }
                logger.info("user data colleted ");
                Double score = this.getScorebasedOnConditionOnCollectedData(valuesCollected, parameterMastersList);
                Map<String, Object> response = new HashMap<>();
                response.put("calculateScore", score);
                response.put("Ideal Score", modelMaster.get(0).getDiseaseMasterId().getIdealScore());
                response.put("Poor Score", modelMaster.get(0).getDiseaseMasterId().getPoorScore());
                logger.info("Response sent to customer {} , {}", response, users.getUsername());
                this.logUserReport(response,users,modelMaster);

                return new ResponseEntity<>(response.toString(), HttpStatus.OK);
            } else {
                throw new CustomException("No model active for the disease ");
            }
        } catch (Exception e) {
            logger.error("Error to calculate score of user :: {}", e.getMessage());
            return new ResponseEntity<>("Error to calculate score of user :: " + e.getLocalizedMessage(), HttpStatus.CONFLICT);

        }
    }

    private void logUserReport(Map<String, Object> response, Users users, List<ModelAndDiseaseMapper> modelMaster) {
        try {
            UserHealthReportLogs userHealthReportLogs=userHealthReportLogsRepo.findTop1ByUserId(users);
            if(userHealthReportLogs==null) {
                userHealthReportLogs = new UserHealthReportLogs(1L,response.toString(),users, modelMaster.get(0), new Date());

            }else{
                userHealthReportLogs = new UserHealthReportLogs(userHealthReportLogs.getId(),response.toString(),users, modelMaster.get(0), new Date());
            }
            userHealthReportLogsRepo.saveAndFlush(userHealthReportLogs);
        }catch (Exception e)
        {
            logger.error("Error to log user health report :: {}",e.getMessage());
        }
    }

    private void collectUserDataParameters(List<ParameterMaster> parameterMasterList, UserHealthData userHealthData, Map<String, List<Integer>> valuesCollected) throws Exception {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            for (ParameterMaster parameterMaster : parameterMasterList) {
                Map<String, Integer> healthParameters = objectMapper.readValue(userHealthData.getHealthParameters(), new TypeReference<Map<String, Integer>>() {
                });
                if (!valuesCollected.containsKey(parameterMaster.getParameterName())) {
                    valuesCollected.put(parameterMaster.getParameterName(), new ArrayList<>());
                }
                valuesCollected.get(parameterMaster.getParameterName()).add(healthParameters.get(parameterMaster.getParameterName()));
            }

        } catch (Exception e) {
            logger.error("Error to collect the user data :: {}", e.getMessage());
            throw new CustomException("Error to collect the user data :: " + e.getMessage());
        }
    }

    private Double getScorebasedOnConditionOnCollectedData(Map<String, List<Integer>> valuesCollected, List<ParameterMaster> parameterMasterList) throws CustomException {

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Integer> averageOfDataParams = new HashMap<>();

            for (ParameterMaster parameterMaster : parameterMasterList) {
                ParameterCondition parameterCondition = objectMapper.readValue(parameterMaster.getParameterCondition(), ParameterCondition.class);

                switch (parameterCondition.getConditionName()) {
                    case "GREATER_THAN": {
                        this.evaluteConditionGreaterThan(valuesCollected, parameterMaster, parameterCondition, averageOfDataParams);
                        break;
                    }
                    case "AVERAGE_OF_TIME_PERIOD": {
                        this.evaluateConditionAverageOfTheTimeperiod(valuesCollected, parameterMaster, parameterCondition, averageOfDataParams);
                        break;
                    }
                    case "BOOLEAN": {
                        this.evaluteConditionOnBoolean(valuesCollected, parameterMaster, parameterCondition, averageOfDataParams);
                        break;
                    }
                }
            }
            logger.info("individual health param values{}", averageOfDataParams);
          return   this.normalizeValues(averageOfDataParams);
//            return averageOfDataParams.values().stream().mapToInt(Integer::intValue).sum() / parameterMasterList.size();
        } catch (Exception e) {
            logger.error("Error to calculate data parameters based on condition :: {}", e.getMessage());
            throw new CustomException("Error to calculate data parameters based on condition :: " + e.getLocalizedMessage());
        }
    }

    private Double normalizeValues(Map<String, Integer> averageOfDataParams) throws CustomException {
        try {
            List<Integer> sortedvalues = averageOfDataParams.values().stream().sorted().collect(Collectors.toList());
            int score = 0;
            for (String key : averageOfDataParams.keySet()) {
                int numerator=(averageOfDataParams.get(key) - sortedvalues.get(0));
                int denominator=(sortedvalues.get(sortedvalues.size() - 1) - sortedvalues.get(0));
                score=score+((numerator/denominator)*100);
            }
             return (double) (score/sortedvalues.size());

        } catch (Exception e) {
            logger.error("Error to normalize data :: {}", e.getMessage());
            throw new CustomException("Error to normalize data ::"+ e.getMessage());
        }
    }

    private void evaluteConditionOnBoolean(Map<String, List<Integer>> valuesCollected, ParameterMaster parameterMaster, ParameterCondition parameterCondition, Map<String, Integer> averageOfDataParams) {
        try {
            Integer average = 0;
            if (Objects.equals(parameterCondition.getCondition().get("value"), valuesCollected.get(parameterMaster.getParameterName()).get(valuesCollected.get(parameterMaster.getParameterName()).size() - 1))) {
                average = parameterMaster.getAddScore();
            } else {
                average = -1 * parameterMaster.getDeductScore();
            }
            averageOfDataParams.put(parameterMaster.getParameterName(), average);

        } catch (Exception e) {
            logger.info("Error to find average of data param :: {} ,{}", parameterMaster.getParameterName(), e.getMessage());
        }
    }

    private void evaluateConditionAverageOfTheTimeperiod(Map<String, List<Integer>> valuesCollected, ParameterMaster parameterMaster, ParameterCondition parameterCondition,
                                                         Map<String, Integer> averageOfDataParams) {

        try {
            Integer average;
            if (valuesCollected.get(parameterMaster.getParameterName()).size() < parameterCondition.getCondition().get("timePeriod")) {
                average = -1 * parameterMaster.getDeductScore();
            } else {
                Integer averageDataInaTimePeriod = this.getSumOfArrayList(valuesCollected.get(parameterMaster.getParameterName())) / parameterCondition.getCondition().get("timePeriod");
                if (averageDataInaTimePeriod >= parameterCondition.getCondition().get("values")) {
                    average = parameterMaster.getDeductScore();
                } else {
                    average = -1 * parameterMaster.getDeductScore();

                }
            }
            averageOfDataParams.put(parameterMaster.getParameterName(), average);
        } catch (Exception e) {
            logger.info("Error to find average of data param :: {}", parameterMaster.getParameterName());

        }
    }

    private void evaluteConditionGreaterThan(Map<String, List<Integer>> valuesCollected, ParameterMaster parameterMaster, ParameterCondition parameterCondition,
                                             Map<String, Integer> averageOfDataParams) {
        try {
            Integer average = 0;
            for (Integer data : valuesCollected.get(parameterMaster.getParameterName())) {
                if (parameterCondition.getCondition().get("value") < data) {
                    average = average + parameterMaster.getAddScore();
                } else {
                    average = average + (-1 * parameterMaster.getDeductScore());
                }
            }
            averageOfDataParams.put(parameterMaster.getParameterName(), average);

        } catch (Exception e) {
            logger.info("Error to find average of data param :: {}", parameterMaster.getParameterName());
        }
    }

    private Integer getSumOfArrayList(List<Integer> listToAdd) {
        if (!listToAdd.isEmpty()) {
            return listToAdd.stream().mapToInt(Integer::intValue).sum();
        } else {
            return 0;
        }
    }

    public ResponseEntity<Object> getuserScoreReport(String username) {

        try{
            Users users = userDetailsRepo.findByUsername(username);
            if (users != null) {
               List<UserHealthReportLogs> userHealthReportLogs=userHealthReportLogsRepo.findByUserId(users);
               if(!userHealthReportLogs.isEmpty()){
                   return new ResponseEntity<>(userHealthReportLogs,HttpStatus.OK);
               }else{
                   throw new CustomException("No records of health report found");
               }

            } else {
                throw new CustomException("User Not found ::" + username);
            }
        }catch (Exception e)
        {
            logger.error("Error to fetch user report :; {}",e.getMessage());
            return new ResponseEntity<>("Error to fetch user report ",HttpStatus.OK);
        }
    }
}
