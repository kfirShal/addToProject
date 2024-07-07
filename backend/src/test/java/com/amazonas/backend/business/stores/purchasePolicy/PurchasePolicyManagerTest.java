package com.amazonas.backend.business.stores.purchasePolicy;

import com.amazonas.backend.business.stores.discountPolicies.DiscountDTOs.UnaryConditionDTO;
import com.amazonas.backend.business.stores.discountPolicies.DiscountManager;
import com.amazonas.backend.business.stores.discountPolicies.ProductAfterDiscount;
import com.amazonas.backend.business.stores.discountPolicies.ProductWithQuantitiy;
import com.amazonas.backend.business.stores.purchasePolicy.PurchaseRule.AgeRestrictionRule;
import com.amazonas.backend.business.stores.purchasePolicy.PurchaseRule.PurchaseRule;
import com.amazonas.backend.business.userProfiles.RegisteredUser;
import com.amazonas.common.PurchaseRuleDTO.*;
import com.amazonas.common.dtos.Product;
import com.amazonas.common.permissions.profiles.PermissionsProfile;
import com.amazonas.common.utils.Rating;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PurchasePolicyManagerTest {
    List<ProductWithQuantitiy> cart;
    PurchasePolicyManager purchasePolicyManager;
    RegisteredUser user;

    @BeforeEach
    void setUp() {
        purchasePolicyManager = new PurchasePolicyManager();
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
        user = mock(RegisteredUser.class);
        when(user.getBirthDate()).thenReturn(LocalDate.now().minusYears(22));
    }

    @Test
    void noPolicy() {
        try {
            assertTrue(purchasePolicyManager.isSatisfied(cart, user));
        }
        catch (Exception e) {
            fail("Exception occurred while applying empty discount policy");
        }
    }

    @Test
    void ageRestrictionRuleSuccess() {
        try {
            NumericalPurchaseRuleDTO ageRestrictionRule1 = new NumericalPurchaseRuleDTO(NumericalPurchaseRuleType.AGE_RESTRICTION, 22);
            NumericalPurchaseRuleDTO ageRestrictionRule2 = new NumericalPurchaseRuleDTO(NumericalPurchaseRuleType.AGE_RESTRICTION, 21);
            purchasePolicyManager.changePurchasePolicy(ageRestrictionRule1);
            assertTrue(purchasePolicyManager.isSatisfied(cart, user));
            purchasePolicyManager.changePurchasePolicy(ageRestrictionRule2);
            assertTrue(purchasePolicyManager.isSatisfied(cart, user));
        }
        catch (Exception e) {
            fail("Exception occurred while applying empty discount policy");
        }
    }

    @Test
    void ageRestrictionRuleFail() {
        try {
            NumericalPurchaseRuleDTO ageRestrictionRule1 = new NumericalPurchaseRuleDTO(NumericalPurchaseRuleType.AGE_RESTRICTION, 23);
            purchasePolicyManager.changePurchasePolicy(ageRestrictionRule1);
            assertFalse(purchasePolicyManager.isSatisfied(cart, user));
            when(user.getBirthDate()).thenReturn(LocalDate.now().minusYears(22).minusDays(1));
            assertFalse(purchasePolicyManager.isSatisfied(cart, user));
        }
        catch (Exception e) {
            fail("Exception occurred while applying empty discount policy");
        }
    }

    @Test
    void maxUniqueProductsSuccess() {
        try {
            NumericalPurchaseRuleDTO rule1 = new NumericalPurchaseRuleDTO(NumericalPurchaseRuleType.MAX_UNIQUE_PRODUCTS, 7);
            NumericalPurchaseRuleDTO rule2 = new NumericalPurchaseRuleDTO(NumericalPurchaseRuleType.MAX_UNIQUE_PRODUCTS, 8);
            purchasePolicyManager.changePurchasePolicy(rule1);
            assertTrue(purchasePolicyManager.isSatisfied(cart, user));
            purchasePolicyManager.changePurchasePolicy(rule2);
            assertTrue(purchasePolicyManager.isSatisfied(cart, user));
        }
        catch (Exception e) {
            fail("Exception occurred while applying empty discount policy");
        }
    }

    @Test
    void maxUniqueProductsFail() {
        try {
            NumericalPurchaseRuleDTO rule1 = new NumericalPurchaseRuleDTO(NumericalPurchaseRuleType.MAX_UNIQUE_PRODUCTS, 7);
            purchasePolicyManager.changePurchasePolicy(rule1);
            assertFalse(purchasePolicyManager.isSatisfied(cart, user));
        }
        catch (Exception e) {
            fail("Exception occurred while applying empty discount policy");
        }
    }

    @Test
    void minUniqueProductsSuccess() {
        try {
            NumericalPurchaseRuleDTO rule1 = new NumericalPurchaseRuleDTO(NumericalPurchaseRuleType.MIN_UNIQUE_PRODUCTS, 0);
            NumericalPurchaseRuleDTO rule2 = new NumericalPurchaseRuleDTO(NumericalPurchaseRuleType.MIN_UNIQUE_PRODUCTS, 7);
            purchasePolicyManager.changePurchasePolicy(rule1);
            assertTrue(purchasePolicyManager.isSatisfied(cart, user));
            purchasePolicyManager.changePurchasePolicy(rule2);
            assertTrue(purchasePolicyManager.isSatisfied(cart, user));
        }
        catch (Exception e) {
            fail("Exception occurred while applying empty discount policy");
        }
    }

    @Test
    void minUniqueProductsFail() {
        try {
            NumericalPurchaseRuleDTO rule1 = new NumericalPurchaseRuleDTO(NumericalPurchaseRuleType.MIN_UNIQUE_PRODUCTS, 8);
            purchasePolicyManager.changePurchasePolicy(rule1);
            assertFalse(purchasePolicyManager.isSatisfied(cart, user));
        }
        catch (Exception e) {
            fail("Exception occurred while applying empty discount policy");
        }
    }

    @Test
    void conditionalCategoryRuleSuccess1() {
        try {
            NumericalPurchaseRuleDTO rule = new NumericalPurchaseRuleDTO(NumericalPurchaseRuleType.MIN_UNIQUE_PRODUCTS, 7);
            ConditionLevelDTO cond = new ConditionLevelDTO(ConditionLevelType.CATEGORY_LEVEL, "category1", 2);
            ConditionalPurchaseRuleDTO rule1 = new ConditionalPurchaseRuleDTO(cond, rule);
            purchasePolicyManager.changePurchasePolicy(rule1);
            assertTrue(purchasePolicyManager.isSatisfied(cart, user));
        }
        catch (Exception e) {
            fail("Exception occurred while applying empty discount policy");
        }
    }

    @Test
    void conditionalCategoryRuleSuccess2() {
        try {
            NumericalPurchaseRuleDTO rule = new NumericalPurchaseRuleDTO(NumericalPurchaseRuleType.MIN_UNIQUE_PRODUCTS, 8);
            ConditionLevelDTO cond = new ConditionLevelDTO(ConditionLevelType.CATEGORY_LEVEL, "category1", 4);
            ConditionalPurchaseRuleDTO rule1 = new ConditionalPurchaseRuleDTO(cond, rule);
            purchasePolicyManager.changePurchasePolicy(rule1);
            assertTrue(purchasePolicyManager.isSatisfied(cart, user));
        }
        catch (Exception e) {
            fail("Exception occurred while applying empty discount policy");
        }
    }

    @Test
    void conditionalCategoryRuleFail() {
        try {
            NumericalPurchaseRuleDTO rule = new NumericalPurchaseRuleDTO(NumericalPurchaseRuleType.MIN_UNIQUE_PRODUCTS, 8);
            ConditionLevelDTO cond = new ConditionLevelDTO(ConditionLevelType.CATEGORY_LEVEL, "category1", 2);
            ConditionalPurchaseRuleDTO rule1 = new ConditionalPurchaseRuleDTO(cond, rule);
            purchasePolicyManager.changePurchasePolicy(rule1);
            assertFalse(purchasePolicyManager.isSatisfied(cart, user));
        }
        catch (Exception e) {
            fail("Exception occurred while applying empty discount policy");
        }
    }

    @Test
    void conditionalProductRuleSuccess1() {
        try {
            NumericalPurchaseRuleDTO rule = new NumericalPurchaseRuleDTO(NumericalPurchaseRuleType.MIN_UNIQUE_PRODUCTS, 7);
            ConditionLevelDTO cond = new ConditionLevelDTO(ConditionLevelType.PRODUCT_LEVEL, "1", 1);
            ConditionalPurchaseRuleDTO rule1 = new ConditionalPurchaseRuleDTO(cond, rule);
            purchasePolicyManager.changePurchasePolicy(rule1);
            assertTrue(purchasePolicyManager.isSatisfied(cart, user));
        }
        catch (Exception e) {
            fail("Exception occurred while applying empty discount policy");
        }
    }

    @Test
    void conditionalProductRuleSuccess2() {
        try {
            NumericalPurchaseRuleDTO rule = new NumericalPurchaseRuleDTO(NumericalPurchaseRuleType.MIN_UNIQUE_PRODUCTS, 8);
            ConditionLevelDTO cond = new ConditionLevelDTO(ConditionLevelType.PRODUCT_LEVEL, "1", 2);
            ConditionalPurchaseRuleDTO rule1 = new ConditionalPurchaseRuleDTO(cond, rule);
            purchasePolicyManager.changePurchasePolicy(rule1);
            assertTrue(purchasePolicyManager.isSatisfied(cart, user));
        }
        catch (Exception e) {
            fail("Exception occurred while applying empty discount policy");
        }
    }

    @Test
    void conditionalProductRuleFail() {
        try {
            NumericalPurchaseRuleDTO rule = new NumericalPurchaseRuleDTO(NumericalPurchaseRuleType.MIN_UNIQUE_PRODUCTS, 8);
            ConditionLevelDTO cond = new ConditionLevelDTO(ConditionLevelType.PRODUCT_LEVEL, "1", 1);
            ConditionalPurchaseRuleDTO rule1 = new ConditionalPurchaseRuleDTO(cond, rule);
            purchasePolicyManager.changePurchasePolicy(rule1);
            assertFalse(purchasePolicyManager.isSatisfied(cart, user));
        }
        catch (Exception e) {
            fail("Exception occurred while applying empty discount policy");
        }
    }

    @Test
    void dayRestrictionRuleSuccess1() {
        try {
            DatePurchaseRuleDTO rule = new DatePurchaseRuleDTO(DatePurchaseRuleType.DAY_RESTRICTION, LocalDate.now().minusDays(1), LocalDate.now());
            purchasePolicyManager.changePurchasePolicy(rule);
            assertTrue(purchasePolicyManager.isSatisfied(cart, user));
        }
        catch (Exception e) {
            fail("Exception occurred while applying empty discount policy");
        }
    }

    @Test
    void dayRestrictionRuleSuccess2() {
        try {
            DatePurchaseRuleDTO rule = new DatePurchaseRuleDTO(DatePurchaseRuleType.DAY_RESTRICTION, LocalDate.now(), LocalDate.now().plusDays(1));
            purchasePolicyManager.changePurchasePolicy(rule);
            assertTrue(purchasePolicyManager.isSatisfied(cart, user));
        }
        catch (Exception e) {
            fail("Exception occurred while applying empty discount policy");
        }
    }

    @Test
    void dayRestrictionRuleSuccess3() {
        try {
            DatePurchaseRuleDTO rule = new DatePurchaseRuleDTO(DatePurchaseRuleType.DAY_RESTRICTION, LocalDate.now().minusDays(1), LocalDate.now().plusDays(1));
            purchasePolicyManager.changePurchasePolicy(rule);
            assertTrue(purchasePolicyManager.isSatisfied(cart, user));
        }
        catch (Exception e) {
            fail("Exception occurred while applying empty discount policy");
        }
    }

    @Test
    void dayRestrictionRuleFail1() {
        try {
            DatePurchaseRuleDTO rule = new DatePurchaseRuleDTO(DatePurchaseRuleType.DAY_RESTRICTION, LocalDate.now().minusDays(2), LocalDate.now().minusDays(1));
            purchasePolicyManager.changePurchasePolicy(rule);
            assertTrue(purchasePolicyManager.isSatisfied(cart, user));
        }
        catch (Exception e) {
            fail("Exception occurred while applying empty discount policy");
        }
    }

    @Test
    void dayRestrictionRuleFail2() {
        try {
            DatePurchaseRuleDTO rule = new DatePurchaseRuleDTO(DatePurchaseRuleType.DAY_RESTRICTION, LocalDate.now().plusDays(1), LocalDate.now().plusDays(2));
            purchasePolicyManager.changePurchasePolicy(rule);
            assertTrue(purchasePolicyManager.isSatisfied(cart, user));
        }
        catch (Exception e) {
            fail("Exception occurred while applying empty discount policy");
        }
    }

    @Test
    void hoursRestrictionRuleSuccess1() {
        try {
            DatePurchaseRuleDTO rule = new DatePurchaseRuleDTO(DatePurchaseRuleType.HOUR_RESTRICTION, LocalTime.now().minusHours(1), LocalTime.now());
            purchasePolicyManager.changePurchasePolicy(rule);
            assertTrue(purchasePolicyManager.isSatisfied(cart, user));
        }
        catch (Exception e) {
            fail("Exception occurred while applying empty discount policy");
        }
    }

    @Test
    void hoursRestrictionRuleSuccess2() {
        try {
            DatePurchaseRuleDTO rule = new DatePurchaseRuleDTO(DatePurchaseRuleType.HOUR_RESTRICTION, LocalTime.now(), LocalTime.now().plusHours(1));
            purchasePolicyManager.changePurchasePolicy(rule);
            assertTrue(purchasePolicyManager.isSatisfied(cart, user));
        }
        catch (Exception e) {
            fail("Exception occurred while applying empty discount policy");
        }
    }

    @Test
    void hoursRestrictionRuleSuccess3() {
        try {
            DatePurchaseRuleDTO rule = new DatePurchaseRuleDTO(DatePurchaseRuleType.HOUR_RESTRICTION, LocalTime.now().minusHours(1), LocalTime.now().plusHours(1));
            purchasePolicyManager.changePurchasePolicy(rule);
            assertTrue(purchasePolicyManager.isSatisfied(cart, user));
        }
        catch (Exception e) {
            fail("Exception occurred while applying empty discount policy");
        }
    }

    @Test
    void hoursRestrictionRuleFail1() {
        try {
            DatePurchaseRuleDTO rule = new DatePurchaseRuleDTO(DatePurchaseRuleType.HOUR_RESTRICTION, LocalTime.now().minusHours(2), LocalTime.now().minusHours(1));
            purchasePolicyManager.changePurchasePolicy(rule);
            assertTrue(purchasePolicyManager.isSatisfied(cart, user));
        }
        catch (Exception e) {
            fail("Exception occurred while applying empty discount policy");
        }
    }

    @Test
    void hoursRestrictionRuleFail2() {
        try {
            DatePurchaseRuleDTO rule = new DatePurchaseRuleDTO(DatePurchaseRuleType.HOUR_RESTRICTION, LocalTime.now().plusHours(1), LocalTime.now().plusHours(2));
            purchasePolicyManager.changePurchasePolicy(rule);
            assertTrue(purchasePolicyManager.isSatisfied(cart, user));
        }
        catch (Exception e) {
            fail("Exception occurred while applying empty discount policy");
        }
    }

    @Test
    void andRuleSuccess() {
        try {
            List<PurchaseRuleDTO> rules = new LinkedList<>();
            rules.add(new NumericalPurchaseRuleDTO(NumericalPurchaseRuleType.MIN_UNIQUE_PRODUCTS, 7));
            rules.add(new DatePurchaseRuleDTO(DatePurchaseRuleType.HOUR_RESTRICTION, LocalTime.now().plusHours(1), LocalTime.now().plusHours(2)));
            rules.add(new NumericalPurchaseRuleDTO(NumericalPurchaseRuleType.AGE_RESTRICTION, 22));
            MultiplePurchaseRuleDTO rule = new MultiplePurchaseRuleDTO(MultiplePurchaseRuleType.AND, rules);
            purchasePolicyManager.changePurchasePolicy(rule);
            assertTrue(purchasePolicyManager.isSatisfied(cart, user));
        }
        catch (Exception e) {
            fail("Exception occurred while applying empty discount policy");
        }
    }

    @Test
    void andRuleFail() {
        try {
            List<PurchaseRuleDTO> rules = new LinkedList<>();
            rules.add(new NumericalPurchaseRuleDTO(NumericalPurchaseRuleType.MIN_UNIQUE_PRODUCTS, 8));
            rules.add(new DatePurchaseRuleDTO(DatePurchaseRuleType.HOUR_RESTRICTION, LocalTime.now().plusHours(1), LocalTime.now().plusHours(2)));
            rules.add(new NumericalPurchaseRuleDTO(NumericalPurchaseRuleType.AGE_RESTRICTION, 22));
            MultiplePurchaseRuleDTO rule = new MultiplePurchaseRuleDTO(MultiplePurchaseRuleType.AND, rules);
            purchasePolicyManager.changePurchasePolicy(rule);
            assertFalse(purchasePolicyManager.isSatisfied(cart, user));
        }
        catch (Exception e) {
            fail("Exception occurred while applying empty discount policy");
        }
    }

    @Test
    void orRuleSuccess() {
        try {
            List<PurchaseRuleDTO> rules = new LinkedList<>();
            rules.add(new NumericalPurchaseRuleDTO(NumericalPurchaseRuleType.MIN_UNIQUE_PRODUCTS, 8));
            rules.add(new DatePurchaseRuleDTO(DatePurchaseRuleType.HOUR_RESTRICTION, LocalTime.now().plusHours(1), LocalTime.now().plusHours(2)));
            rules.add(new NumericalPurchaseRuleDTO(NumericalPurchaseRuleType.AGE_RESTRICTION, 30));
            MultiplePurchaseRuleDTO rule = new MultiplePurchaseRuleDTO(MultiplePurchaseRuleType.OR, rules);
            purchasePolicyManager.changePurchasePolicy(rule);
            assertTrue(purchasePolicyManager.isSatisfied(cart, user));
        }
        catch (Exception e) {
            fail("Exception occurred while applying empty discount policy");
        }
    }

    @Test
    void orRuleFail() {
        try {
            List<PurchaseRuleDTO> rules = new LinkedList<>();
            rules.add(new NumericalPurchaseRuleDTO(NumericalPurchaseRuleType.MIN_UNIQUE_PRODUCTS, 8));
            rules.add(new DatePurchaseRuleDTO(DatePurchaseRuleType.HOUR_RESTRICTION, LocalTime.now(), LocalTime.now().plusHours(2)));
            rules.add(new NumericalPurchaseRuleDTO(NumericalPurchaseRuleType.AGE_RESTRICTION, 30));
            MultiplePurchaseRuleDTO rule = new MultiplePurchaseRuleDTO(MultiplePurchaseRuleType.OR, rules);
            purchasePolicyManager.changePurchasePolicy(rule);
            assertFalse(purchasePolicyManager.isSatisfied(cart, user));
        }
        catch (Exception e) {
            fail("Exception occurred while applying empty discount policy");
        }
    }
}