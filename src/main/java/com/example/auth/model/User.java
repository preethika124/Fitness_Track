package com.example.auth.model;

public class User {

    private int id;
    private String email;
    private String passwordHash;
    private String firstName;
    private String lastName;
    private String role;

    private Integer age;
    private Double weight;

    // 🎯 GOALS (NEW)
    private Integer weeklyExerciseGoal;
    private Double dailyWaterGoal;
    private Double dailySleepGoal;

    /* ================= GETTERS ================= */

    public int getId() { return id; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getRole() { return role; }

    public Integer getAge() { return age; }
    public Double getWeight() { return weight; }

    public Integer getWeeklyExerciseGoal() { return weeklyExerciseGoal; }
    public Double getDailyWaterGoal() { return dailyWaterGoal; }
    public Double getDailySleepGoal() { return dailySleepGoal; }

    /* ================= SETTERS ================= */

    public void setId(int id) { this.id = id; }
    public void setEmail(String email) { this.email = email; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setRole(String role) { this.role = role; }

    public void setAge(Integer age) { this.age = age; }
    public void setWeight(Double weight) { this.weight = weight; }

    public void setWeeklyExerciseGoal(Integer weeklyExerciseGoal) {
        this.weeklyExerciseGoal = weeklyExerciseGoal;
    }

    public void setDailyWaterGoal(Double dailyWaterGoal) {
        this.dailyWaterGoal = dailyWaterGoal;
    }

    public void setDailySleepGoal(Double dailySleepGoal) {
        this.dailySleepGoal = dailySleepGoal;
    }
}
