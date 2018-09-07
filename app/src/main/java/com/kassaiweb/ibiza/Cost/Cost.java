package com.kassaiweb.ibiza.Cost;

import java.util.ArrayList;

public class Cost {

    private String id;
    private String description;
    private int total;
    private ArrayList<CostPerson> costPersonArrayList = new ArrayList<>();

    public Cost() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public ArrayList<CostPerson> getCostPersons() {
        return costPersonArrayList;
    }

    public void setCostPersons(ArrayList<CostPerson> costPersonArrayList) {
        this.costPersonArrayList = costPersonArrayList;
    }
}
