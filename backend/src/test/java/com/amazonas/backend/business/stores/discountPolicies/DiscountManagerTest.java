package com.amazonas.backend.business.stores.discountPolicies;

import com.amazonas.common.DiscountDTOs.*;
import com.amazonas.common.dtos.Product;
import com.amazonas.common.utils.Rating;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DiscountManagerTest {
    List<ProductWithQuantitiy> cart;
    DiscountManager discountManager;

    @BeforeEach
    void setUp() {
        discountManager = new DiscountManager();
        Product product1 = new Product("1", "", 100.0, "category1", "", Rating.FIVE_STARS);
        Product product2 = new Product("2", "", 100.0, "category1", "", Rating.FIVE_STARS);
        Product product3 = new Product("3", "", 100.0, "category1", "", Rating.FIVE_STARS);
        Product product4 = new Product("4", "", 1000.0, "category2", "", Rating.FIVE_STARS);
        Product product5 = new Product("5", "", 1000.0, "category2", "", Rating.FIVE_STARS);
        Product product6 = new Product("6", "", 10.0, "category2", "", Rating.FIVE_STARS);
        cart = new ArrayList<>();
        cart.add(new ProductWithQuantitiy(product1, 1));
        cart.add(new ProductWithQuantitiy(product2, 1));
        cart.add(new ProductWithQuantitiy(product3, 1));
        cart.add(new ProductWithQuantitiy(product4, 1));
        cart.add(new ProductWithQuantitiy(product5, 1));
        cart.add(new ProductWithQuantitiy(product6, 2));
    }

    @Test
    //case from requirements
    void additionDiscountRequirement() {
        try {
            List<DiscountComponentDTO> discounts = new ArrayList<>();
            discounts.add(new SimpleDiscountDTO(HierarchyLevel.CategoryLevel, "category1", 5));
            discounts.add(new SimpleDiscountDTO(HierarchyLevel.StoreLevel, "", 20));

            DiscountComponentDTO additionDiscount = new MultipleDiscountDTO(discounts, MultipleDiscountType.ADDITION);
            discountManager.changeDiscountPolicy(additionDiscount);
            ProductAfterDiscount[] res = discountManager.applyDiscountPolicy(cart);
            double finalPrice = 0;
            for (ProductAfterDiscount p : res) {
                finalPrice += p.priceAfterDiscount() * p.quantity();
            }
            assertEquals(finalPrice, 1841.0);
        }
        catch (Exception e) {
            fail("Exception occurred while applying discount policy");
        }
    }

    @Test
    void noDiscount() {
        try {
            ProductAfterDiscount[] res = discountManager.applyDiscountPolicy(cart);
            double finalPrice = 0;
            for (ProductAfterDiscount p : res) {
                finalPrice += p.priceAfterDiscount() * p.quantity();
            }
            assertEquals(finalPrice, 2320.0);
        }
        catch (Exception e) {
            fail("Exception occurred while applying empty discount policy");
        }
    }

    @Test
    void globalDiscount() {
        try {
            DiscountComponentDTO globalDiscount = new SimpleDiscountDTO(HierarchyLevel.StoreLevel, "", 50);
            discountManager.changeDiscountPolicy(globalDiscount);
            ProductAfterDiscount[] res = discountManager.applyDiscountPolicy(cart);
            double finalPrice = 0;
            for (ProductAfterDiscount p : res) {
                finalPrice += p.priceAfterDiscount() * p.quantity();
            }
            assertEquals(finalPrice, 1160.0);
        }
        catch (Exception e) {
            fail("Exception occurred while applying discount policy");
        }
    }

    @Test
    void categoryDiscount() {
        try {
            DiscountComponentDTO categoryDiscount = new SimpleDiscountDTO(HierarchyLevel.CategoryLevel, "category1", 50);
            discountManager.changeDiscountPolicy(categoryDiscount);
            ProductAfterDiscount[] res = discountManager.applyDiscountPolicy(cart);
            double finalPrice = 0;
            for (ProductAfterDiscount p : res) {
                finalPrice += p.priceAfterDiscount() * p.quantity();
            }
            assertEquals(finalPrice, 2170.0);
        }
        catch (Exception e) {
            fail("Exception occurred while applying discount policy");
        }
    }

    @Test
    void productDiscountSomeProducts() {
        try {
            DiscountComponentDTO productDiscount = new SimpleDiscountDTO(HierarchyLevel.ProductLevel, "6", 50);
            discountManager.changeDiscountPolicy(productDiscount);
            ProductAfterDiscount[] res = discountManager.applyDiscountPolicy(cart);
            double finalPrice = 0;
            for (ProductAfterDiscount p : res) {
                finalPrice += p.priceAfterDiscount() * p.quantity();
            }
            assertEquals(finalPrice, 2310.0);
        }
        catch (Exception e) {
            fail("Exception occurred while applying discount policy");
        }
    }

    @Test
    void maximumPriceDiscount() {
        try {
            List<DiscountComponentDTO> discounts = new ArrayList<>();
            discounts.add(new SimpleDiscountDTO(HierarchyLevel.ProductLevel, "6", 50));
            discounts.add(new SimpleDiscountDTO(HierarchyLevel.CategoryLevel, "category1", 50));
            discounts.add(new SimpleDiscountDTO(HierarchyLevel.StoreLevel, "", 50));

            DiscountComponentDTO maximalPriceDiscount = new MultipleDiscountDTO(discounts, MultipleDiscountType.MAXIMUM_PRICE);
            discountManager.changeDiscountPolicy(maximalPriceDiscount);
            ProductAfterDiscount[] res = discountManager.applyDiscountPolicy(cart);
            double finalPrice = 0;
            for (ProductAfterDiscount p : res) {
                finalPrice += p.priceAfterDiscount() * p.quantity();
            }
            assertEquals(finalPrice, 2310.0);
        }
        catch (Exception e) {
            fail("Exception occurred while applying discount policy");
        }
    }

    @Test
    void minimumPriceDiscount() {
        try {
            List<DiscountComponentDTO> discounts = new ArrayList<>();
            discounts.add(new SimpleDiscountDTO(HierarchyLevel.ProductLevel, "6", 50));
            discounts.add(new SimpleDiscountDTO(HierarchyLevel.CategoryLevel, "category1", 50));
            discounts.add(new SimpleDiscountDTO(HierarchyLevel.StoreLevel, "", 50));

            DiscountComponentDTO minimalPriceDiscount = new MultipleDiscountDTO(discounts, MultipleDiscountType.MINIMUM_PRICE);
            discountManager.changeDiscountPolicy(minimalPriceDiscount);
            ProductAfterDiscount[] res = discountManager.applyDiscountPolicy(cart);
            double finalPrice = 0;
            for (ProductAfterDiscount p : res) {
                finalPrice += p.priceAfterDiscount() * p.quantity();
            }
            assertEquals(finalPrice, 1160.0);
        }
        catch (Exception e) {
            fail("Exception occurred while applying discount policy");
        }
    }

    @Test
    void additionDiscount() {
        try {
            List<DiscountComponentDTO> discounts = new ArrayList<>();
            discounts.add(new SimpleDiscountDTO(HierarchyLevel.ProductLevel, "6", 50));
            discounts.add(new SimpleDiscountDTO(HierarchyLevel.StoreLevel, "", 50));

            DiscountComponentDTO additionDiscount = new MultipleDiscountDTO(discounts, MultipleDiscountType.ADDITION);
            discountManager.changeDiscountPolicy(additionDiscount);
            ProductAfterDiscount[] res = discountManager.applyDiscountPolicy(cart);
            double finalPrice = 0;
            for (ProductAfterDiscount p : res) {
                finalPrice += p.priceAfterDiscount() * p.quantity();
            }
            assertEquals(finalPrice, 1150.0);
        }
        catch (Exception e) {
            fail("Exception occurred while applying discount policy");
        }
    }

    @Test
    void atLeastPriceConditionExist() {
        try {
            DiscountConditionDTO condition = new UnaryConditionDTO(UnaryConditionType.AT_LEAST_SOME_PRICE, 1500, "");
            DiscountComponentDTO discountComponent = new SimpleDiscountDTO(HierarchyLevel.StoreLevel, "", 50);
            DiscountComponentDTO discount = new ComplexDiscountDTO(condition, discountComponent);
            discountManager.changeDiscountPolicy(discount);
            ProductAfterDiscount[] res = discountManager.applyDiscountPolicy(cart);
            double finalPrice = 0;
            for (ProductAfterDiscount p : res) {
                finalPrice += p.priceAfterDiscount() * p.quantity();
            }
            assertEquals(finalPrice, 1160.0);
        }
        catch (Exception e) {
            fail("Exception occurred while applying discount policy");
        }
    }

    @Test
    void atLeastPriceConditionDoesntExist() {
        try {
            DiscountConditionDTO condition = new UnaryConditionDTO(UnaryConditionType.AT_LEAST_SOME_PRICE, 3000, "");
            DiscountComponentDTO discountComponent = new SimpleDiscountDTO(HierarchyLevel.StoreLevel, "", 50);
            DiscountComponentDTO discount = new ComplexDiscountDTO(condition, discountComponent);
            discountManager.changeDiscountPolicy(discount);
            ProductAfterDiscount[] res = discountManager.applyDiscountPolicy(cart);
            double finalPrice = 0;
            for (ProductAfterDiscount p : res) {
                finalPrice += p.priceAfterDiscount() * p.quantity();
            }
            assertEquals(finalPrice, 2320.0);
        }
        catch (Exception e) {
            fail("Exception occurred while applying discount policy");
        }
    }

    @Test
    void atLeastSomeProductsConditionExist() {
        try {
            DiscountConditionDTO condition = new UnaryConditionDTO(UnaryConditionType.AT_LEAST_NUMBER_OF_SOME_PRODUCT, 2, "6");
            DiscountComponentDTO discountComponent = new SimpleDiscountDTO(HierarchyLevel.StoreLevel, "", 50);
            DiscountComponentDTO discount = new ComplexDiscountDTO(condition, discountComponent);
            discountManager.changeDiscountPolicy(discount);
            ProductAfterDiscount[] res = discountManager.applyDiscountPolicy(cart);
            double finalPrice = 0;
            for (ProductAfterDiscount p : res) {
                finalPrice += p.priceAfterDiscount() * p.quantity();
            }
            assertEquals(finalPrice, 1160.0);
        }
        catch (Exception e) {
            fail("Exception occurred while applying discount policy");
        }
    }

    @Test
    void atLeastSomeProductsConditionDoesntExist() {
        try {
            DiscountConditionDTO condition = new UnaryConditionDTO(UnaryConditionType.AT_LEAST_NUMBER_OF_SOME_PRODUCT, 2, "1");
            DiscountComponentDTO discountComponent = new SimpleDiscountDTO(HierarchyLevel.StoreLevel, "", 50);
            DiscountComponentDTO discount = new ComplexDiscountDTO(condition, discountComponent);
            discountManager.changeDiscountPolicy(discount);
            ProductAfterDiscount[] res = discountManager.applyDiscountPolicy(cart);
            double finalPrice = 0;
            for (ProductAfterDiscount p : res) {
                finalPrice += p.priceAfterDiscount() * p.quantity();
            }
            assertEquals(finalPrice, 2320.0);
        }
        catch (Exception e) {
            fail("Exception occurred while applying discount policy");
        }
    }

    @Test
    void atLeastSomeCategoriesConditionExist() {
        try {
            DiscountConditionDTO condition = new UnaryConditionDTO(UnaryConditionType.AT_LEAST_NUMBER_OF_ITEMS_FROM_CATEGORY, 3, "category1");
            DiscountComponentDTO discountComponent = new SimpleDiscountDTO(HierarchyLevel.StoreLevel, "", 50);
            DiscountComponentDTO discount = new ComplexDiscountDTO(condition, discountComponent);
            discountManager.changeDiscountPolicy(discount);
            ProductAfterDiscount[] res = discountManager.applyDiscountPolicy(cart);
            double finalPrice = 0;
            for (ProductAfterDiscount p : res) {
                finalPrice += p.priceAfterDiscount() * p.quantity();
            }
            assertEquals(finalPrice, 1160.0);
        }
        catch (Exception e) {
            fail("Exception occurred while applying discount policy");
        }
    }

    @Test
    void atLeastSomeCategoriesConditionDoesntExist() {
        try {
            DiscountConditionDTO condition = new UnaryConditionDTO(UnaryConditionType.AT_LEAST_NUMBER_OF_ITEMS_FROM_CATEGORY, 4, "category1");
            DiscountComponentDTO discountComponent = new SimpleDiscountDTO(HierarchyLevel.StoreLevel, "", 50);
            DiscountComponentDTO discount = new ComplexDiscountDTO(condition, discountComponent);
            discountManager.changeDiscountPolicy(discount);
            ProductAfterDiscount[] res = discountManager.applyDiscountPolicy(cart);
            double finalPrice = 0;
            for (ProductAfterDiscount p : res) {
                finalPrice += p.priceAfterDiscount() * p.quantity();
            }
            assertEquals(finalPrice, 2320.0);
        }
        catch (Exception e) {
            fail("Exception occurred while applying discount policy");
        }
    }

    @Test
    void xorConditionExist() {
        try {
            List<DiscountConditionDTO> conditions = new LinkedList<>();
            conditions.add(new UnaryConditionDTO(UnaryConditionType.AT_LEAST_NUMBER_OF_ITEMS_FROM_CATEGORY, 3, "category1"));
            conditions.add(new UnaryConditionDTO(UnaryConditionType.AT_LEAST_NUMBER_OF_SOME_PRODUCT, 3, "6"));
            conditions.add(new UnaryConditionDTO(UnaryConditionType.AT_LEAST_SOME_PRICE, 3000, ""));
            DiscountConditionDTO condition = new MultipleConditionDTO(MultipleConditionType.XOR, conditions);

            DiscountComponentDTO discountComponent = new SimpleDiscountDTO(HierarchyLevel.StoreLevel, "", 50);
            DiscountComponentDTO discount = new ComplexDiscountDTO(condition, discountComponent);
            discountManager.changeDiscountPolicy(discount);
            ProductAfterDiscount[] res = discountManager.applyDiscountPolicy(cart);
            double finalPrice = 0;
            for (ProductAfterDiscount p : res) {
                finalPrice += p.priceAfterDiscount() * p.quantity();
            }
            assertEquals(finalPrice, 1160.0);
        }
        catch (Exception e) {
            fail("Exception occurred while applying discount policy");
        }
    }

    @Test
    void xorConditionDoesntExist() {
        try {
            List<DiscountConditionDTO> conditions = new LinkedList<>();
            conditions.add(new UnaryConditionDTO(UnaryConditionType.AT_LEAST_NUMBER_OF_ITEMS_FROM_CATEGORY, 3, "category1"));
            conditions.add(new UnaryConditionDTO(UnaryConditionType.AT_LEAST_NUMBER_OF_SOME_PRODUCT, 2, "6"));
            conditions.add(new UnaryConditionDTO(UnaryConditionType.AT_LEAST_SOME_PRICE, 1500, ""));
            DiscountConditionDTO condition = new MultipleConditionDTO(MultipleConditionType.XOR, conditions);

            DiscountComponentDTO discountComponent = new SimpleDiscountDTO(HierarchyLevel.StoreLevel, "", 50);
            DiscountComponentDTO discount = new ComplexDiscountDTO(condition, discountComponent);
            discountManager.changeDiscountPolicy(discount);
            ProductAfterDiscount[] res = discountManager.applyDiscountPolicy(cart);
            double finalPrice = 0;
            for (ProductAfterDiscount p : res) {
                finalPrice += p.priceAfterDiscount() * p.quantity();
            }
            assertEquals(finalPrice, 1160.0);
        }
        catch (Exception e) {
            fail("Exception occurred while applying discount policy");
        }
    }

    @Test
    void orConditionExist() {
        try {
            List<DiscountConditionDTO> conditions = new LinkedList<>();
            conditions.add(new UnaryConditionDTO(UnaryConditionType.AT_LEAST_NUMBER_OF_ITEMS_FROM_CATEGORY, 3, "category1"));
            conditions.add(new UnaryConditionDTO(UnaryConditionType.AT_LEAST_NUMBER_OF_SOME_PRODUCT, 2, "6"));
            conditions.add(new UnaryConditionDTO(UnaryConditionType.AT_LEAST_SOME_PRICE, 3000, ""));
            DiscountConditionDTO condition = new MultipleConditionDTO(MultipleConditionType.OR, conditions);

            DiscountComponentDTO discountComponent = new SimpleDiscountDTO(HierarchyLevel.StoreLevel, "", 50);
            DiscountComponentDTO discount = new ComplexDiscountDTO(condition, discountComponent);
            discountManager.changeDiscountPolicy(discount);
            ProductAfterDiscount[] res = discountManager.applyDiscountPolicy(cart);
            double finalPrice = 0;
            for (ProductAfterDiscount p : res) {
                finalPrice += p.priceAfterDiscount() * p.quantity();
            }
            assertEquals(finalPrice, 1160.0);
        }
        catch (Exception e) {
            fail("Exception occurred while applying discount policy");
        }
    }

    @Test
    void orConditionDoesntExist() {
        try {
            List<DiscountConditionDTO> conditions = new LinkedList<>();
            conditions.add(new UnaryConditionDTO(UnaryConditionType.AT_LEAST_NUMBER_OF_ITEMS_FROM_CATEGORY, 4, "category1"));
            conditions.add(new UnaryConditionDTO(UnaryConditionType.AT_LEAST_NUMBER_OF_SOME_PRODUCT, 3, "6"));
            conditions.add(new UnaryConditionDTO(UnaryConditionType.AT_LEAST_SOME_PRICE, 3000, ""));
            DiscountConditionDTO condition = new MultipleConditionDTO(MultipleConditionType.OR, conditions);

            DiscountComponentDTO discountComponent = new SimpleDiscountDTO(HierarchyLevel.StoreLevel, "", 50);
            DiscountComponentDTO discount = new ComplexDiscountDTO(condition, discountComponent);
            discountManager.changeDiscountPolicy(discount);
            ProductAfterDiscount[] res = discountManager.applyDiscountPolicy(cart);
            double finalPrice = 0;
            for (ProductAfterDiscount p : res) {
                finalPrice += p.priceAfterDiscount() * p.quantity();
            }
            assertEquals(finalPrice, 1160.0);
        }
        catch (Exception e) {
            fail("Exception occurred while applying discount policy");
        }
    }

    @Test
    void andConditionExist() {
        try {
            List<DiscountConditionDTO> conditions = new LinkedList<>();
            conditions.add(new UnaryConditionDTO(UnaryConditionType.AT_LEAST_NUMBER_OF_ITEMS_FROM_CATEGORY, 3, "category1"));
            conditions.add(new UnaryConditionDTO(UnaryConditionType.AT_LEAST_NUMBER_OF_SOME_PRODUCT, 2, "6"));
            conditions.add(new UnaryConditionDTO(UnaryConditionType.AT_LEAST_SOME_PRICE, 1500, ""));
            DiscountConditionDTO condition = new MultipleConditionDTO(MultipleConditionType.AND, conditions);

            DiscountComponentDTO discountComponent = new SimpleDiscountDTO(HierarchyLevel.StoreLevel, "", 50);
            DiscountComponentDTO discount = new ComplexDiscountDTO(condition, discountComponent);
            discountManager.changeDiscountPolicy(discount);
            ProductAfterDiscount[] res = discountManager.applyDiscountPolicy(cart);
            double finalPrice = 0;
            for (ProductAfterDiscount p : res) {
                finalPrice += p.priceAfterDiscount() * p.quantity();
            }
            assertEquals(finalPrice, 1160.0);
        }
        catch (Exception e) {
            fail("Exception occurred while applying discount policy");
        }
    }

    @Test
    void andConditionDoesntExist() {
        try {
            List<DiscountConditionDTO> conditions = new LinkedList<>();
            conditions.add(new UnaryConditionDTO(UnaryConditionType.AT_LEAST_NUMBER_OF_ITEMS_FROM_CATEGORY, 4, "category1"));
            conditions.add(new UnaryConditionDTO(UnaryConditionType.AT_LEAST_NUMBER_OF_SOME_PRODUCT, 2, "6"));
            conditions.add(new UnaryConditionDTO(UnaryConditionType.AT_LEAST_SOME_PRICE, 1500, ""));
            DiscountConditionDTO condition = new MultipleConditionDTO(MultipleConditionType.AND, conditions);

            DiscountComponentDTO discountComponent = new SimpleDiscountDTO(HierarchyLevel.StoreLevel, "", 50);
            DiscountComponentDTO discount = new ComplexDiscountDTO(condition, discountComponent);
            discountManager.changeDiscountPolicy(discount);
            ProductAfterDiscount[] res = discountManager.applyDiscountPolicy(cart);
            double finalPrice = 0;
            for (ProductAfterDiscount p : res) {
                finalPrice += p.priceAfterDiscount() * p.quantity();
            }
            assertEquals(finalPrice, 1160.0);
        }
        catch (Exception e) {
            fail("Exception occurred while applying discount policy");
        }
    }

}