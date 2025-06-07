package com.hevttc.bigwork.bean;

public class Todo {
    private int id;
    private String content;
    private String createdAt;
    private boolean completed;

    public Todo() {
    }

    public Todo(int id, String content, String createdAt, boolean completed) {
        this.id = id;
        this.content = content;
        this.createdAt = createdAt;
        this.completed = completed;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}
