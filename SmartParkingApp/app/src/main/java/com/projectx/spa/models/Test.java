package com.projectx.spa.models;

import com.google.firebase.Timestamp;

public class Test extends Vehicle{
    private Timestamp entryTime;
    private Timestamp exitTime;

    public Test(Timestamp entryTime, Timestamp exitTime) {
        this.entryTime = entryTime;
        this.exitTime = exitTime;
    }
}