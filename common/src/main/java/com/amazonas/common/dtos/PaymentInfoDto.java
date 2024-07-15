package com.amazonas.common.dtos;

public class PaymentInfoDto {
    private String currency;
    private String cardNumber;
    private String month;
    private String year;
    private String holder;
    private String cvv;
    private String id;

    public PaymentInfoDto() {
    }

    public PaymentInfoDto(String currency, String cardNumber, String month, String year, String holder, String cvv, String id) {
        this.currency = currency;
        this.cardNumber = cardNumber;
        this.month = month;
        this.year = year;
        this.holder = holder;
        this.cvv = cvv;
        this.id = id;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public void setCardNumber(String card_number) {
        this.cardNumber = card_number;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public void setHolder(String holder) {
        this.holder = holder;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCurrency() {
        return currency;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public String getMonth() {
        return month;
    }

    public String getYear() {
        return year;
    }

    public String getHolder() {
        return holder;
    }

    public String getCvv() {
        return cvv;
    }

    public String getId() {
        return id;
    }
}
