package com.projectx.spa.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.Timestamp;
import com.projectx.spa.interfaces.Settable;

public class User implements Parcelable, Settable {
    private String id, name, email, phoneNo;
    private Timestamp createdTime;

    public User() {
    }

    public User(@Nullable String id, @NonNull String name, @NonNull String email,
                @NonNull String phoneNo, @NonNull Timestamp createdTime) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phoneNo = phoneNo;
        this.createdTime = createdTime;
    }

    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
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
        id = in.readString();
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
        parcel.writeString(id);
        parcel.writeString(name);
        parcel.writeString(email);
        parcel.writeString(phoneNo);
        parcel.writeParcelable(createdTime, i);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id.equals(user.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @NonNull
    @Override
    public String toString() {
        return "User{" +
                "userId='" + id + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phoneNo='" + phoneNo + '\'' +
                ", createdTime=" + createdTime +
                '}';
    }
}