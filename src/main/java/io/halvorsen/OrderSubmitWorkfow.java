package io.halvorsen;

import io.halvorsen.model.Order;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface OrderSubmitWorkfow {

    @WorkflowMethod
    boolean submitOrder(Order o);

}
