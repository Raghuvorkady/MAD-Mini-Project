package com.projectx.spa.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.Timestamp;

public class User implements Parcelable {
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

    // Parcelable implementations
    protected User(Parcel in) {
        userId = in.readString();
        name = in.readString();
        email = in.readString();
        phoneNo = in.readString();
        createdTime = in.readParcelable(Timestamp.class.getClassLoader());
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(userId);
        parcel.writeString(name);
        parcel.writeString(email);
        parcel.writeString(phoneNo);
        parcel.writeParcelable(createdTime, i);
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
