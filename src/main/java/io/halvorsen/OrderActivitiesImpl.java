package io.halvorsen;

import java.util.List;

import io.halvorsen.model.Order;
import io.halvorsen.model.OrderLineItem;
import io.halvorsen.service.FraudDetectionService;
import io.halvorsen.service.InventoryService;
import io.halvorsen.service.OMSService;
import io.halvorsen.service.PaymentService;
import io.halvorsen.service.ProductCatalogService;
import io.halvorsen.service.impl.FraudDetectionServiceStub;
import io.halvorsen.service.impl.InventoryServiceStub;
import io.halvorsen.service.impl.OMSServiceStub;
import io.halvorsen.service.impl.PaymentServiceStub;
import io.halvorsen.service.impl.ProductCatalogServiceStub;
import io.temporal.failure.ApplicationFailure;

public class OrderActivitiesImpl implements OrderActivities {

    // Initialize the service adapters
    // Typically I'd use DI/IoC here but let's just hardcode it for the example
    FraudDetectionService fraudDetectionService = FraudDetectionServiceStub.create();
    InventoryService inventoryService = InventoryServiceStub.create();
    PaymentService paymentService = PaymentServiceStub.create();
    ProductCatalogService productService = ProductCatalogServiceStub.create();
    OMSService omsService = OMSServiceStub.create();

    @Override
    public void decrementInventory(List<OrderLineItem> orderLines) {
        // Ok, so, we'd probably need to do each line item in isolation with their own compensating transaction
        // to go along with it, but I'm just treating the entire order atomically for simplicity sake
        for(OrderLineItem i : orderLines ){
            inventoryService.changeStockQuantity(i.getSku(), (i.getQuantity() * -1));
        }
    }

    @Override
    public void submitOrderToOms(Order o) {
        System.out.println("OrderActivities :: Sending order to OMS: " + o.getOrderId());
        // In this case, let's assume the OMS is only handling order / warehouse management operations
        // In a real world situation, this could open up a ton of potential edge-cases like shorting
        boolean success = omsService.submitOrderForFulfillment(o);
        if(!success){
            throw ApplicationFailure.newFailure("OMS Failure, Retry again", "oms-submit-failed");
        }
    }

    @Override
    public void updateProductAvailability(List<OrderLineItem> ol) {
        // Recalcualte the product availability for each line item
        for(OrderLineItem item : ol){
            productService.setProductAvailability(item.getSku(), inventoryService.getStockStatus(item.getSku()));
        }
    }

    @Override
    // Compensating method of replacing the inventory in case an order could not be captured in OMS
    public void incrementInventory(List<OrderLineItem> orderLines) {
        System.out.println("OrderActivities :: Compensating for workflow failure");
        for(OrderLineItem i : orderLines ){
            inventoryService.changeStockQuantity(i.getSku(), i.getQuantity());
        }
    }

    @Override
    public String preauth(Order o) {
        return paymentService.getPreAuthorization(o.getOrderId(), o.getPaymentInfo(), o.getAmount());
    }

    @Override
    public void reverseAuth(String authRef) {
        // Only compensate if the original auth was captured
        if(authRef != null){
            paymentService.reverseAuthorization(authRef);
        }
    }

    @Override
    public void sendOrderConfirmationEmail(Order o) {
        System.out.println("OrderActivities :: Send the order confirmation email to the customer");
    }

    @Override
    public void sendOrderCancelationEmail(Order o) {
        System.out.println("OrderActivities :: Send the order cancel email to the customer when fraud hold fails");
    }

}
