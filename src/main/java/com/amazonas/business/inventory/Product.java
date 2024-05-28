package com.amazonas.business.inventory;

import com.amazonas.business.stores.Rating;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Product implements Cloneable {
    private String productId;
    private String productName;
    private double price;
    private String category;
    private String description;
    private Rating rating;
    private Set<String> keyWords;

    public Product(
            String productId,
            String productName,
            double price,
            String category,
<<<<<<< HEAD
            String description,
            Rating rating
=======
            int rate,
            String description,
            Boolean enabled
>>>>>>> dace3e6ae6448bb5df3c3db7401cdff255994dcf
    ) {
        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.category = category;
        this.rating = rating;
        this.description = description;
<<<<<<< HEAD
        this.keyWords = new HashSet<>();
=======
        this.enabled = enabled;
>>>>>>> dace3e6ae6448bb5df3c3db7401cdff255994dcf
    }

    public String productId() {
        return productId;
    }

    public String productName() {
        return productName;
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

    public void setProductId(String newProductId) {
         this.productId = newProductId;
    }

    public void setProductName(String productName) {
        this.productName = productName;
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

    public Set<String> keyWords() {
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
                "nameProduct=" + productName + ", " +
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

    @Override
    public Product clone() {
        try {
            Product clone = (Product) super.clone();
            clone.productId = productId;
            clone.productName = productName;
            clone.price = price;
            clone.category = category;
            clone.description = description;
            clone.rating = rating;
            clone.keyWords = (HashSet<String>)((HashSet<String>) keyWords).clone();
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    public void setEnabled(){
        enabled = true;
    }
    public void setDisable(){
        enabled = true;
    }
    public void setDisabled(){
        enabled = false;
    }
    public boolean getEnabled(){
        return enabled;
    }
}
