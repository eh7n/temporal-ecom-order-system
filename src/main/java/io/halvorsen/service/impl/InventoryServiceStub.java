package io.halvorsen.service.impl;

import io.halvorsen.service.InventoryService;

public class InventoryServiceStub implements InventoryService{

    public static final String INVENTORY_STATUS_INSTOCK = "INSTOCK";
    public static final String INVENTORY_STATUS_LOWSTOCK = "LOWSTOCK";
    public static final String INVENTORY_STATUS_OUTOFSTOCK = "OOS";

    private InventoryServiceStub(){

    }

    @Override
    public boolean changeStockQuantity(String sku, Integer qty) {
        System.out.printf("InventoryService :: Adjusting stock quantity for %s by %d %n", sku, qty);
        return true;
    }

    @Override
    public Integer getStockQuantity(String sku) {
        System.out.printf("InventoryService :: Querying stock quantity for %s, returning 100 %n", sku);
        return 100;
    }

    @Override
    public String getStockStatus(String sku) {
        System.out.printf("InventoryService :: Get stock status for %s %n", sku);

        Integer stockQty = this.getStockQuantity(sku);
        if(stockQty > 10){
            return INVENTORY_STATUS_INSTOCK;
        }else if(stockQty > 0){
            return INVENTORY_STATUS_LOWSTOCK;
        }else{
            return INVENTORY_STATUS_OUTOFSTOCK;
        }
    }

    public static InventoryService create(){
        return new InventoryServiceStub();
    }

}
