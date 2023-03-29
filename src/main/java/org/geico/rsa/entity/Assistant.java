package org.geico.rsa.entity;

import lombok.Data;

@Data
public class Assistant   implements Comparable<Assistant>{
    private String id;
    private Geolocation location;
    private double distance;
    private boolean isAvailable;

    public Assistant(String id, Geolocation location, boolean isAvailable) {
        this.id = id;
        this.location = location;
        this.isAvailable = isAvailable;
    }
    @Override
    public int compareTo(Assistant o) {
        return this.id.compareTo(o.id);
    }
}


