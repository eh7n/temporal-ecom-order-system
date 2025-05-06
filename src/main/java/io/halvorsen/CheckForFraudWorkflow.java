package io.halvorsen;

import io.halvorsen.model.FinalFraudDecision;
import io.halvorsen.model.Order;
import io.temporal.workflow.SignalMethod;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface CheckForFraudWorkflow {

    @WorkflowMethod
    String checkFraud(Order o);

    @SignalMethod
    void receiveFinalDecision(FinalFraudDecision decision);
}
