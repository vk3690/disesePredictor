package com.disease.predictor.service;


import com.disease.predictor.entity.Users;
import com.disease.predictor.jwt.JWTService;
import com.disease.predictor.repo.UserDetailsRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {

    @Autowired
    private JWTService jwtService;


    @Autowired
    private UserDetailsRepo repo;


    @Autowired
    private KafkaProducer kafkaProducer;


}