package io.halvorsen.service;

public interface InventoryService {

    public boolean changeStockQuantity(String sku, Integer qty);
    public Integer getStockQuantity(String sku);
    public String getStockStatus(String sku);

}
