package com.empuje.userservice.grpc.gen;

public class CheckEmailRequest {
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static final class Builder {
        private CheckEmailRequest request = new CheckEmailRequest();

        public Builder setEmail(String email) {
            request.setEmail(email);
            return this;
        }

        public CheckEmailRequest build() {
            return request;
        }
    }
}
