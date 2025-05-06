package io.halvorsen.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(as = OrderLineItem.class)
public class OrderLineItem {

    private String sku;
    private int quantity;


    public OrderLineItem(){
        // Default constructor is needed for Jackson deserialization
    }

    public OrderLineItem(String sku, int quantity) {
        this.sku = sku;
        this.quantity = quantity;
    }

    public String getSku() {
        return sku;
    }
    public void setSku(String sku) {
        this.sku = sku;
    }
    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public static OrderLineItem create(String sku, int quantity){
        return new OrderLineItem(sku, quantity);
    }

}
