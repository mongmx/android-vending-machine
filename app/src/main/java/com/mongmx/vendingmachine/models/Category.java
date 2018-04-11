package com.mongmx.vendingmachine.models;

import java.util.List;

public class Category {
    private int id;
    private String name;
    private List<Product> products;
//    private List<Product> products_l;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

//    public List<Product> getProducts_l() {
//        return products_l;
//    }
//
//    public void setProducts_l(List<Product> products_l) {
//        this.products_l = products_l;
//    }
}
