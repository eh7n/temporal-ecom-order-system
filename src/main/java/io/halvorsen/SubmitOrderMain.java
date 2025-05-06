package io.halvorsen;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.halvorsen.model.Customer;
import io.halvorsen.model.Order;
import io.halvorsen.model.OrderLineItem;
import io.halvorsen.model.PaymentInfo;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.client.WorkflowStub;
import io.temporal.serviceclient.WorkflowServiceStubs;

public class SubmitOrderMain {
    public static void main(String[] args) {

        final String orderId = UUID.randomUUID().toString();

        // This gRPC stubs wrapper talks to the local docker instance of the Temporal service.
        WorkflowServiceStubs service = WorkflowServiceStubs.newLocalServiceStubs();

        // WorkflowClient can be used to start, signal, query, cancel, and terminate Workflows.
        WorkflowClient client = WorkflowClient.newInstance(service);

        // Define our workflow unique id
        final String WORKFLOW_ID = "OrderSubmitWorkflow-" + orderId;

        /*
         * Set Workflow options such as WorkflowId and Task Queue so the worker knows where to list and which workflows to execute.
         */
        WorkflowOptions options = WorkflowOptions.newBuilder()
                    .setWorkflowId(WORKFLOW_ID)
                    .setTaskQueue(Constants.ORDER_SUBMIT_QUEUE)
                    .build();

        // Create the workflow client stub. It is used to start our workflow execution.
        OrderSubmitWorkfow workflow = client.newWorkflowStub(OrderSubmitWorkfow.class, options);

        /*
         * Execute our workflow and wait for it to complete. 
         */

        List<OrderLineItem> oi = new ArrayList<OrderLineItem>();
        oi.add(OrderLineItem.create("sku123", 2));
        oi.add(OrderLineItem.create("sku456", 3));
        Customer customer = new Customer();
        customer.setName("Test Success");
        //customer.setName("Test Hold");
        //customer.setName("Test Reject");

        customer.setEmail("test@test.com");
        customer.setAddress("1060 W Addison, Chicago, IL");
        
        PaymentInfo payment = new PaymentInfo();
        payment.setPaymentToken("STRIPE-" + UUID.randomUUID().toString());

        boolean success = workflow.submitOrder(Order.create(orderId, 99.95, customer, oi, payment));

        String workflowId = WorkflowStub.fromTyped(workflow).getExecution().getWorkflowId();
        // Display workflow execution results
        System.out.println(workflowId + " " + success);
        System.exit(0);
    }
}