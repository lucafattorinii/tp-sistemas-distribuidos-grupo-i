package com.empuje.inventoryservice.grpc;

import com.empuje.inventoryservice.grpc.gen.*;
import com.empuje.inventoryservice.model.Item;
import com.empuje.inventoryservice.repository.ItemRepository;
import com.google.protobuf.Timestamp;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

import java.time.Instant;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class InventoryGrpcServiceImpl extends InventoryServiceGrpc.InventoryServiceImplBase {

    private final ItemRepository itemRepository;

    @Override
    public void addItem(AddItemRequest request, StreamObserver<ItemResponse> responseObserver) {
        try {
            if (request.getCategory() == Category.CATEGORY_UNKNOWN || request.getDescription().isEmpty() || request.getQuantity() < 0) {
                throw new IllegalArgumentException("Invalid item data");
            }
            Item entity = Item.builder()
                    .category(request.getCategory().name())
                    .description(request.getDescription())
                    .quantity(request.getQuantity())
                    .deleted(false)
                    .build();
            Item saved = itemRepository.save(entity);
            responseObserver.onNext(toResponse(saved));
            responseObserver.onCompleted();
        } catch (IllegalArgumentException ex) {
            responseObserver.onError(Status.INVALID_ARGUMENT.withDescription(ex.getMessage()).asRuntimeException());
        } catch (Exception e) {
            log.error("addItem error", e);
            responseObserver.onError(Status.INTERNAL.withDescription("Error adding item").asRuntimeException());
        }
    }

    @Override
    public void updateItem(UpdateItemRequest request, StreamObserver<ItemResponse> responseObserver) {
        try {
            Item it = itemRepository.findById(request.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Item not found"));
            if (!request.getDescription().isEmpty()) it.setDescription(request.getDescription());
            if (request.getQuantity() >= 0) it.setQuantity(request.getQuantity());
            Item saved = itemRepository.save(it);
            responseObserver.onNext(toResponse(saved));
            responseObserver.onCompleted();
        } catch (IllegalArgumentException ex) {
            responseObserver.onError(Status.NOT_FOUND.withDescription(ex.getMessage()).asRuntimeException());
        } catch (Exception e) {
            log.error("updateItem error", e);
            responseObserver.onError(Status.INTERNAL.withDescription("Error updating item").asRuntimeException());
        }
    }

    @Override
    public void deleteItem(DeleteItemRequest request, StreamObserver<DeleteItemResponse> responseObserver) {
        try {
            Item it = itemRepository.findById(request.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Item not found"));
            if (Boolean.TRUE.equals(it.getDeleted())) {
                responseObserver.onNext(DeleteItemResponse.newBuilder().setSuccess(false).setMessage("Already deleted").build());
                responseObserver.onCompleted();
                return;
            }
            it.setDeleted(true);
            itemRepository.save(it);
            responseObserver.onNext(DeleteItemResponse.newBuilder().setSuccess(true).setMessage("Deleted").build());
            responseObserver.onCompleted();
        } catch (IllegalArgumentException ex) {
            responseObserver.onNext(DeleteItemResponse.newBuilder().setSuccess(false).setMessage("Not found").build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("deleteItem error", e);
            responseObserver.onError(Status.INTERNAL.withDescription("Error deleting item").asRuntimeException());
        }
    }

    @Override
    public void listItems(ListItemsRequest request, StreamObserver<ItemResponse> responseObserver) {
        try {
            itemRepository.findByDeletedFalseOrderByCreatedAtDesc()
                    .stream()
                    .map(this::toResponse)
                    .forEach(responseObserver::onNext);
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("listItems error", e);
            responseObserver.onError(Status.INTERNAL.withDescription("Error listing items").asRuntimeException());
        }
    }

    @Override
    public void adjustQuantity(AdjustQtyRequest request, StreamObserver<ItemResponse> responseObserver) {
        try {
            Item it = itemRepository.findById(request.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Item not found"));
            int newQty = it.getQuantity() + request.getDelta();
            if (newQty < 0) {
                responseObserver.onError(Status.FAILED_PRECONDITION.withDescription("Quantity cannot be negative").asRuntimeException());
                return;
            }
            it.setQuantity(newQty);
            Item saved = itemRepository.save(it);
            responseObserver.onNext(toResponse(saved));
            responseObserver.onCompleted();
        } catch (IllegalArgumentException ex) {
            responseObserver.onError(Status.NOT_FOUND.withDescription(ex.getMessage()).asRuntimeException());
        } catch (Exception e) {
            log.error("adjustQuantity error", e);
            responseObserver.onError(Status.INTERNAL.withDescription("Error adjusting quantity").asRuntimeException());
        }
    }

    private ItemResponse toResponse(Item it) {
        Category cat;
        try {
            cat = Category.valueOf(it.getCategory());
        } catch (Exception e) {
            cat = Category.CATEGORY_UNKNOWN;
        }
        Instant created = it.getCreatedAt() != null ? it.getCreatedAt() : Instant.now();
        return ItemResponse.newBuilder()
                .setId(it.getId() == null ? 0 : it.getId())
                .setCategory(cat)
                .setDescription(it.getDescription() == null ? "" : it.getDescription())
                .setQuantity(it.getQuantity() == null ? 0 : it.getQuantity())
                .setDeleted(Boolean.TRUE.equals(it.getDeleted()))
                .setCreatedAt(Timestamp.newBuilder().setSeconds(created.getEpochSecond()).setNanos(created.getNano()).build())
                .build();
    }
}
