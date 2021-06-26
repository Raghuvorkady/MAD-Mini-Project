package com.projectx.spa.models;

import android.util.Log;

import com.google.firebase.Timestamp;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ParkingSlot implements Serializable {
    private String id, location, landmark;
    private int totalSpace, availableSpace;
    private String createdTime;

    public ParkingSlot() {
    }

    public ParkingSlot(UUID id, String location, String landmark, int totalSpace, int availableSpace, Timestamp createdTime) {
        this.id = id.toString();
        this.location = location;
        this.landmark = landmark;
        this.totalSpace = totalSpace;
        this.availableSpace = availableSpace;
        this.createdTime = createdTime.toString();
    }

    public String getLocation() {
        return location;
    }

    public String getLandmark() {
        return landmark;
    }

    public String getId() {
        return id;
    }

    public int getTotalSpace() {
        return totalSpace;
    }

    public int getAvailableSpace() {
        return availableSpace;
    }

    public String getCreatedTime() {
        long seconds = 0;
        int nanoseconds = 0;
        try {
            String[] strings = createdTime.split("=");
            seconds = Long.parseLong(strings[1].split(",")[0]);
            nanoseconds = Integer.parseInt(strings[2].split("\\)")[0]);
            Log.d("Date", "seconds " + seconds + "\nnanoseconds " + nanoseconds);
        } catch (Exception e) {
            Log.d("Date", "Error");
        }
        Timestamp timestamp = new Timestamp(seconds, nanoseconds);
        return timestamp.toDate().toString();
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        for (Field field : this.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try {
                map.put(field.getName(), field.get(this));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return map;
    }

    @Override
    public String toString() {
        return "ParkingSlot{" +
                "id='" + id + '\'' +
                ", location='" + location + '\'' +
                ", landmark='" + landmark + '\'' +
                ", totalSpace=" + totalSpace +
                ", availableSpace=" + availableSpace +
                ", createdTime='" + createdTime + '\'' +
                '}';
    }
}