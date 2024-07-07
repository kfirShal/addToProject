package com.amazonas.common.PurchaseRuleDTO;

/**
 *
 * @param type
 * @param data contains the argument for the relevant <code>type</code>
 */
public record NumericalPurchaseRuleDTO(NumericalPurchaseRuleType type,
                                       int data)
        implements PurchaseRuleDTO {
}
