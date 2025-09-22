package com.empuje.eventservice.exception;

import com.google.rpc.Code;
import com.google.rpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.protobuf.StatusProto;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import net.devh.boot.grpc.server.advice.GrpcAdvice;
import net.devh.boot.grpc.server.advice.GrpcExceptionHandler;

import java.util.stream.Collectors;

@GrpcAdvice
public class GlobalExceptionHandler {

    @GrpcExceptionHandler(ConstraintViolationException.class)
    public StatusRuntimeException handleConstraintViolation(ConstraintViolationException e) {
        String errorMessage = e.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));
        
        Status status = Status.newBuilder()
                .setCode(Code.INVALID_ARGUMENT_VALUE)
                .setMessage(errorMessage)
                .build();
        
        return StatusProto.toStatusRuntimeException(status);
    }

    @GrpcExceptionHandler(IllegalArgumentException.class)
    public StatusRuntimeException handleIllegalArgumentException(IllegalArgumentException e) {
        Status status = Status.newBuilder()
                .setCode(Code.INVALID_ARGUMENT_VALUE)
                .setMessage(e.getMessage())
                .build();
        
        return StatusProto.toStatusRuntimeException(status);
    }

    @GrpcExceptionHandler(IllegalStateException.class)
    public StatusRuntimeException handleIllegalStateException(IllegalStateException e) {
        Status status = Status.newBuilder()
                .setCode(Code.FAILED_PRECONDITION_VALUE)
                .setMessage(e.getMessage())
                .build();
        
        return StatusProto.toStatusRuntimeException(status);
    }
}
