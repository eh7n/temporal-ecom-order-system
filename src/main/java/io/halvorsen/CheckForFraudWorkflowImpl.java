package io.halvorsen;

import java.time.Duration;

import io.halvorsen.model.FinalFraudDecision;
import io.halvorsen.model.Order;
import io.temporal.activity.ActivityOptions;
import io.temporal.workflow.Workflow;

public class CheckForFraudWorkflowImpl implements CheckForFraudWorkflow {

    private String finalDecision = null;

    private final ActivityOptions defaultActivityOptions = ActivityOptions.newBuilder()
        .setScheduleToCloseTimeout(Duration.ofDays(3)) // Given it's a manual process, this could take 72 hours
        .build();

    private final CheckForFraudActivities activities = Workflow.newActivityStub(CheckForFraudActivities.class, defaultActivityOptions);

    @Override
    public String checkFraud(Order o) {
        System.out.println("CheckForFraudWorkflow :: Calling fraud service");
        String decision = activities.callFraudService(o);
        // If the Fraud service returns "Hold" then wait until it's actioned
        if(Constants.FRAUD_STATUS_HOLD.equals(decision)){
            // Polling based approach
            // String finalDecision = Constants.FRAUD_STATUS_PENDING; 
            // Final decision will return Pending until a decision has been made
            // Is this the right way of doing it, or should I use Signals? Let's find out
            /* 
            while(Constants.FRAUD_STATUS_PENDING.equals(finalDecision)){
                System.out.println("CheckForFraudWorkflow :: waiting for final decision");
                Workflow.sleep(Duration.ofMinutes(1)); // poll every minute
                finalDecision = activities.getFinalDecision(o);
            }*/
            
            // Event-driven approach
            // Let's use a signal instead!
            Workflow.await(() -> this.finalDecision != null);
            return this.finalDecision;
        }
        return decision;
    }

    @Override
    public void receiveFinalDecision(FinalFraudDecision decision) {
        System.out.printf("CheckForFraudWorkflow :: Decision received via signal: %s %n", decision.getDecision());
        this.finalDecision = decision.getDecision();
    }

}
