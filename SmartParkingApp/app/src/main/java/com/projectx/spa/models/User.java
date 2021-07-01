package com.projectx.spa.models;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.Timestamp;

public class User {
    private String userId, name, email, phoneNo;
    private Timestamp createdTime;

    public User() {
    }

    public User(@Nullable String userId, @NonNull String name, @NonNull String email,
                @NonNull String phoneNo, @NonNull Timestamp createdTime) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.phoneNo = phoneNo;
        this.createdTime = createdTime;
    }

    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public Timestamp getCreatedTime() {
        return createdTime;
    }

    @NonNull
    @Override
    public String toString() {
        return "User{" +
                "userId='" + userId + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phoneNo='" + phoneNo + '\'' +
                ", createdTime=" + createdTime +
                '}';
    }
}
