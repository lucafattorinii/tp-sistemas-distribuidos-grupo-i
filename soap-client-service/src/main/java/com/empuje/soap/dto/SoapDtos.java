package com.empuje.soap.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SoapRequest {
    private List<String> organizationIds;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PresidentResponse {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String organizationId;
    private String organizationName;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PresidentsResponse {
    private List<PresidentResponse> presidents;
    private String soapRequest;
    private String soapResponse;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationResponse {
    private String id;
    private String name;
    private String description;
    private String address;
    private String phone;
    private String email;
    private String website;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationsResponse {
    private List<OrganizationResponse> organizations;
    private String soapRequest;
    private String soapResponse;
}
