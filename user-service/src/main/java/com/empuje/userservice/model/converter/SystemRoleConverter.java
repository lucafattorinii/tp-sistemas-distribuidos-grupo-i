package com.empuje.userservice.model.converter;

import com.empuje.userservice.grpc.gen.SystemRole;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class SystemRoleConverter implements AttributeConverter<SystemRole, String> {

    @Override
    public String convertToDatabaseColumn(SystemRole attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.name();
    }

    @Override
    public SystemRole convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return SystemRole.ROLE_UNSPECIFIED;
        }
        try {
            return SystemRole.valueOf(dbData);
        } catch (IllegalArgumentException e) {
            return SystemRole.ROLE_UNSPECIFIED;
        }
    }
}
