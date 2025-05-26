package com.disease.predictor.service;

import com.disease.predictor.entity.*;
import com.disease.predictor.entity.dto.ParameterCondition;
import com.disease.predictor.repo.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.IntStream;

@Service
public class DataInjection {

    @Autowired
    private DiseaseMasterRepo diseaseMasterRepo;
    @Autowired
    public ParameterMasterRepo parameterMasterRepo;

    @Autowired
    public UserDetailsRepo userDetailsRepo;

    @Autowired
    private ModelMasterRepo modelMasterRepo;
    @Autowired
    private ModelAndDiseaseMapperRepo modelAndDiseaseMapperRepo;

    @Autowired
    private UserHealthDataRepo userHealthDataRepo;


    public void insertData() throws JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();

        //add model config
        ModelMaster modelMaster=new ModelMaster(1L,"Diabetes Model One",Boolean.TRUE,new Date(),new Date());
        ModelMaster CVDmodelMaster=new ModelMaster(2L,"CVD Model One",Boolean.TRUE,new Date(),new Date());

        modelMaster=modelMasterRepo.saveAndFlush(modelMaster);
        CVDmodelMaster=modelMasterRepo.saveAndFlush(CVDmodelMaster);


        DiseaseMaster diseaseMaster = new DiseaseMaster(1L, "Diabetes", 75, 65, 55);
        List<ParameterMaster> diabetesParameterMasterList = Arrays.asList(new ParameterMaster(1L, "hba1c",
                        objectMapper.writeValueAsString(new ParameterCondition("GREATER_THAN", new HashMap<>() {{
                            put("value", 6);
                        }})), 20, 20,modelMaster),
                new ParameterMaster(2L, "Diabetes in family",
                        objectMapper.writeValueAsString(new ParameterCondition("BOOLEAN", new HashMap<>() {{
                            put("value", 1);
                        }})), 25, 25,modelMaster)
                , new ParameterMaster(3L, "Physical Activity",
                        objectMapper.writeValueAsString(new ParameterCondition("AVERAGE_OF_TIME_PERIOD", new HashMap<>() {{
                            put("value", 45);
                            put("timePeriod", 90);
                        }})), 20, 20,modelMaster),
                new ParameterMaster(4L, "Kidney Disease in family",
                        objectMapper.writeValueAsString(new ParameterCondition("BOOLEAN", new HashMap<>() {{
                            put("value", 1);
                        }})), 25, 25,modelMaster),
                new ParameterMaster(5L, "hscrp",
                        objectMapper.writeValueAsString(new ParameterCondition("GREATER_THAN", new HashMap<>() {{
                            put("value", 2);
                        }})), 20, 20,modelMaster));

        DiseaseMaster diseaseMasterCVD = new DiseaseMaster(2L, "CVD", 70, 60, 55);
        List<ParameterMaster> CVDParameterMasterList = Arrays.asList(new ParameterMaster(6L, "hscrp",
                        objectMapper.writeValueAsString(new ParameterCondition("GREATER_THAN", new HashMap<>() {{
                            put("value", 3);
                        }})), 20, 20,CVDmodelMaster),

                new ParameterMaster(7L, "Cholesterol in family",
                        objectMapper.writeValueAsString(new ParameterCondition("BOOLEAN", new HashMap<>() {{
                            put("value", 0);
                        }})), 10, 10,CVDmodelMaster),

                new ParameterMaster(8L, "Hyper tension in family",
                        objectMapper.writeValueAsString(new ParameterCondition("BOOLEAN", new HashMap<>() {{
                            put("value", 0);
                        }})), 10, 10,CVDmodelMaster)


                , new ParameterMaster(9L, "Physical Activity",
                        objectMapper.writeValueAsString(new ParameterCondition("AVERAGE_OF_TIME_PERIOD", new HashMap<>() {{
                            put("value", 30);
                            put("timePeriod", 90);
                        }})), 20, 20,CVDmodelMaster),

                new ParameterMaster(10L, "Triglycerides",
                        objectMapper.writeValueAsString(new ParameterCondition("GREATER_THAN", new HashMap<>() {{
                            put("value", 200);
                        }})), 20, 20,CVDmodelMaster)
                ,
                new ParameterMaster(24L, "Blood Pressure",
                        objectMapper.writeValueAsString(new ParameterCondition("GREATER_THAN", new HashMap<>() {{
                            put("value", 110);
                        }})), 12, 12,CVDmodelMaster));

        diseaseMaster = diseaseMasterRepo.save(diseaseMaster);
        diabetesParameterMasterList = parameterMasterRepo.saveAllAndFlush(diabetesParameterMasterList);
        CVDParameterMasterList = parameterMasterRepo.saveAllAndFlush(CVDParameterMasterList);
        diseaseMasterCVD = diseaseMasterRepo.saveAndFlush(diseaseMasterCVD);
       List<ModelAndDiseaseMapper> modelAndDiseaseMapperList=Arrays.asList(new ModelAndDiseaseMapper(1L,diseaseMaster,modelMaster),
               new ModelAndDiseaseMapper(2L,diseaseMasterCVD,CVDmodelMaster)  );
       modelAndDiseaseMapperRepo.saveAllAndFlush(modelAndDiseaseMapperList);
        this.addOneMoreModel(diseaseMaster,diseaseMasterCVD);
        this.adduserData();
    }

    private void adduserData() {

        List<Users> users = Arrays.asList(new Users(1L, "vikas", "1234"),
                new Users(2L, "Ram", "12345"));
       users= userDetailsRepo.saveAllAndFlush(users);

        ObjectMapper objectMapper=new ObjectMapper();
        int min = 1;
        int max = 50;
        for (Users users1 : users) {
            Random rn = new Random();

            List<UserHealthData> userHealthData = new ArrayList<>();

            userHealthData.clear();

            IntStream.range(min, max).forEach(i -> {
                Map<String, Integer> healthData = new HashMap<>();
                healthData.put("hscrp", rn.nextInt(10 - 1 + 1) + 1);
                healthData.put("Kidney Disease in family", (Integer) rn.ints(0, 2)
                        .findFirst()
                        .getAsInt());
                healthData.put("Physical Activity", rn.nextInt(1440 - 1 + 1) + 1);
                healthData.put("Diabetes in family", rn.ints(0, 2)
                        .findFirst()
                        .getAsInt());
                healthData.put("hba1c", rn.nextInt(1440 - 1 + 1) + 1);
                healthData.put("Hyper tension in family", rn.ints(0, 2)
                        .findFirst()
                        .getAsInt());
                healthData.put("Triglycerides", rn.nextInt(1000 - 1 + 1) + 1);

                healthData.put("Cholesterol in family", rn.ints(0, 2)
                        .findFirst()
                        .getAsInt());

                healthData.put("Blood Pressure", rn.nextInt(250 - 1 + 1) + 1);
                long MILLIS_IN_1_DAYS = (1000L * 60 * 60 * 24);

                try {
                    userHealthData.add(new UserHealthData((long) i, objectMapper.writeValueAsString(healthData), users1, new Date(new Date().getTime() - (MILLIS_IN_1_DAYS * 100) - (MILLIS_IN_1_DAYS * i))));

                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            });
            min = userHealthData.size() + 1;
            max = userHealthData.size() + 50;
            userHealthDataRepo.saveAllAndFlush(userHealthData);
        }
    }




    private void addOneMoreModel(DiseaseMaster diseaseMaster,DiseaseMaster diseaseMasterCVD) throws JsonProcessingException {


        ModelMaster modelMaster=new ModelMaster(3L,"Diabetes Model two",Boolean.FALSE,new Date(),new Date());
        ModelMaster CVdmodelMaster=new ModelMaster(4L,"CVD Model two",Boolean.FALSE,new Date(),new Date());

        modelMaster=modelMasterRepo.saveAndFlush(modelMaster);
        CVdmodelMaster=modelMasterRepo.saveAndFlush(CVdmodelMaster);
        ObjectMapper objectMapper = new ObjectMapper();
        List<ParameterMaster> diabetesParameterMasterList = Arrays.asList(new ParameterMaster(12L, "hba1c",
                        objectMapper.writeValueAsString(new ParameterCondition("GREATER_THAN", new HashMap<>() {{
                            put("value", 6);
                        }})), 25, 20,modelMaster),
                new ParameterMaster(13L, "Diabetes in family",
                        objectMapper.writeValueAsString(new ParameterCondition("BOOLEAN", new HashMap<>() {{
                            put("value", 1);
                        }})), 15, 15,modelMaster)
                , new ParameterMaster(14L, "Physical Activity",
                        objectMapper.writeValueAsString(new ParameterCondition("AVERAGE_OF_TIME_PERIOD", new HashMap<>() {{
                            put("value", 45);
                            put("timePeriod", 90);
                        }})), 30, 20,modelMaster),
                new ParameterMaster(15L, "Kidney Disease in family",
                        objectMapper.writeValueAsString(new ParameterCondition("BOOLEAN", new HashMap<>() {{
                            put("value", 1);
                        }})), 15, 20,modelMaster),
                new ParameterMaster(16L, "hscrp",
                        objectMapper.writeValueAsString(new ParameterCondition("GREATER_THAN", new HashMap<>() {{
                            put("value", 2);
                        }})), 10, 10,modelMaster));

        List<ParameterMaster> CVDParameterMasterList = Arrays.asList(new ParameterMaster(17L, "hscrp",
                        objectMapper.writeValueAsString(new ParameterCondition("GREATER_THAN", new HashMap<>() {{
                            put("value", 3);
                        }})), 20, 20,CVdmodelMaster),

                new ParameterMaster(18L, "Cholesterol in family",
                        objectMapper.writeValueAsString(new ParameterCondition("BOOLEAN", new HashMap<>() {{
                            put("value", 0);
                        }})), 10, 10,CVdmodelMaster),

                new ParameterMaster(19L, "Hyper tension in family",
                        objectMapper.writeValueAsString(new ParameterCondition("BOOLEAN", new HashMap<>() {{
                            put("value", 0);
                        }})), 15, 15,CVdmodelMaster)


                , new ParameterMaster(20L, "Physical Activity",
                        objectMapper.writeValueAsString(new ParameterCondition("AVERAGE_OF_TIME_PERIOD", new HashMap<>() {{
                            put("value", 30);
                            put("timePeriod", 90);
                        }})), 10, 20,CVdmodelMaster),

                new ParameterMaster(21L, "Triglycerides",
                        objectMapper.writeValueAsString(new ParameterCondition("GREATER_THAN", new HashMap<>() {{
                            put("value", 200);
                        }})), 30, 30,CVdmodelMaster),



        new ParameterMaster(23L, "Blood Pressure",
                objectMapper.writeValueAsString(new ParameterCondition("GREATER_THAN", new HashMap<>() {{
                    put("value", 110);
                }})), 20, 20,CVdmodelMaster));

        CVDParameterMasterList = parameterMasterRepo.saveAllAndFlush(CVDParameterMasterList);
//        diseaseMasterCVD = diseaseMasterRepo.saveAndFlush(diseaseMasterCVD);
//        diseaseMaster = diseaseMasterRepo.save(diseaseMaster);
        diabetesParameterMasterList = parameterMasterRepo.saveAllAndFlush(diabetesParameterMasterList);
        List<ModelAndDiseaseMapper> modelAndDiseaseMapperList=Arrays.asList(new ModelAndDiseaseMapper(3L,diseaseMaster,modelMaster),
                new ModelAndDiseaseMapper(4L,diseaseMasterCVD,CVdmodelMaster)  );
        modelAndDiseaseMapperRepo.saveAllAndFlush(modelAndDiseaseMapperList);



    }
}

