package io.halvorsen;

import io.temporal.client.WorkflowClient;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;

public class OrderSubmitWorker {
 public static void main(String[] args) {

        // Get a Workflow service stub.
        WorkflowServiceStubs service = WorkflowServiceStubs.newLocalServiceStubs();

        /*
        * Get a Workflow service client which can be used to start, Signal, and Query Workflow Executions.
        */
        WorkflowClient client = WorkflowClient.newInstance(service);

        /*
        * Define the workflow factory. It is used to create workflow workers that poll specific Task Queues.
        */
        WorkerFactory factory = WorkerFactory.newInstance(client);

        /*
        * Define the workflow worker. Workflow workers listen to a defined task queue and process
        * workflows and activities.
        */
        Worker orderSubmitWorker = factory.newWorker(Constants.ORDER_SUBMIT_QUEUE);
        Worker fraudWorker = factory.newWorker(Constants.FRAUD_CHECK_QUEUE);

        /*
        * Register our workflow implementation with the worker.
        * Workflow implementations must be known to the worker at runtime in
        * order to dispatch workflow tasks.
        */
        orderSubmitWorker.registerWorkflowImplementationTypes(OrderSubmitWorkflowImpl.class);
        fraudWorker.registerWorkflowImplementationTypes(CheckForFraudWorkflowImpl.class);


        /*
        * Register our Activity Types with the Worker. Since Activities are stateless and thread-safe,
        * the Activity Type is a shared instance.
        */
        orderSubmitWorker.registerActivitiesImplementations(new OrderActivitiesImpl());
        fraudWorker.registerActivitiesImplementations(new CheckForFraudActivitiesImpl());


        /*
        * Start all the workers registered for a specific task queue.
        * The started workers then start polling for workflows and activities.
        */
        factory.start();
    }
}
