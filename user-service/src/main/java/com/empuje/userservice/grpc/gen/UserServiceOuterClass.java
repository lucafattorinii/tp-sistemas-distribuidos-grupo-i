package com.empuje.userservice.grpc.gen;

import com.google.protobuf.Descriptors;

public final class UserServiceOuterClass {
    private static volatile Descriptors.FileDescriptor descriptor;

    private UserServiceOuterClass() {}

    public static Descriptors.FileDescriptor getDescriptor() {
        if (descriptor == null) {
            synchronized (UserServiceOuterClass.class) {
                if (descriptor == null) {
                    descriptor = new Descriptors.FileDescriptor(new Descriptors.FileDescriptor[] {
                        com.google.protobuf.DescriptorProtos.getDescriptor()
                    }, new Descriptors.FileDescriptor.InternalDescriptorAssigner() {
                        @Override
                        public Descriptors.FileDescriptor assignDescriptors(Descriptors.FileDescriptor root) {
                            return null; // Not needed for our simplified implementation
                        }
                    });
                }
            }
        }
        return descriptor;
    }

    static {
        // Initialize the descriptor
        getDescriptor();
    }
}
