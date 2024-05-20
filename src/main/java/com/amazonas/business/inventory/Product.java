package com.amazonas.business.inventory;

import java.util.HashSet;
import java.util.Objects;

public class Product {
    private String productID;
    private String nameProduct;
    private double price;
    private String category;
    private String description;
    private int rate;
    private HashSet<String> keyWords = new HashSet<>();

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

    public String description() {
        return description;
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
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Product) obj;
        return Objects.equals(this.productID, that.productID) &&
                Objects.equals(this.nameProduct, that.nameProduct) &&
                Double.doubleToLongBits(this.price) == Double.doubleToLongBits(that.price) &&
                Objects.equals(this.category, that.category) &&
                this.rate == that.rate;
    }

    @Override
    public int hashCode() {
        return Objects.hash(productID, nameProduct, price, category, rate);
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


}
