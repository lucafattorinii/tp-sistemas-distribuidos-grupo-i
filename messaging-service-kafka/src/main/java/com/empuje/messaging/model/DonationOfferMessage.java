package com.empuje.messaging.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DonationOfferMessage {
    private String offerId;
    private String donorOrganizationId;
    private String donationCategory;
    private String donationDescription;
    private String quantity;
    private String timestamp;
}
