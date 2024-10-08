package com.example.to_do_list_application;
// File Location: app/src/main/java/com/example/todolistapp/Task.java

import com.google.firebase.firestore.Exclude;

public class Task {
    @Exclude
    private String id; // Firestore document ID
    private String title;
    private String description;
    private String priority;

    // Required no-argument constructor for Firestore
    public Task() {}

    public Task(String title, String description, String priority) {
        this.title = title;
        this.description = description;
        this.priority = priority;
    }

    // Getters and Setters

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }

    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }

    public String getPriority() { return priority; }

    public void setPriority(String priority) { this.priority = priority; }
}