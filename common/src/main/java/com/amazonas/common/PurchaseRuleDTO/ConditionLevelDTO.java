package com.amazonas.common.PurchaseRuleDTO;

public record ConditionLevelDTO(ConditionLevelType type,
                                String levelIdentifier,
                                int quantity) implements PurchaseRuleDTO {
}
