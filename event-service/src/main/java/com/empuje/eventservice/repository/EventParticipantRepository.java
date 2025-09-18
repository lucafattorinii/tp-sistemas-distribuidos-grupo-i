package com.empuje.eventservice.repository;

import com.empuje.eventservice.model.EventParticipant;
import com.empuje.eventservice.model.EventParticipantId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventParticipantRepository extends JpaRepository<EventParticipant, EventParticipantId> {
    List<EventParticipant> findByIdEventId(Long eventId);
}
