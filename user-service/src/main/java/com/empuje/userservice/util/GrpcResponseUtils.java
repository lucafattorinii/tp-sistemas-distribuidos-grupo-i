package com.empuje.userservice.util;

import com.empuje.userservice.exception.BadRequestException;
import com.empuje.userservice.exception.ResourceNotFoundException;
import com.empuje.userservice.exception.UnauthorizedException;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GrpcResponseUtils {

    public static <T> void handleError(StreamObserver<T> responseObserver, Exception e) {
        Status status;
        
        if (e instanceof IllegalArgumentException || e instanceof BadRequestException) {
            status = Status.INVALID_ARGUMENT.withDescription(e.getMessage());
        } else if (e instanceof ResourceNotFoundException) {
            status = Status.NOT_FOUND.withDescription(e.getMessage());
        } else if (e instanceof UnauthorizedException) {
            status = Status.UNAUTHENTICATED.withDescription(e.getMessage());
        } else {
            log.error("Error interno del servidor", e);
            status = Status.INTERNAL.withDescription("Error interno del servidor");
        }
        
        responseObserver.onError(status.asRuntimeException());
    }
}
