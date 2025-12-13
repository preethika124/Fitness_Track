package com.example.auth.model;

public class BMI {
    private int id;
    private String email;
    private double bmiValue;
    private String status;
    private String recordedAt;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public double getBmiValue() { return bmiValue; }
    public void setBmiValue(double bmiValue) { this.bmiValue = bmiValue; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getRecordedAt() { return recordedAt; }
    public void setRecordedAt(String recordedAt) { this.recordedAt = recordedAt; }
}
