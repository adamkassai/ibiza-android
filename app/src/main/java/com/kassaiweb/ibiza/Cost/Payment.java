package com.kassaiweb.ibiza.Cost;

public class Payment {

    private String id;
    private String fromId;
    private String toId;
    private int sum;

    public Payment() {
    }

    public Payment(String id, String fromId, String toId, int sum) {
        this.id = id;
        this.fromId = fromId;
        this.toId = toId;
        this.sum = sum;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFromId() {
        return fromId;
    }

    public void setFromId(String fromId) {
        this.fromId = fromId;
    }

    public String getToId() {
        return toId;
    }

    public void setToId(String toId) {
        this.toId = toId;
    }

    public int getSum() {
        return sum;
    }

    public void setSum(int sum) {
        this.sum = sum;
    }
}
