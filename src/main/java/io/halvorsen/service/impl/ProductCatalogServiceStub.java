package io.halvorsen.service.impl;

import io.halvorsen.service.ProductCatalogService;

public class ProductCatalogServiceStub implements ProductCatalogService{

    private ProductCatalogServiceStub(){

    }

    @Override
    public boolean setProductAvailability(String sku, String status) {
        System.out.printf("ProductCatalogService :: Setting %s to %s %n", sku, status);
        return true;
    }

    public static ProductCatalogService create(){
        return new ProductCatalogServiceStub();
    }

}
