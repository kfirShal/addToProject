package com.amazonas.business.inventory;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Product implements Cloneable {
    private String productID;
    private String nameProduct;
    private double price;
    private String category;
    private String description;
    private int rate;
    private Set<String> keyWords = new HashSet<>();

    public Product(
            String productID,
            String nameProduct,
            double price,
            String category,
            String description,
            int rate
    ) {
        this.productID = productID;
        this.nameProduct = nameProduct;
        this.price = price;
        this.category = category;
        this.description = description;
        this.rate = rate;
        this.description = description;
    }

    public String productID() {
        return productID;
    }

    public String nameProduct() {
        return nameProduct;
    }

    public double price() {
        return price;
    }

    public String category() {
        return category;
    }

    public int rate() {
        return rate;
    }

    public void changeProductID(String newProductId) {
         this.productID = newProductId;
    }

    public void changeNameProduct(String nameProduct) {
        this.nameProduct = nameProduct;
    }

    public void changePrice(double price) {
        this.price = price;
    }

    public void changeCategory(String category) {
        this.category = category;
    }

    public void changeDescription(String description) {
        this.description = description;
    }

    public void changeRate(int rate) {
        this.rate = rate;
    }

    public void addKeyWords(String key){
        keyWords.add(key);
    }

    public void removeKeyWords(String key){
        keyWords.remove(key);
    }

    @Override
    public String toString() {
        return "Product[" +
                "productID=" + productID + ", " +
                "nameProduct=" + nameProduct + ", " +
                "price=" + price + ", " +
                "category=" + category + ", " +
                "rate=" + rate + ']';
    }


    public String description() {
        return description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(productID, product.productID);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(productID);
    }

    @Override
    public Product clone() {
        try {
            Product clone = (Product) super.clone();
            clone.productID = productID;
            clone.nameProduct = nameProduct;
            clone.price = price;
            clone.category = category;
            clone.description = description;
            clone.rate = rate;
            clone.keyWords = (HashSet<String>)((HashSet<String>) keyWords).clone();
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
