package com.empuje.userservice.grpc.gen;

public final class CreateUserResponse {
    private long id;
    private String generatedPassword;

    // Getters and Setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    
    public String getGeneratedPassword() { return generatedPassword; }
    public void setGeneratedPassword(String generatedPassword) { 
        this.generatedPassword = generatedPassword; 
    }

    // Builder pattern
    public static Builder newBuilder() {
        return new Builder();
    }

    public static final class Builder {
        private CreateUserResponse response = new CreateUserResponse();

        public Builder setId(long id) {
            response.setId(id);
            return this;
        }

        public Builder setGeneratedPassword(String generatedPassword) {
            response.setGeneratedPassword(generatedPassword);
            return this;
        }

        public CreateUserResponse build() {
            return response;
        }
    }
}
