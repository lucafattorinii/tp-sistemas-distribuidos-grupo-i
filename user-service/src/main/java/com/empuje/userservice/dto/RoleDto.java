package com.empuje.userservice.dto;

import com.empuje.userservice.grpc.gen.SystemRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Map;

/**
 * DTO for {@link com.empuje.userservice.model.Role} entity.
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class RoleDto extends BaseDto {
    private SystemRole name;
    private String description;
    private Map<String, Object> permissions;
    private Boolean active;
}
