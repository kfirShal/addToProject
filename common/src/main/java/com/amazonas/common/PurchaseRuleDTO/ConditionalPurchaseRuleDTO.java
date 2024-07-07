package com.amazonas.common.PurchaseRuleDTO;

public record ConditionalPurchaseRuleDTO(ConditionLevelDTO conditionLevelDTO,
                                         PurchaseRuleDTO purchaseRuleDTO)
        implements PurchaseRuleDTO {
}
