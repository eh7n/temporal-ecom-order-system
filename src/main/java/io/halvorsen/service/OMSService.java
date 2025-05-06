package io.halvorsen.service;

import io.halvorsen.model.Order;

public interface OMSService {

    public boolean submitOrderForFulfillment(Order o);

}
