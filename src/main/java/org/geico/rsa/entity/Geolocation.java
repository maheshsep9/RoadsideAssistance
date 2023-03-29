package org.geico.rsa.entity;

import ch.hsr.geohash.GeoHash;
import lombok.Data;
@Data
public class Geolocation {
    private static final int GEO_HASH_PRECISION_4 = 4;
    private static final int GEO_HASH_PRECISION_5 = 5;
    private static final int GEO_HASH_PRECISION_6 = 6;  //more precise location to define the grid
    private double latitude;
    private double longitude;
    private final GeoHash geoHash;

    public Geolocation(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.geoHash = GeoHash.withCharacterPrecision(latitude, longitude, GEO_HASH_PRECISION_5);
    }
}
