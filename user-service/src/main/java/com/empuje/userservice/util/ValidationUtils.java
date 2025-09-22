package com.empuje.userservice.util;

import com.empuje.userservice.exception.BadRequestException;
import org.empuje.user.LoginRequest;
import org.springframework.util.StringUtils;

public class ValidationUtils {

    public static void validateLoginRequest(LoginRequest request) {
        if (!StringUtils.hasText(request.getUsernameOrEmail())) {
            throw new BadRequestException("El nombre de usuario o correo electrónico es obligatorio");
        }
        
        if (!StringUtils.hasText(request.getPassword())) {
            throw new BadRequestException("La contraseña es obligatoria");
        }
    }
}
