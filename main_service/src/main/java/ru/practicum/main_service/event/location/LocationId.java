package ru.practicum.main_service.event.location;

import java.io.Serializable;

public class LocationId implements Serializable {
    private final Float lat;
    private final Float lon;

    public LocationId(Float lat, Float lon) {
        this.lat = lat;
        this.lon = lon;
    }
}
