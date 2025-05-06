package io.halvorsen;

import java.util.List;

import io.halvorsen.model.Order;
import io.halvorsen.model.OrderLineItem;
import io.temporal.activity.ActivityInterface;

@ActivityInterface
public interface OrderActivities {

    String preauth(Order o);
    //void fulfillOrder(Order o);
    //void settleOrder(Order o);

    void submitOrderToOms(Order o);
    void decrementInventory(List<OrderLineItem> orderLines);
    void updateProductAvailability(List<OrderLineItem> orderLines);
    void sendOrderConfirmationEmail(Order o);

    // compensating methods
    void reverseAuth(String authRef);
    void incrementInventory(List<OrderLineItem> orderLines);
    void sendOrderCancelationEmail(Order o);
    
}
