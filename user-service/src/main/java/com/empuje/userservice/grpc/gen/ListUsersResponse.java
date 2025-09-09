package com.empuje.userservice.grpc.gen;

import java.util.ArrayList;
import java.util.List;

public class ListUsersResponse {
    private List<UserResponse> users = new ArrayList<>();

    public List<UserResponse> getUsersList() {
        return users;
    }

    public void setUsersList(List<UserResponse> users) {
        this.users = users;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static final class Builder {
        private ListUsersResponse response = new ListUsersResponse();

        public Builder addUsers(UserResponse user) {
            response.getUsersList().add(user);
            return this;
        }

        public Builder addAllUsers(Iterable<UserResponse> users) {
            for (UserResponse user : users) {
                response.getUsersList().add(user);
            }
            return this;
        }

        public ListUsersResponse build() {
            return response;
        }
    }
}
