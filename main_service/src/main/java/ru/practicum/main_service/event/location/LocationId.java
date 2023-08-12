package ru.practicum.main_service.event.location;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class LocationId implements Serializable {
    private Float lat;
    private Float lon;

    public LocationId(Float lat, Float lon) {
        this.lat = lat;
        this.lon = lon;
    }
}
