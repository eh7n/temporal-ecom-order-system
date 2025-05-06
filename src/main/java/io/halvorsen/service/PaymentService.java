package io.halvorsen.service;

import io.halvorsen.model.PaymentInfo;

public interface PaymentService {

    public String getPreAuthorization(String orderId, PaymentInfo info, Double amount);
    public String reverseAuthorization(String authorizationReference);
    public String capturePayment(String authorizationReference, Double settledAmount);

}
