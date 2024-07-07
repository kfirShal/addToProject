package com.amazonas.common.PurchaseRuleDTO;

import java.util.List;

public record MultiplePurchaseRuleDTO(MultiplePurchaseRuleType type,
                                      List<PurchaseRuleDTO> purchaseRules)
        implements PurchaseRuleDTO {
}
