package com.amazonas.service.requests.store;

import com.amazonas.business.stores.search.SearchRequest;

public record SearchInStoreRequest(String storeId, SearchRequest searchRequest) {
}
