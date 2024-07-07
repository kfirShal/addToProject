package com.amazonas.backend.business.stores.purchasePolicy;

import com.amazonas.backend.business.stores.discountPolicies.ProductWithQuantitiy;
import com.amazonas.backend.business.stores.purchasePolicy.PurchaseRule.*;
import com.amazonas.backend.business.userProfiles.RegisteredUser;
import com.amazonas.backend.exceptions.StoreException;
import com.amazonas.common.PurchaseRuleDTO.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class PurchasePolicyManager {
    private PurchaseRule purchasePolicy;

    public PurchasePolicyManager() {
        purchasePolicy = null;
    }

    public void changePurchasePolicy(PurchaseRuleDTO purchasePolicy) throws StoreException {
        if (purchasePolicy == null) {
            throw new StoreException("The new purchase policy cannot be empty");
        }
        this.purchasePolicy = translateFromDTO(purchasePolicy);
    }

    public PurchaseRuleDTO getPurchasePolicy () throws StoreException {
        if (purchasePolicy == null) {
            return null;
        }
        return translateToDTO(purchasePolicy);
    }

    public boolean deletePurchasePolicy() {
        purchasePolicy = null;
        return true;
    }

    public boolean isSatisfied(List<ProductWithQuantitiy> products, RegisteredUser user) {
        if(purchasePolicy == null) {
            return true;
        }
        return purchasePolicy.isSatisfied(products, user);
    }


    //====================================================================== |
    //============================ TRANSLATION ============================= |
    //====================================================================== |

    private PurchaseRule translateFromDTO(PurchaseRuleDTO purchasePolicyDTO) throws StoreException {
        switch (purchasePolicyDTO) {
            case null -> throw new StoreException("Purchase policy cannot be empty");

            case DatePurchaseRuleDTO datePurchaseRuleDTO -> {
                switch (datePurchaseRuleDTO.type()) {
                    case DatePurchaseRuleType.DAY_RESTRICTION -> {
                        if (!(datePurchaseRuleDTO.beginningRange() instanceof LocalDate && datePurchaseRuleDTO.endRange() instanceof LocalDate)) {
                            throw new StoreException("Invalid date range");
                        }
                        return new DayRestrictionRule((LocalDate) datePurchaseRuleDTO.beginningRange(),
                                                      (LocalDate) datePurchaseRuleDTO.endRange());
                    }
                    case DatePurchaseRuleType.HOUR_RESTRICTION -> {
                        if (!(datePurchaseRuleDTO.beginningRange() instanceof LocalTime && datePurchaseRuleDTO.endRange() instanceof LocalTime)) {
                            throw new StoreException("Invalid hours range");
                        }
                        return new HoursRestrictionRule((LocalTime) datePurchaseRuleDTO.beginningRange(),
                                (LocalTime) datePurchaseRuleDTO.endRange());
                    }
                    default -> throw new StoreException("Invalid purchase rule type");
                }
            }

            case MultiplePurchaseRuleDTO multiplePurchaseRuleDTO -> {
                switch (multiplePurchaseRuleDTO.type()) {
                    case MultiplePurchaseRuleType.OR -> {
                        if (multiplePurchaseRuleDTO.purchaseRules() == null) {
                            throw new StoreException("Or's purchase rules cannot be empty");
                        }
                        List<PurchaseRule> purchaseRules = new ArrayList<>();
                        for (PurchaseRuleDTO purchaseRuleDTO : multiplePurchaseRuleDTO.purchaseRules()) {
                            purchaseRules.add(translateFromDTO(purchaseRuleDTO));
                        }
                        return new OrRule(purchaseRules);
                    }
                    case MultiplePurchaseRuleType.AND -> {
                        if (multiplePurchaseRuleDTO.purchaseRules() == null) {
                            throw new StoreException("Or's purchase rules cannot be empty");
                        }
                        List<PurchaseRule> purchaseRules = new ArrayList<>();
                        for (PurchaseRuleDTO purchaseRuleDTO : multiplePurchaseRuleDTO.purchaseRules()) {
                            purchaseRules.add(translateFromDTO(purchaseRuleDTO));
                        }
                        return new AndRule(purchaseRules);
                    }
                    default -> throw new StoreException("Invalid purchase rule type");
                }
            }

            case NumericalPurchaseRuleDTO numericalPurchaseRuleDTO -> {
                switch (numericalPurchaseRuleDTO.type()) {
                    case NumericalPurchaseRuleType.AGE_RESTRICTION -> {
                        return new AgeRestrictionRule(numericalPurchaseRuleDTO.data());
                    }
                    case NumericalPurchaseRuleType.MAX_UNIQUE_PRODUCTS -> {
                        return new MaxUniqueProductsRule(numericalPurchaseRuleDTO.data());
                    }
                    case NumericalPurchaseRuleType.MIN_UNIQUE_PRODUCTS -> {
                        return new MinUniqueProductsRule(numericalPurchaseRuleDTO.data());
                    }
                    default -> throw new StoreException("Invalid purchase rule type");
                }
            }

            case ConditionalPurchaseRuleDTO conditionalPurchaseRuleDTO -> {
                switch (conditionalPurchaseRuleDTO.conditionLevelDTO().type()) {
                    case ConditionLevelType.CATEGORY_LEVEL -> {
                        return new ConditionalCategoryRule(conditionalPurchaseRuleDTO.conditionLevelDTO().levelIdentifier(),
                                                           conditionalPurchaseRuleDTO.conditionLevelDTO().quantity(),
                                                           translateFromDTO(conditionalPurchaseRuleDTO.purchaseRuleDTO()));
                    }
                    case ConditionLevelType.PRODUCT_LEVEL ->  {
                        return new ConditionalProductRule(conditionalPurchaseRuleDTO.conditionLevelDTO().levelIdentifier(),
                                conditionalPurchaseRuleDTO.conditionLevelDTO().quantity(),
                                translateFromDTO(conditionalPurchaseRuleDTO.purchaseRuleDTO()));
                    }
                    default -> throw new StoreException("Invalid purchase rule type");
                }
            }

            default -> throw new IllegalStateException("Invalid purchase policy");
        }
    }

    private PurchaseRuleDTO translateToDTO(PurchaseRule purchasePolicy) throws StoreException {
        switch (purchasePolicy) {
            case null -> throw new StoreException("Something got wrong with getting purchase policy");
            case AgeRestrictionRule ageRestrictionRule -> {
                return new NumericalPurchaseRuleDTO(NumericalPurchaseRuleType.AGE_RESTRICTION,
                                                    ageRestrictionRule.getMinAge());
            }
            case MaxUniqueProductsRule maxUniqueProductsRule -> {
                return new NumericalPurchaseRuleDTO(NumericalPurchaseRuleType.MAX_UNIQUE_PRODUCTS,
                                                    maxUniqueProductsRule.getLimit());
            }
            case MinUniqueProductsRule minUniqueProductsRule -> {
                return new NumericalPurchaseRuleDTO(NumericalPurchaseRuleType.MIN_UNIQUE_PRODUCTS,
                                                    minUniqueProductsRule.getLimit());
            }
            case DayRestrictionRule dayRestrictionRule -> {
                return new DatePurchaseRuleDTO(DatePurchaseRuleType.DAY_RESTRICTION,
                                               dayRestrictionRule.getFirstRestrictedDay(),
                                               dayRestrictionRule.getLastRestrictedDay());
            }
            case HoursRestrictionRule hoursRestrictionRule -> {
                return new DatePurchaseRuleDTO(DatePurchaseRuleType.HOUR_RESTRICTION,
                                               hoursRestrictionRule.getStartRestrictionTime(),
                                               hoursRestrictionRule.getEndRestrictionTime());
            }
            case AndRule andRule -> {
                List<PurchaseRuleDTO> purchaseRuleDTOs = new ArrayList<>();
                for (PurchaseRule purchaseRule : andRule.getRules()) {
                    purchaseRuleDTOs.add(translateToDTO(purchaseRule));
                }
                return new MultiplePurchaseRuleDTO(MultiplePurchaseRuleType.AND,
                                                   purchaseRuleDTOs);
            }
            case OrRule orRule -> {
                List<PurchaseRuleDTO> purchaseRuleDTOs = new ArrayList<>();
                for (PurchaseRule purchaseRule : orRule.getRules()) {
                    purchaseRuleDTOs.add(translateToDTO(purchaseRule));
                }
                return new MultiplePurchaseRuleDTO(MultiplePurchaseRuleType.OR,
                        purchaseRuleDTOs);
            }
            case ConditionalCategoryRule conditionalCategoryRule -> {
                return new ConditionalPurchaseRuleDTO(new ConditionLevelDTO(ConditionLevelType.CATEGORY_LEVEL,
                                                                            conditionalCategoryRule.getCategoryName(),
                                                                            conditionalCategoryRule.getQuantity()),
                                                      translateToDTO(conditionalCategoryRule.getPurchaseRule()));
            }
            case ConditionalProductRule conditionalProductRule -> {
                return new ConditionalPurchaseRuleDTO(new ConditionLevelDTO(ConditionLevelType.PRODUCT_LEVEL,
                                                                            conditionalProductRule.getProductID(),
                                                                            conditionalProductRule.getQuantity()),
                                                      translateToDTO(conditionalProductRule.getPurchaseRule()));
            }
            default -> throw new StoreException("Something got wrong with getting purchase policy");
        }
    }
}
