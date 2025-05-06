package io.halvorsen;

import io.halvorsen.model.Order;
import io.temporal.activity.ActivityInterface;

@ActivityInterface
public interface CheckForFraudActivities {

    public String callFraudService(Order o);
    public String getFinalDecision(Order o);

}
