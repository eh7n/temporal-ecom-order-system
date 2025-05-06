package io.halvorsen.service.impl;

import java.time.Duration;
import java.time.Instant;

import io.halvorsen.Constants;
import io.halvorsen.model.Customer;
import io.halvorsen.model.Order;
import io.halvorsen.service.FraudDetectionService;

public class FraudDetectionServiceStub implements FraudDetectionService{

    Instant startupTime = Instant.now();

    private FraudDetectionServiceStub () {

    }

    public String checkFraud(Order o){
        Customer customer = o.getCustomer();
        System.out.printf("FraudDetectionService :: Checking fraud for customer %s %n", customer.getName());

        // To test the hold logic, let's look at the customer name
        if(customer.getName().contains("Hold")){
            return Constants.FRAUD_STATUS_HOLD;
        }else if(customer.getName().contains("Reject")){
            return Constants.FRAUD_STATUS_REJECT;
        }else{
            return Constants.FRAUD_STATUS_ACCEPT;
        }
    }

    public String getFinalDecision(Order o){
        // Simulate something that takes 5 minutes to return true
        // In reality, this might take hours or days
        // This is a polling model, but in a event model we probably should use Signals (?)
        Instant now = Instant.now();
        if(Duration.between(startupTime, now).toMinutes() > 5){
            System.out.println("FraudDetectionService :: Fraud service returns Accept!");
            return Constants.FRAUD_STATUS_ACCEPT;
        }else{
            System.out.println("FraudDetectionService :: Fraud service returns Pending, keep waiting");
            return Constants.FRAUD_STATUS_PENDING;
        }
    }

    public static FraudDetectionService create(){
        return new FraudDetectionServiceStub();
    }
}