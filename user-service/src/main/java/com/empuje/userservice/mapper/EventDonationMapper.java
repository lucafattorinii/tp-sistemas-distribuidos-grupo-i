package com.empuje.userservice.mapper;

import com.empuje.userservice.dto.EventDonationDto;
import com.empuje.userservice.model.EventDonation;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * Mapper for the entity {@link EventDonation} and its DTO {@link EventDonationDto}.
 */
@Mapper(componentModel = "spring", uses = {EventMapper.class, UserMapper.class})
public interface EventDonationMapper extends BaseMapper<EventDonation, EventDonationDto> {
    
    EventDonationMapper INSTANCE = Mappers.getMapper(EventDonationMapper.class);
    
    @Override
    @Mapping(target = "isAnonymous", source = "anonymous")
    @Mapping(target = "receiptRequired", source = "receiptRequired")
    @Mapping(target = "acknowledgmentSent", source = "acknowledgmentSent")
    EventDonationDto toDto(EventDonation eventDonation);
    
    @Override
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "anonymous", source = "isAnonymous")
    @Mapping(target = "receiptRequired", source = "receiptRequired")
    @Mapping(target = "acknowledgmentSent", source = "acknowledgmentSent")
    @Mapping(target = "receiptNumber", ignore = true) // Should be set by the service
    @Mapping(target = "receiptIssuedAt", ignore = true) // Should be set by the service
    @Mapping(target = "acknowledgmentDate", ignore = true) // Should be set by the service
    EventDonation toEntity(EventDonationDto eventDonationDto);
    
    @Override
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "anonymous", source = "isAnonymous")
    @Mapping(target = "receiptRequired", source = "receiptRequired")
    @Mapping(target = "acknowledgmentSent", source = "acknowledgmentSent")
    @Mapping(target = "receiptNumber", ignore = true) // Should be set by the service
    @Mapping(target = "receiptIssuedAt", ignore = true) // Should be set by the service
    @Mapping(target = "acknowledgmentDate", ignore = true) // Should be set by the service
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(EventDonationDto eventDonationDto, @MappingTarget EventDonation eventDonation);
}
