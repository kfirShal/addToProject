package com.amazonas.backend.service.requests.store;

import com.amazonas.backend.business.stores.search.SearchRequest;

public record SearchInStoreRequest(String storeId, SearchRequest searchRequest) {
}
