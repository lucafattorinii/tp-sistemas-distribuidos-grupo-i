package com.empuje.userservice.grpc.gen;

public class CheckUsernameResponse {
    private boolean available;

    public boolean getAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static final class Builder {
        private CheckUsernameResponse response = new CheckUsernameResponse();

        public Builder setAvailable(boolean available) {
            response.setAvailable(available);
            return this;
        }

        public CheckUsernameResponse build() {
            return response;
        }
    }
}
