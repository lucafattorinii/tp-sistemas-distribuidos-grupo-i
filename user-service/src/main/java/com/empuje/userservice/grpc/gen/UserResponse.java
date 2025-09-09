package com.empuje.userservice.grpc.gen;

import java.time.Instant;

public class UserResponse {
    private long id;
    private String username;
    private String firstName;
    private String lastName;
    private String phone;
    private String email;
    private Role role;
    private boolean active;
    private Instant createdAt;

    // Getters y Setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
    
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    // Patrón Builder
    public static Builder newBuilder() {
        return new Builder();
    }

    public static final class Builder {
        private UserResponse response = new UserResponse();

        public Builder setId(long id) {
            response.setId(id);
            return this;
        }

        public Builder setUsername(String username) {
            response.setUsername(username);
            return this;
        }

        public Builder setFirstName(String firstName) {
            response.setFirstName(firstName);
            return this;
        }

        public Builder setLastName(String lastName) {
            response.setLastName(lastName);
            return this;
        }

        public Builder setPhone(String phone) {
            response.setPhone(phone);
            return this;
        }

        public Builder setEmail(String email) {
            response.setEmail(email);
            return this;
        }

        public Builder setRole(Role role) {
            response.setRole(role);
            return this;
        }

        public Builder setActive(boolean active) {
            response.setActive(active);
            return this;
        }

        public Builder setCreatedAt(Instant createdAt) {
            response.setCreatedAt(createdAt);
            return this;
        }

        public UserResponse build() {
            return response;
        }
    }
}
