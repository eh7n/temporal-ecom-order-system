package io.halvorsen.service.impl;

import java.util.UUID;

import io.halvorsen.model.PaymentInfo;
import io.halvorsen.service.PaymentService;

public class PaymentServiceStub implements PaymentService{

    private PaymentServiceStub(){

    }

    @Override
    public String getPreAuthorization(String orderId, PaymentInfo info, Double amount) {
        // The order key should be used to prevent multiple preauths to maintain idempodency 
        System.out.printf("PaymentService :: Authorizing payment of %f %n", amount);
        return "AUTH-" + UUID.randomUUID();
    }

    @Override
    public String capturePayment(String authorizationReference, Double settledAmount) {
        System.out.printf("PaymentService :: Capturing payment for auth %s of $%f %n", authorizationReference, settledAmount);
        return "success";
    }

    @Override
    public String reverseAuthorization(String authorizationReference) {
        System.out.printf("PaymentService :: Reversing auth %s %n", authorizationReference);
        return "success";
    }


    public static PaymentService create(){
        return new PaymentServiceStub();
    }

}
