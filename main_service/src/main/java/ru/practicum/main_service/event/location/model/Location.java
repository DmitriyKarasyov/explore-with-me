package ru.practicum.main_service.event.location.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.main_service.event.location.LocationId;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@IdClass(LocationId.class)
@Table(name = "locations", schema = "public")
public class Location {
    @Id
    @Column(name = "lat", nullable = false)
    private Float lat;
    @Id
    @Column(name = "lon", nullable = false)
    private Float lon;
}
