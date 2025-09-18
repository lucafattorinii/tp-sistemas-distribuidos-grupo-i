package com.empuje.userservice.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entity representing a category for inventory items.
 */
@Entity
@Table(name = "inventory_categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryCategory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InventoryCategory that = (InventoryCategory) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "InventoryCategory{" +
               "id=" + id +
               ", name='" + name + '\'' +
               ", active=" + active +
               '}';
    }
}
