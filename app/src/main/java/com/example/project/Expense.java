package com.example.project;
import java.util.Date;

public class Expense {
    private String id;
    private double amount;
    private String description;
    private String category;
    private Date date;

    public Expense(double amount, String description, String category, Date date) {
        this.amount = amount;
        this.description = description;
        this.category = category;
        this.date = date;
    }

    // Gettery a settery
    public double getAmount() { return amount; }
    public String getDescription() { return description; }
    public String getCategory() { return category; }
    public Date getDate() { return date; }


}
