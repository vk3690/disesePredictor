package com.disease.predictor.serviceTests;

import com.disease.predictor.dto.GetScore;
import com.disease.predictor.entity.*;
import com.disease.predictor.entity.dto.ParameterCondition;
import com.disease.predictor.repo.*;
import com.disease.predictor.service.CalculateScore;
import com.disease.predictor.service.DataInjection;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class CalculateScoreTest {


    @Mock
    UserDetailsRepo userDetailsRepo;
    @Mock
    private ModelAndDiseaseMapperRepo modelAndDiseaseMapperRepo;
    @Mock
    private DiseaseMasterRepo diseaseMasterRepo;
    @Mock
    private ModelMasterRepo modelMasterRepo;

    @Mock
    private ParameterMasterRepo parameterMasterRepo;
    @Mock
    private UserHealthDataRepo userHealthDataRepo;
    private static final Logger logger = LoggerFactory.getLogger("CalculateScore");

    @InjectMocks
    private CalculateScore calculateScore1;
    @Autowired
    private DataInjection dataInjection;

    @BeforeEach
    void injectData() {
        try {

            dataInjection.insertData();
        } catch (Exception e) {
            logger.error("{}", e.getMessage());
        }
    }

    private List<UserHealthData> adduserData(Users users) throws JsonProcessingException {


        ObjectMapper objectMapper = new ObjectMapper();
        List<UserHealthData> userHealthData = new ArrayList<>();

        Map<String, Integer> healthData = new HashMap<>();
        healthData.put("hscrp", 8);
        healthData.put("Kidney Disease in family", 1);
        healthData.put("Physical Activity", 30);
        healthData.put("Diabetes in family", 0);
        healthData.put("hba1c", 40);
        healthData.put("Hyper tension in family", 0);
        healthData.put("Triglycerides", 300);
        healthData.put("Cholesterol in family", 0);
        healthData.put("Blood Pressure", 120);

        userHealthData.add(new UserHealthData((long) 1L, objectMapper.writeValueAsString(healthData), users, new Date()));
return userHealthData;

    }


public List<ParameterMaster> addDiseaseMaster(ModelMaster modelMaster) throws JsonProcessingException {

    ObjectMapper objectMapper = new ObjectMapper();
    List<ParameterMaster> diabetesParameterMasterList = Arrays.asList(new ParameterMaster(1L, "hba1c",
                    objectMapper.writeValueAsString(new ParameterCondition("GREATER_THAN", new HashMap<>() {{
                        put("value", 6);
                    }})), 20, 20, modelMaster),
            new ParameterMaster(2L, "Diabetes in family",
                    objectMapper.writeValueAsString(new ParameterCondition("BOOLEAN", new HashMap<>() {{
                        put("value", 1);
                    }})), 25, 25, modelMaster)
            , new ParameterMaster(3L, "Physical Activity",
                    objectMapper.writeValueAsString(new ParameterCondition("AVERAGE_OF_TIME_PERIOD", new HashMap<>() {{
                        put("value", 45);
                        put("timePeriod", 90);
                    }})), 20, 20, modelMaster),
            new ParameterMaster(4L, "Kidney Disease in family",
                    objectMapper.writeValueAsString(new ParameterCondition("BOOLEAN", new HashMap<>() {{
                        put("value", 1);
                    }})), 25, 25, modelMaster),
            new ParameterMaster(5L, "hscrp",
                    objectMapper.writeValueAsString(new ParameterCondition("GREATER_THAN", new HashMap<>() {{
                        put("value", 2);
                    }})), 20, 20, modelMaster));
    return diabetesParameterMasterList;
}


@Test
void TestGetScore() throws Exception {

    GetScore getScore = new GetScore("vikas", "CVD");
    Users users = new Users(1L, "vikas", "1234");
    Mockito.when(userDetailsRepo.findByUsername(any())).thenReturn(users);
    DiseaseMaster diseaseMaster = new DiseaseMaster(1L, "Diabetes", 75, 65, 55);
    Mockito.when(diseaseMasterRepo.findByDiseaseName("CVD")).thenReturn(diseaseMaster);
    ModelMaster modelMaster = new ModelMaster(1L, "Diabetes Model One", Boolean.TRUE, new Date(), new Date());
    List<ParameterMaster> parameterMasterList = this.addDiseaseMaster(modelMaster);
    List<ModelAndDiseaseMapper> modelAndDiseaseMapperList = Arrays.asList(new ModelAndDiseaseMapper(1L, diseaseMaster, parameterMasterList.get(0).getModelId()));
    Mockito.when(modelAndDiseaseMapperRepo.findByDiseaseMasterId(diseaseMaster)).thenReturn(modelAndDiseaseMapperList);
    Mockito.when(parameterMasterRepo.findByModelId(parameterMasterList.get(0).getModelId())).thenReturn(parameterMasterList);
    Mockito.when(userHealthDataRepo.findByUserId(users)).thenReturn(this.adduserData(users));
    ResponseEntity<Object> response = calculateScore1.getScore(getScore);

    assertEquals("{Ideal Score=75, calculateScore=20.0, Poor Score=55}", response.getBody());
}


    @Test
    void TestGetScoreUserNotFound() throws Exception {

        GetScore getScore = new GetScore("vikas", "CVD");
        Users users = new Users(1L, "vikas", "1234");
        Mockito.when(userDetailsRepo.findByUsername("vikas")).thenReturn(null);
        DiseaseMaster diseaseMaster = new DiseaseMaster(1L, "Diabetes", 75, 65, 55);
//        Mockito.when(diseaseMasterRepo.findByDiseaseName("CVD")).thenReturn(diseaseMaster);

        ModelAndDiseaseMapper modelAndDiseaseMapper = new ModelAndDiseaseMapper(1L);
        ResponseEntity<Object> response = calculateScore1.getScore(getScore);
        assertEquals("Error User Not found ::vikas", response.getBody());
    }


    @Test
    void TestGetScoreDiseaseNotFound() throws Exception {

        GetScore getScore = new GetScore("vikas", "CVD");
        Users users = new Users(1L, "vikas", "1234");
        Mockito.when(userDetailsRepo.findByUsername("vikas")).thenReturn(users);
        DiseaseMaster diseaseMaster = new DiseaseMaster(1L, "Diabetes", 75, 65, 55);
        Mockito.when(diseaseMasterRepo.findByDiseaseName("CVD")).thenReturn(null);

        ModelAndDiseaseMapper modelAndDiseaseMapper = new ModelAndDiseaseMapper(1L);
        ResponseEntity<Object> response = calculateScore1.getScore(getScore);
        assertEquals("Error Disease Not found to calculate score ::CVD", response.getBody());
    }



    @Test
    void TestGetScoreModelNotActive() throws Exception {

        GetScore getScore = new GetScore("vikas", "CVD");
        Users users = new Users(1L, "vikas", "1234");
        Mockito.when(userDetailsRepo.findByUsername(any())).thenReturn(users);
        DiseaseMaster diseaseMaster = new DiseaseMaster(1L, "Diabetes", 75, 65, 55);
        Mockito.when(diseaseMasterRepo.findByDiseaseName("CVD")).thenReturn(diseaseMaster);
        ModelMaster modelMaster = new ModelMaster(1L, "Diabetes Model One", Boolean.FALSE, new Date(), new Date());

        List<ParameterMaster> parameterMasterList = this.addDiseaseMaster(modelMaster);
        List<ModelAndDiseaseMapper> modelAndDiseaseMapperList = Arrays.asList(new ModelAndDiseaseMapper(1L, diseaseMaster, parameterMasterList.get(0).getModelId()));
        Mockito.when(modelAndDiseaseMapperRepo.findByDiseaseMasterId(diseaseMaster)).thenReturn(modelAndDiseaseMapperList);
        Mockito.when(parameterMasterRepo.findByModelId(parameterMasterList.get(0).getModelId())).thenReturn(parameterMasterList);
        Mockito.when(userHealthDataRepo.findByUserId(users)).thenReturn(this.adduserData(users));
        ResponseEntity<Object> response = calculateScore1.getScore(getScore);
        assertEquals("Error to calculate score of user :: No model active for the disease", response.getBody().toString());
    }
@Test
void TestGetScore1() throws Exception {

    GetScore getScore = new GetScore("vikas", "CVD");
    Users users = new Users(1L, "vikas", "1234");
    Mockito.when(userDetailsRepo.findByUsername(any())).thenReturn(users);
    DiseaseMaster diseaseMaster = new DiseaseMaster(1L, "Diabetes", 75, 65, 55);
    Mockito.when(diseaseMasterRepo.findByDiseaseName("CVD")).thenReturn(diseaseMaster);

    ModelAndDiseaseMapper modelAndDiseaseMapper = new ModelAndDiseaseMapper(1L);
    ResponseEntity<Object> response = calculateScore1.getScore(getScore);
    assertEquals(409, response.getStatusCodeValue());

}
}
