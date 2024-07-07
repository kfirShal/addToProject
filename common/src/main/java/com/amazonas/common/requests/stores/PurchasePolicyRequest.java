package com.amazonas.common.requests.stores;

import com.amazonas.common.PurchaseRuleDTO.PurchaseRuleDTO;
import com.amazonas.common.requests.Request;
import com.amazonas.common.utils.JsonUtils;

public record PurchasePolicyRequest(String StoreID, PurchaseRuleDTO purchaseRule) {
    public static PurchasePolicyRequest from(String json) {
        return JsonUtils.deserialize(json, PurchasePolicyRequest.class);
    }
}
