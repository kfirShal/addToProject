package com.amazonas.backend.exceptions;

public class PurchasePolicyException extends Exception {
    public int startIndex;
    public int endIndex;
    public PurchasePolicyException(String message, int startIndex, int endIndex) {
        super(message);
        this.startIndex = startIndex;
        this.endIndex = endIndex;
    }
}
