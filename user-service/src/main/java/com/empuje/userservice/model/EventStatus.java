package com.empuje.userservice.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entity representing the status of an event.
 */
@Entity
@Table(name = "event_status")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventStatus extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 50)
    private String name;

    @Column(length = 255)
    private String description;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EventStatus that = (EventStatus) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "EventStatus{" +
               "id=" + id +
               ", name='" + name + '\'' +
               ", active=" + active +
               '}';
    }
}
