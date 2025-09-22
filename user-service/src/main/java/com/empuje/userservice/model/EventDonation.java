package com.empuje.userservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity representing a donation made for a specific event.
 */
@Entity
@Table(name = "event_donations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventDonation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Column(name = "donor_name", length = 255)
    private String donorName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "donor_user_id")
    private User donorUser;

    @Column(name = "donation_type", length = 50)
    private String donationType;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "estimated_value", precision = 12, scale = 2)
    private BigDecimal estimatedValue;

    @Column(length = 50)
    private String status;

    @Column(name = "delivery_date")
    private LocalDateTime deliveryDate;

    @Column(name = "delivery_address", length = 500)
    private String deliveryAddress;

    @Column(name = "delivery_contact_name", length = 100)
    private String deliveryContactName;

    @Column(name = "delivery_contact_phone", length = 20)
    private String deliveryContactPhone;

    @Column(name = "delivery_notes", columnDefinition = "TEXT")
    private String deliveryNotes;

    @Column(name = "is_anonymous", nullable = false)
    private boolean anonymous = false;

    @Column(name = "receipt_required", nullable = false)
    private boolean receiptRequired = false;

    @Column(name = "receipt_number", length = 50)
    private String receiptNumber;

    @Column(name = "receipt_issued_at")
    private LocalDateTime receiptIssuedAt;

    @Column(name = "acknowledgment_sent", nullable = false)
    private boolean acknowledgmentSent = false;

    @Column(name = "acknowledgment_date")
    private LocalDateTime acknowledgmentDate;

    @Column(name = "acknowledgment_method", length = 50)
    private String acknowledgmentMethod;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EventDonation that = (EventDonation) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "EventDonation{" +
               "id=" + id +
               ", donorName='" + donorName + '\'' +
               ", donationType='" + donationType + '\'' +
               ", estimatedValue=" + estimatedValue +
               ", status='" + status + '\'' +
               '}';
    }
}
