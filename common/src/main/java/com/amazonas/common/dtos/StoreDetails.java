package com.amazonas.common.dtos;

import com.amazonas.common.utils.Rating;

public record StoreDetails(String storeId, String storeName, String storeDescription, Rating storeRating) { }
