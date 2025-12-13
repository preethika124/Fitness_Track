package com.example.auth.model;

public class Workout {

    private int id;
    private String email;
    private String workoutType;
    private Integer durationMinutes;
    private Double caloriesBurned;
    private String workoutDate;  // yyyy-MM-dd

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getWorkoutType() { return workoutType; }
    public void setWorkoutType(String workoutType) { this.workoutType = workoutType; }

    public Integer getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }

    public Double getCaloriesBurned() { return caloriesBurned; }
    public void setCaloriesBurned(Double caloriesBurned) { this.caloriesBurned = caloriesBurned; }

    public String getWorkoutDate() { return workoutDate; }
    public void setWorkoutDate(String workoutDate) { this.workoutDate = workoutDate; }
}
