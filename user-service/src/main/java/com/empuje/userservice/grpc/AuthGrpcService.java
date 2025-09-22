package com.empuje.userservice.grpc;

import com.empuje.userservice.dto.JwtAuthenticationResponse;
import com.empuje.userservice.dto.LoginRequest;
import com.empuje.userservice.service.AuthService;
import com.empuje.userservice.util.GrpcResponseUtils;
import com.empuje.userservice.util.ValidationUtils;
import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import org.empuje.user.AuthServiceGrpc;
import org.empuje.user.LoginRequest as GrpcLoginRequest;
import org.empuje.user.LoginResponse;

@GrpcService
@RequiredArgsConstructor
public class AuthGrpcService extends AuthServiceGrpc.AuthServiceImplBase {

    private final AuthService authService;

    @Override
    public void login(GrpcLoginRequest request, StreamObserver<LoginResponse> responseObserver) {
        try {
            // Validar la solicitud
            ValidationUtils.validateLoginRequest(request);
            
            // Convertir la solicitud gRPC a DTO
            LoginRequest loginRequest = LoginRequest.builder()
                    .usernameOrEmail(request.getUsernameOrEmail())
                    .password(request.getPassword())
                    .build();
            
            // Autenticar al usuario
            JwtAuthenticationResponse authResponse = authService.authenticateUser(loginRequest);
            
            // Construir y enviar la respuesta
            LoginResponse response = LoginResponse.newBuilder()
                    .setToken(authResponse.getToken())
                    .setTokenType(authResponse.getTokenType())
                    .build();
            
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            
        } catch (Exception e) {
            GrpcResponseUtils.handleError(responseObserver, e);
        }
    }
}
