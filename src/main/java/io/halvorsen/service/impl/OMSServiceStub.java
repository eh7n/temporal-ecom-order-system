package io.halvorsen.service.impl;

import java.util.Random;

import io.halvorsen.model.Order;
import io.halvorsen.service.OMSService;

public class OMSServiceStub implements OMSService {

    private OMSServiceStub(){

    }

    @Override
    public boolean submitOrderForFulfillment(Order o){
        System.out.printf("OMSService :: Submitting order %s to OMS %n", o.getOrderId());

        // Simulate a faulty service by having this fail ~50% of the time
        Random random = new Random();
        int randomNumber = random.nextInt(10) + 1;
        if(randomNumber < 5){
            System.out.println("OMSService :: Oh noes! Something went wrong with the OMS API call.");
            return false;
        }
        return true;
    }


    public static OMSService create(){
        return new OMSServiceStub();
    }

}
