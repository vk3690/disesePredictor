package com.disease.predictor;

import com.disease.predictor.repo.DiseaseMasterRepo;
import com.disease.predictor.repo.ParameterMasterRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DiseasePredictorApplication {


	public static void main(String[] args) {
		SpringApplication.run(DiseasePredictorApplication.class, args);




	}

}
