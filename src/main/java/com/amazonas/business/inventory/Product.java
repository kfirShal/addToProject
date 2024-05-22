package com.amazonas.business.inventory;

import com.amazonas.business.stores.Rating;

import java.util.HashSet;
import java.util.Objects;

public class Product {
    private String productId;
    private String nameProduct;
    private double price;
    private String category;
    private String description;
    private Rating rating;

    private HashSet<String> keyWords = new HashSet<>();

    public Product(
            String productId,
            String nameProduct,
            double price,
            String category,
            String description,
            Rating rating
    ) {
        this.productId = productId;
        this.nameProduct = nameProduct;
        this.price = price;
        this.category = category;
        this.rating = rating;
        this.description = description;
    }

    public String productId() {
        return productId;
    }

    public String productName() {
        return nameProduct;
    }

    public double price() {
        return price;
    }

    public String category() {
        return category;
    }

    public Rating rating() {
        return rating;
    }

    public String description() {
        return description;
    }

    public void setProductID(String newProductId) {
         this.productId = newProductId;
    }

    public void setProductName(String productName) {
        this.nameProduct = productName;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setRating(Rating rating) {
        this.rating = rating;
    }

    public HashSet<String> keyWords() {
        return keyWords;
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
                "productID=" + productId + ", " +
                "nameProduct=" + nameProduct + ", " +
                "price=" + price + ", " +
                "category=" + category + ", " +
                "rate=" + rating + ']';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(productId, product.productId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(productId);
    }
}
