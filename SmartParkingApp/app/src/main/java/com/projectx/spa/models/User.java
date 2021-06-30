package com.projectx.spa.models;

import androidx.annotation.NonNull;

import java.util.UUID;

public class User {
    private String userId, name, email, phoneNo;

    public User() {
    }

    public User(UUID userId, String name, String email, String phoneNo) {
        this.userId = userId.toString();
        this.name = name;
        this.email = email;
        this.phoneNo = phoneNo;
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

    @NonNull
    @Override
    public String toString() {
        return "User{" +
                "userId='" + userId + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phoneNo='" + phoneNo + '\'' +
                '}';
    }
}
