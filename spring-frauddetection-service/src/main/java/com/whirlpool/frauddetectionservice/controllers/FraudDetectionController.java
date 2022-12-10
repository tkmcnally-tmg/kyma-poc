package com.whirlpool.frauddetectionservice.controllers;

import com.whirlpool.frauddetectionservice.FraudDetectionSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FraudDetectionController {

    private final FraudDetectionSettings fraudDetectionSettings;

    @Autowired
    private Environment environment;

    public FraudDetectionController(FraudDetectionSettings fraudDetectionSettings) {
        this.fraudDetectionSettings = fraudDetectionSettings;
    }

    @GetMapping ("/frauddetection")
    public String frauddetection() {
        return environment.getProperty("FRAUDDETECTION_ISENABLED");
    }


}
