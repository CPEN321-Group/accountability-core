package com.cpen321group.accountability.mainscreen.dashboard.functionpack.transaction;

public class TransactionModel {
    private String user_id;
    private String transaction_id;
    private String transaction_title;
    private String transaction_category;
    private String transaction_date;
    private double transaction_dollars;
    private boolean isIncome;
    private String receiptURL;

    public TransactionModel(String user_id, String transaction_id, String transaction_title,
                            String transaction_category, String transaction_date,
                            double transaction_dollars, boolean isIncome, String receiptURL) {
        this.user_id = user_id;
        this.transaction_id = transaction_id;
        this.transaction_title = transaction_title;
        this.transaction_category = transaction_category;
        this.transaction_date = transaction_date;
        this.transaction_dollars = transaction_dollars;
        this.isIncome = isIncome;
        this.receiptURL = receiptURL;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public void setTransaction_id(String transaction_id) {
        this.transaction_id = transaction_id;
    }

    public void setTransaction_title(String transaction_title) {
        this.transaction_title = transaction_title;
    }

    public void setTransaction_category(String transaction_category) {
        this.transaction_category = transaction_category;
    }

    public void setTransaction_date(String transaction_date) {
        this.transaction_date = transaction_date;
    }

    public void setTransaction_cents(double transaction_dollars) {
        this.transaction_dollars = transaction_dollars;
    }

    public void setIncome(boolean income) {
        isIncome = income;
    }

    public void setReceiptURL(String receiptURL) {
        this.receiptURL = receiptURL;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getTransaction_id() {
        return transaction_id;
    }

    public String getTransaction_title() {
        return transaction_title;
    }

    public String getTransaction_category() {
        return transaction_category;
    }

    public String getTransaction_date() {
        return transaction_date;
    }

    public double getTransaction_cents() {
        return transaction_dollars;
    }

    public String getReceiptURL() {
        return receiptURL;
    }

    public boolean isIncome() {
        return isIncome;
    }
}
