package com.whirlpool.frauddetectionservice.controllers;

import com.whirlpool.frauddetectionservice.order.request.ObjectFactory;
import com.whirlpool.frauddetectionservice.order.request.TransactionRequest;
import com.whirlpool.frauddetectionservice.order.response.TransactionResults;
import com.whirlpool.frauddetectionservice.service.DefaultAccertifyCallService;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.StringReader;

@RestController
public class FraudDetectionController {

    @Autowired
    private DefaultAccertifyCallService defaultAccertifyCallService;

    @RequestMapping(consumes = {MediaType.APPLICATION_XML_VALUE}, path = "/frauddetection", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<TransactionResults> frauddetection(@RequestBody String name) throws Exception {
        JAXBContext jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        StringReader reader = new StringReader(name);
        TransactionRequest transactionRequest = (TransactionRequest) unmarshaller.unmarshal(reader);
        ResponseEntity<TransactionResults> response = new ResponseEntity<>(defaultAccertifyCallService.callFraudCheckService(transactionRequest), HttpStatus.OK);
        response.getBody().setRulesetId("kyma-ruleset-test");
        return response;
    }
}
