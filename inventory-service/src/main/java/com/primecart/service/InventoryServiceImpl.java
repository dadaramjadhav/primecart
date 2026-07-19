package com.primecart.service;

import com.primecart.dto.request.CreateInventoryRequest;
import com.primecart.dto.request.ReserveStockRequest;
import com.primecart.dto.request.UpdateStockRequest;
import com.primecart.dto.response.InventoryResponse;
import com.primecart.entity.Inventory;
import com.primecart.exception.InventoryNotFoundException;
import com.primecart.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;

    @PreAuthorize("hasRole('INVENTORY_READ')")
    @Override
    @Transactional(readOnly = true)
    public InventoryResponse getInventory(Long productId) {

        Inventory inventory = getInventoryEntity(productId);

        return mapToResponse(inventory);
    }

    @PreAuthorize("hasRole('INVENTORY_INCREASE')")
    @Override
    public InventoryResponse increaseStock(Long productId, UpdateStockRequest request) {

        Inventory inventory = getInventoryEntity(productId);

        inventory.setAvailableQuantity(inventory.getAvailableQuantity() + request.getQuantity());

        inventoryRepository.save(inventory);

        return mapToResponse(inventory);
    }

    @PreAuthorize("hasRole('INVENTORY_DECREASE')")
    @Override
    public InventoryResponse decreaseStock(Long productId, UpdateStockRequest request) {

        Inventory inventory = getInventoryEntity(productId);

        if (inventory.getAvailableQuantity() < request.getQuantity()) {

            throw new RuntimeException("Insufficient stock");
        }

        inventory.setAvailableQuantity(inventory.getAvailableQuantity() - request.getQuantity());

        inventoryRepository.save(inventory);

        return mapToResponse(inventory);
    }

    @PreAuthorize("hasRole('INVENTORY_RESERVE')")
    @Override
    public void reserveStock(ReserveStockRequest request) {

        Inventory inventory = getInventoryEntity(request.getProductId());

        if (inventory.getAvailableQuantity() < request.getQuantity()) {

            throw new RuntimeException("Not enough stock available");
        }

        inventory.setAvailableQuantity(inventory.getAvailableQuantity() - request.getQuantity());

        inventory.setReservedQuantity(inventory.getReservedQuantity() + request.getQuantity());

        inventoryRepository.save(inventory);
    }

    @PreAuthorize("hasRole('INVENTORY_RELEASE')")
    @Override
    public void releaseStock(ReserveStockRequest request) {

        Inventory inventory = getInventoryEntity(request.getProductId());

        if (inventory.getReservedQuantity() < request.getQuantity()) {

            throw new RuntimeException("Invalid release quantity");
        }

        inventory.setReservedQuantity(inventory.getReservedQuantity() - request.getQuantity());

        inventory.setAvailableQuantity(inventory.getAvailableQuantity() + request.getQuantity());

        inventoryRepository.save(inventory);
    }

    @PreAuthorize("hasRole('INVENTORY_CREATE')")
    @Override
    @Transactional
    public InventoryResponse createInventory(CreateInventoryRequest request) {

        if (inventoryRepository.existsByProductId(request.getProductId())) {

            throw new RuntimeException("Inventory already exists for product " + request.getProductId());
        }

        Inventory inventory = Inventory
                .builder()
                .productId(request.getProductId())
                .availableQuantity(request.getAvailableQuantity())
                .reservedQuantity(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Inventory saved = inventoryRepository.save(inventory);

        return mapToResponse(saved);
    }

    @PreAuthorize("hasRole('INVENTORY_CONFIRM')")
    @Override
    @Transactional
    public void confirmStock(ReserveStockRequest request) {

        Inventory inventory = inventoryRepository
                .findByProductId(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Inventory not found"));

        if (inventory.getReservedQuantity() < request.getQuantity()) {

            throw new RuntimeException("Not enough reserved stock");
        }

        // only remove reservation
        inventory.setReservedQuantity(inventory.getReservedQuantity() - request.getQuantity());

        inventoryRepository.save(inventory);
    }

    private Inventory getInventoryEntity(Long productId) {

        return inventoryRepository
                .findByProductId(productId)
                .orElseThrow(() -> new InventoryNotFoundException("Inventory not found for product " + productId));
    }

    private InventoryResponse mapToResponse(Inventory inventory) {

        return InventoryResponse
                .builder()

                .productId(inventory.getProductId())

                .availableQuantity(inventory.getAvailableQuantity())

                .reservedQuantity(inventory.getReservedQuantity())

                .build();
    }
}