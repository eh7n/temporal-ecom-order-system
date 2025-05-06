package io.halvorsen.model;

import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(as = Order.class)
public class Order {

    private String orderId;
    private List<OrderLineItem> lineItems;
    private Customer customer;
    private PaymentInfo paymentInfo;
    private Double amount;

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public PaymentInfo getPaymentInfo() {
        return paymentInfo;
    }

    public void setPaymentInfo(PaymentInfo paymentInfo) {
        this.paymentInfo = paymentInfo;
    }

    public Order(){
    }

    public Order(String orderId, Double amount, Customer customer, List<OrderLineItem> lineItems, PaymentInfo payment) {
        this.orderId = orderId;
        this.amount = amount;
        this.customer = customer;
        this.lineItems = lineItems;
        this.paymentInfo = payment;
    }

    public String getOrderId() {
        return orderId;
    }
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
    public List<OrderLineItem> getLineItems() {
        return lineItems;
    }
    public void setLineItems(List<OrderLineItem> lineItems) {
        this.lineItems = lineItems;
    }

    public static Order create(String orderId, Double amount, Customer customer, List<OrderLineItem> lineItems, PaymentInfo payment){
        return new Order(orderId, amount, customer, lineItems, payment);
    }
}
