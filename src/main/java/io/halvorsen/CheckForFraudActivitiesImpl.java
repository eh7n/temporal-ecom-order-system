package io.halvorsen;

import io.halvorsen.model.Order;
import io.halvorsen.service.FraudDetectionService;
import io.halvorsen.service.impl.FraudDetectionServiceStub;

public class CheckForFraudActivitiesImpl implements CheckForFraudActivities{

    private FraudDetectionService fraudService = FraudDetectionServiceStub.create();

    @Override
    public String callFraudService(Order o) {
        System.out.println("CheckForFraudActivities :: Calling fraud service from activities");
        return fraudService.checkFraud(o);
    }

    @Override
    public String getFinalDecision(Order o) {
        return fraudService.getFinalDecision(o);
    }

}
