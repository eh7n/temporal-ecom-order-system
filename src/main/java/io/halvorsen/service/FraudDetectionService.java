package io.halvorsen.service;

import io.halvorsen.model.Order;

public interface FraudDetectionService {

    public String checkFraud(Order o);
    public String getFinalDecision(Order o);

}
