package com.empuje.userservice.grpc.gen;

public enum Role {
    UNKNOWN(0),
    PRESIDENTE(1),
    VOCAL(2),
    COORDINADOR(3),
    VOLUNTARIO(4);

    private final int value;

    private Role(int value) {
        this.value = value;
    }

    public int getNumber() {
        return value;
    }

    public static Role valueOf(int value) {
        for (Role role : Role.values()) {
            if (role.getNumber() == value) {
                return role;
            }
        }
        return UNKNOWN;
    }
}
