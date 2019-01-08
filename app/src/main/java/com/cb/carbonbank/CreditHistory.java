package com.cb.carbonbank;

public class CreditHistory {
    private String timestamp;
    private String description;
    private int amount;
    private int direction;

    public CreditHistory() {
    }

    public CreditHistory(String timestamp,String description ,int amount, int direction) {
        this.timestamp = timestamp;
        this.amount = amount;
        this.direction = direction;
        this.description=description;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
