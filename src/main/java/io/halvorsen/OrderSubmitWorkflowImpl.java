package io.halvorsen;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import io.halvorsen.model.Order;
import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.workflow.ChildWorkflowOptions;
import io.temporal.workflow.Saga;
import io.temporal.workflow.Workflow;

public class OrderSubmitWorkflowImpl implements OrderSubmitWorkfow {

    /*
     * At least one of the following options needs to be defined:
     * - setStartToCloseTimeout
     * - setScheduleToCloseTimeout
     */

    // RetryOptions specify how to automatically handle retries when Activities fail
    private final RetryOptions retryoptions = RetryOptions.newBuilder()
            .setInitialInterval(Duration.ofSeconds(1)) // Wait 1 second before first retry
            .setMaximumInterval(Duration.ofSeconds(20)) // Do not exceed 20 seconds between retries
            .setBackoffCoefficient(2) // Wait 1 second, then 2, then 4, etc
            .setMaximumAttempts(10) // Fail after 10 attempts
            .build();

    // ActivityOptions specify the limits on how long an Activity can execute before
    // being interrupted by the Orchestration service
    private final ActivityOptions defaultActivityOptions = ActivityOptions.newBuilder()
            .setRetryOptions(retryoptions) // Apply the RetryOptions defined above
            .setStartToCloseTimeout(Duration.ofSeconds(2)) // Max execution time for single Activity
            .setScheduleToCloseTimeout(Duration.ofSeconds(5000)) // Entire duration from scheduling to completion
                                                                 // including queue time
            .build();

    private final Map<String, ActivityOptions> perActivityMethodOptions = new HashMap<String, ActivityOptions>() {
        {
            // A heartbeat time-out is a proof-of life indicator that an activity is still
            // working.
            // The 5 second duration used here waits for up to 5 seconds to hear a
            // heartbeat.
            // If one is not heard, the Activity fails.
            // Use heartbeats for long-lived event-driven applications.
            put("ORDER SUBMITTED", ActivityOptions.newBuilder().setHeartbeatTimeout(Duration.ofSeconds(5)).build());
        }
    };

    private final OrderActivities orderActivityStub = Workflow.newActivityStub(OrderActivities.class,
            defaultActivityOptions, perActivityMethodOptions);

    @Override
    public boolean submitOrder(Order o) {

        // Create the saga to orchestrate the compensating actions
        Saga orderSubmitSaga = new Saga(new Saga.Options.Builder().build());

        // Run the submit order workflow
        try {

            // Preauthorize the payment (hold funds til shipped)
            String authRef = orderActivityStub.preauth(o);
            // Method sequencing matters when dealing with compensations
            // However, I believe this is handled in this case by doing a null check
            // on the authRef that's returned in the activity
            orderSubmitSaga.addCompensation(orderActivityStub::reverseAuth, authRef);
        
            // Now we have payment, Decrement Inventory in Inventory service
            orderSubmitSaga.addCompensation(orderActivityStub::updateProductAvailability, o.getLineItems());
            orderSubmitSaga.addCompensation(orderActivityStub::incrementInventory, o.getLineItems());
            orderActivityStub.decrementInventory(o.getLineItems());

            // Update Product Availability on Product Catalog Service
            orderActivityStub.updateProductAvailability(o.getLineItems());
            
            //Send Order Confirmation
            // I'd actually rather do this after the initial fraud check, but I'm not sure
            // the best approach to forking (ex: if immediate fraud check is successful, then email)
            // otherwise wait. So we'll just compensate for it if fraud does fail
            orderSubmitSaga.addCompensation(orderActivityStub::sendOrderCancelationEmail, o);
            orderActivityStub.sendOrderConfirmationEmail(o);

            // Perform fraud check and manually hold order if manual review is flagged
            // I know the docs say "when in doubt, use Activities" but I wanted to test how child workflows work
            ChildWorkflowOptions childOptions = ChildWorkflowOptions.newBuilder()
                                            .setWorkflowId("fraud-check-" + o.getOrderId())
                                            .setTaskQueue(Constants.FRAUD_CHECK_QUEUE)
                                            .build();
            CheckForFraudWorkflow fraudCheck = Workflow.newChildWorkflowStub(CheckForFraudWorkflow.class, childOptions);
            String decision = fraudCheck.checkFraud(o);
            if (Constants.FRAUD_STATUS_REJECT.equals(decision)) {
                // Roll back the order submit
                orderSubmitSaga.compensate();
                return false;
            }else{
                // Release Order to OMS/WHMS
                orderActivityStub.submitOrderToOms(o);
            }
        }catch(Exception e){
            e.printStackTrace();
            System.out.printf("OrderSubmitWorkfow :: Something went wrong with order ID %s", o.getOrderId());
            System.out.flush();

            // Rollback any changes necessary
            orderSubmitSaga.compensate();

            // Transaction ends here
            throw Workflow.wrap(e);
        }

        return true;
    }

}
