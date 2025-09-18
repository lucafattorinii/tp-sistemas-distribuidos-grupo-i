package com.empuje.eventservice.grpc;

import com.empuje.eventservice.grpc.gen.*;
import com.empuje.eventservice.model.Event;
import com.empuje.eventservice.model.EventParticipant;
import com.empuje.eventservice.model.EventParticipantId;
import com.empuje.eventservice.repository.EventParticipantRepository;
import com.empuje.eventservice.repository.EventRepository;
import com.google.protobuf.Timestamp;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

import java.time.Instant;
import java.util.Comparator;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class EventGrpcServiceDbImpl extends EventServiceGrpc.EventServiceImplBase {

    private final EventRepository eventRepository;
    private final EventParticipantRepository participantRepository;

    @Override
    public void createEvent(CreateEventRequest request, StreamObserver<EventResponse> responseObserver) {
        try {
            if (request.getName().isEmpty() || !request.hasEventDatetime()) {
                throw new IllegalArgumentException("Name and event_datetime are required");
            }
            Instant when = toInstant(request.getEventDatetime());
            if (when.isBefore(Instant.now())) {
                responseObserver.onError(Status.FAILED_PRECONDITION.withDescription("Event datetime must be in the future").asRuntimeException());
                return;
            }
            Event ev = Event.builder()
                    .name(request.getName())
                    .description(request.getDescription())
                    .eventDatetime(when)
                    .active(true)
                    .build();
            Event saved = eventRepository.save(ev);
            // participants optional
            for (long uid : request.getParticipantIdsList()) {
                EventParticipant ep = EventParticipant.builder()
                        .id(new EventParticipantId(saved.getId(), uid))
                        .event(saved)
                        .build();
                participantRepository.save(ep);
            }
            responseObserver.onNext(toResponse(saved));
            responseObserver.onCompleted();
        } catch (IllegalArgumentException ex) {
            responseObserver.onError(Status.INVALID_ARGUMENT.withDescription(ex.getMessage()).asRuntimeException());
        } catch (Exception e) {
            log.error("createEvent error", e);
            responseObserver.onError(Status.INTERNAL.withDescription("Error creating event").asRuntimeException());
        }
    }

    @Override
    public void updateEvent(UpdateEventRequest request, StreamObserver<EventResponse> responseObserver) {
        try {
            Event ev = eventRepository.findById(request.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Event not found"));
            if (!request.getName().isEmpty()) ev.setName(request.getName());
            if (!request.getDescription().isEmpty()) ev.setDescription(request.getDescription());
            if (request.hasEventDatetime()) {
                Instant when = toInstant(request.getEventDatetime());
                ev.setEventDatetime(when);
            }
            if (request.getParticipantIdsCount() > 0) {
                // replace all
                participantRepository.findByIdEventId(ev.getId()).forEach(participantRepository::delete);
                for (long uid : request.getParticipantIdsList()) {
                    EventParticipant ep = EventParticipant.builder()
                            .id(new EventParticipantId(ev.getId(), uid))
                            .event(ev)
                            .build();
                    participantRepository.save(ep);
                }
            }
            Event saved = eventRepository.save(ev);
            responseObserver.onNext(toResponse(saved));
            responseObserver.onCompleted();
        } catch (IllegalArgumentException ex) {
            responseObserver.onError(Status.NOT_FOUND.withDescription(ex.getMessage()).asRuntimeException());
        } catch (Exception e) {
            log.error("updateEvent error", e);
            responseObserver.onError(Status.INTERNAL.withDescription("Error updating event").asRuntimeException());
        }
    }

    @Override
    public void deleteEvent(DeleteEventRequest request, StreamObserver<DeleteEventResponse> responseObserver) {
        try {
            Event ev = eventRepository.findById(request.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Event not found"));
            if (ev.getEventDatetime() != null && ev.getEventDatetime().isBefore(Instant.now())) {
                responseObserver.onNext(DeleteEventResponse.newBuilder().setSuccess(false).setMessage("Cannot delete past events").build());
                responseObserver.onCompleted();
                return;
            }
            eventRepository.delete(ev);
            responseObserver.onNext(DeleteEventResponse.newBuilder().setSuccess(true).setMessage("Deleted").build());
            responseObserver.onCompleted();
        } catch (IllegalArgumentException ex) {
            responseObserver.onNext(DeleteEventResponse.newBuilder().setSuccess(false).setMessage("Not found").build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("deleteEvent error", e);
            responseObserver.onError(Status.INTERNAL.withDescription("Error deleting event").asRuntimeException());
        }
    }

    @Override
    public void listEvents(ListEventsRequest request, StreamObserver<EventResponse> responseObserver) {
        try {
            eventRepository.findAll().stream()
                    .sorted(Comparator.comparing(Event::getEventDatetime))
                    .map(this::toResponse)
                    .forEach(responseObserver::onNext);
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("listEvents error", e);
            responseObserver.onError(Status.INTERNAL.withDescription("Error listing events").asRuntimeException());
        }
    }

    @Override
    public void assignMember(AssignMemberRequest request, StreamObserver<EventResponse> responseObserver) {
        try {
            Event ev = eventRepository.findById(request.getEventId())
                    .orElseThrow(() -> new IllegalArgumentException("Event not found"));
            EventParticipantId id = new EventParticipantId(request.getEventId(), request.getUserId());
            participantRepository.save(EventParticipant.builder().id(id).event(ev).build());
            responseObserver.onNext(toResponse(ev));
            responseObserver.onCompleted();
        } catch (IllegalArgumentException ex) {
            responseObserver.onError(Status.NOT_FOUND.withDescription(ex.getMessage()).asRuntimeException());
        } catch (Exception e) {
            log.error("assignMember error", e);
            responseObserver.onError(Status.INTERNAL.withDescription("Error assigning member").asRuntimeException());
        }
    }

    @Override
    public void removeMember(RemoveMemberRequest request, StreamObserver<EventResponse> responseObserver) {
        try {
            Event ev = eventRepository.findById(request.getEventId())
                    .orElseThrow(() -> new IllegalArgumentException("Event not found"));
            EventParticipantId id = new EventParticipantId(request.getEventId(), request.getUserId());
            participantRepository.findById(id).ifPresent(participantRepository::delete);
            responseObserver.onNext(toResponse(ev));
            responseObserver.onCompleted();
        } catch (IllegalArgumentException ex) {
            responseObserver.onError(Status.NOT_FOUND.withDescription(ex.getMessage()).asRuntimeException());
        } catch (Exception e) {
            log.error("removeMember error", e);
            responseObserver.onError(Status.INTERNAL.withDescription("Error removing member").asRuntimeException());
        }
    }

    private Instant toInstant(Timestamp ts) {
        return Instant.ofEpochSecond(ts.getSeconds(), ts.getNanos());
    }

    private EventResponse toResponse(Event ev) {
        Instant when = ev.getEventDatetime() == null ? Instant.now() : ev.getEventDatetime();
        return EventResponse.newBuilder()
                .setId(ev.getId() == null ? 0 : ev.getId())
                .setName(ev.getName() == null ? "" : ev.getName())
                .setDescription(ev.getDescription() == null ? "" : ev.getDescription())
                .setEventDatetime(Timestamp.newBuilder().setSeconds(when.getEpochSecond()).setNanos(when.getNano()).build())
                .setCreatedAt(Timestamp.newBuilder().setSeconds((ev.getCreatedAt() == null ? Instant.now() : ev.getCreatedAt()).getEpochSecond()).setNanos((ev.getCreatedAt() == null ? Instant.now() : ev.getCreatedAt()).getNano()).build())
                .build();
    }
}
