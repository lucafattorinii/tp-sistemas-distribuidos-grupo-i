package com.empuje.userservice.grpc.gen;

public class UpdateUserRequest {
    private long id;
    private String firstName;
    private String lastName;
    private String phone;
    private String email;
    private Role role;
    private boolean active;

    // Getters y Setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    
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

    // Patrón Builder
    public static Builder newBuilder() {
        return new Builder();
    }

    public static final class Builder {
        private UpdateUserRequest request = new UpdateUserRequest();

        public Builder setId(long id) {
            request.setId(id);
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

        public Builder setActive(boolean active) {
            request.setActive(active);
            return this;
        }

        public UpdateUserRequest build() {
            return request;
        }
    }
}
