package com.amazonas.common.PurchaseRuleDTO;

import java.time.temporal.Temporal;

public record DatePurchaseRuleDTO(DatePurchaseRuleType type,
                                  Temporal beginningRange,
                                  Temporal endRange)
        implements PurchaseRuleDTO {
}
