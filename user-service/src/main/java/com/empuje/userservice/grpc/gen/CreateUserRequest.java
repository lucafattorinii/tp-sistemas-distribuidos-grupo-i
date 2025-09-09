package com.empuje.userservice.grpc.gen;

public final class CreateUserRequest {
    private String username;
    private String firstName;
    private String lastName;
    private String phone;
    private String email;
    private Role role;
    private String createdBy;

    // Getters y Setters
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
    
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    // Patrón Builder
    public static Builder newBuilder() {
        return new Builder();
    }

    public static final class Builder {
        private CreateUserRequest request = new CreateUserRequest();

        public Builder setUsername(String username) {
            request.setUsername(username);
            return this;
        }

        public Builder setFirstName(String firstName) {
            request.setFirstName(firstName);
            return this;
        }

        public Builder setLastName(String lastName) {
            request.setLastName(lastName);
            return this;
        }

        public Builder setPhone(String phone) {
            request.setPhone(phone);
            return this;
        }

        public Builder setEmail(String email) {
            request.setEmail(email);
            return this;
        }

        public Builder setRole(Role role) {
            request.setRole(role);
            return this;
        }

        public Builder setCreatedBy(String createdBy) {
            request.setCreatedBy(createdBy);
            return this;
        }

        public CreateUserRequest build() {
            return request;
        }
    }
}
