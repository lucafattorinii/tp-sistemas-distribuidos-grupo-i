package com.empuje.eventservice.grpc;

import com.empuje.eventservice.grpc.gen.*;
import com.google.protobuf.Timestamp;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

import java.time.Instant;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@GrpcService
public class EventGrpcServiceImpl extends EventServiceGrpc.EventServiceImplBase {

    private final AtomicLong idGen = new AtomicLong(1);
    private final Map<Long, Event> events = new ConcurrentHashMap<>();

    private static class Event {
        long id;
        String name;
        String description;
        Instant when;
        Set<Long> participants = new HashSet<>();
        Instant createdAt;
    }

    @Override
    public void createEvent(CreateEventRequest request, StreamObserver<EventResponse> responseObserver) {
        if (request.getName().isEmpty() || !request.hasEventDatetime()) {
            responseObserver.onError(Status.INVALID_ARGUMENT.withDescription("Name and event_datetime are required").asRuntimeException());
            return;
        }
        Instant when = Instant.ofEpochSecond(request.getEventDatetime().getSeconds(), request.getEventDatetime().getNanos());
        if (when.isBefore(Instant.now())) {
            responseObserver.onError(Status.FAILED_PRECONDITION.withDescription("Event datetime must be in the future").asRuntimeException());
            return;
        }
        long id = idGen.getAndIncrement();
        Event ev = new Event();
        ev.id = id;
        ev.name = request.getName();
        ev.description = request.getDescription();
        ev.when = when;
        ev.createdAt = Instant.now();
        ev.participants.addAll(request.getParticipantIdsList());
        events.put(id, ev);
        responseObserver.onNext(toResponse(ev));
        responseObserver.onCompleted();
    }

    @Override
    public void updateEvent(UpdateEventRequest request, StreamObserver<EventResponse> responseObserver) {
        Event ev = events.get(request.getId());
        if (ev == null) {
            responseObserver.onError(Status.NOT_FOUND.withDescription("Event not found").asRuntimeException());
            return;
        }
        if (!request.getName().isEmpty()) ev.name = request.getName();
        if (!request.getDescription().isEmpty()) ev.description = request.getDescription();
        if (request.hasEventDatetime()) {
            Instant when = Instant.ofEpochSecond(request.getEventDatetime().getSeconds(), request.getEventDatetime().getNanos());
            ev.when = when;
        }
        if (request.getParticipantIdsCount() > 0) {
            ev.participants = new HashSet<>(request.getParticipantIdsList());
        }
        responseObserver.onNext(toResponse(ev));
        responseObserver.onCompleted();
    }

    @Override
    public void deleteEvent(DeleteEventRequest request, StreamObserver<DeleteEventResponse> responseObserver) {
        Event ev = events.get(request.getId());
        if (ev == null) {
            responseObserver.onNext(DeleteEventResponse.newBuilder().setSuccess(false).setMessage("Not found").build());
            responseObserver.onCompleted();
            return;
        }
        // simple rule: allow deletion only if in the future
        if (ev.when.isBefore(Instant.now())) {
            responseObserver.onNext(DeleteEventResponse.newBuilder().setSuccess(false).setMessage("Cannot delete past events").build());
            responseObserver.onCompleted();
            return;
        }
        events.remove(request.getId());
        responseObserver.onNext(DeleteEventResponse.newBuilder().setSuccess(true).setMessage("Deleted").build());
        responseObserver.onCompleted();
    }

    @Override
    public void listEvents(ListEventsRequest request, StreamObserver<EventResponse> responseObserver) {
        events.values().stream().sorted(Comparator.comparing(e -> e.when)).map(this::toResponse).forEach(responseObserver::onNext);
        responseObserver.onCompleted();
    }

    @Override
    public void assignMember(AssignMemberRequest request, StreamObserver<EventResponse> responseObserver) {
        Event ev = events.get(request.getEventId());
        if (ev == null) {
            responseObserver.onError(Status.NOT_FOUND.withDescription("Event not found").asRuntimeException());
            return;
        }
        ev.participants.add(request.getUserId());
        responseObserver.onNext(toResponse(ev));
        responseObserver.onCompleted();
    }

    @Override
    public void removeMember(RemoveMemberRequest request, StreamObserver<EventResponse> responseObserver) {
        Event ev = events.get(request.getEventId());
        if (ev == null) {
            responseObserver.onError(Status.NOT_FOUND.withDescription("Event not found").asRuntimeException());
            return;
        }
        ev.participants.remove(request.getUserId());
        responseObserver.onNext(toResponse(ev));
        responseObserver.onCompleted();
    }

    private EventResponse toResponse(Event ev) {
        return EventResponse.newBuilder()
                .setId(ev.id)
                .setName(ev.name)
                .setDescription(ev.description == null ? "" : ev.description)
                .setEventDatetime(Timestamp.newBuilder().setSeconds(ev.when.getEpochSecond()).setNanos(ev.when.getNano()).build())
                .addAllParticipantIds(ev.participants)
                .setCreatedAt(Timestamp.newBuilder().setSeconds(ev.createdAt.getEpochSecond()).setNanos(ev.createdAt.getNano()).build())
                .build();
    }
}
