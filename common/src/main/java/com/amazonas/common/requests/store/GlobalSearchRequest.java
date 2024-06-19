package com.amazonas.common.requests.store;

import com.amazonas.common.utils.Rating;

public record GlobalSearchRequest(Rating storeRating, SearchRequest searchRequest) {

}
