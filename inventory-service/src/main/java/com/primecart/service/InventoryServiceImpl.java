package com.primecart.service;

import com.primecart.dto.request.ReserveStockRequest;
import com.primecart.dto.request.UpdateStockRequest;
import com.primecart.dto.response.InventoryResponse;
import com.primecart.entity.Inventory;
import com.primecart.exception.InventoryNotFoundException;
import com.primecart.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;

    @Override
    @Transactional(readOnly = true)
    public InventoryResponse getInventory(Long productId) {

        Inventory inventory =
                getInventoryEntity(productId);

        return mapToResponse(inventory);
    }

    @Override
    public InventoryResponse increaseStock(
            Long productId,
            UpdateStockRequest request) {

        Inventory inventory =
                getInventoryEntity(productId);

        inventory.setAvailableQuantity(
                inventory.getAvailableQuantity()
                        + request.getQuantity()
        );

        inventoryRepository.save(inventory);

        return mapToResponse(inventory);
    }

    @Override
    public InventoryResponse decreaseStock(
            Long productId,
            UpdateStockRequest request) {

        Inventory inventory =
                getInventoryEntity(productId);

        if (inventory.getAvailableQuantity()
                < request.getQuantity()) {

            throw new RuntimeException(
                    "Insufficient stock"
            );
        }

        inventory.setAvailableQuantity(
                inventory.getAvailableQuantity()
                        - request.getQuantity()
        );

        inventoryRepository.save(inventory);

        return mapToResponse(inventory);

    }

    @Override
    public void reserveStock(
            ReserveStockRequest request) {

        Inventory inventory =
                getInventoryEntity(request.getProductId());

        if (inventory.getAvailableQuantity()
                < request.getQuantity()) {

            throw new RuntimeException(
                    "Not enough stock available"
            );

        }

        inventory.setAvailableQuantity(
                inventory.getAvailableQuantity()
                        - request.getQuantity()
        );

        inventory.setReservedQuantity(
                inventory.getReservedQuantity()
                        + request.getQuantity()
        );

        inventoryRepository.save(inventory);

    }

    @Override
    public void releaseStock(
            ReserveStockRequest request) {

        Inventory inventory =
                getInventoryEntity(request.getProductId());

        if (inventory.getReservedQuantity()
                < request.getQuantity()) {

            throw new RuntimeException(
                    "Invalid release quantity"
            );

        }

        inventory.setReservedQuantity(
                inventory.getReservedQuantity()
                        - request.getQuantity()
        );

        inventory.setAvailableQuantity(
                inventory.getAvailableQuantity()
                        + request.getQuantity()
        );

        inventoryRepository.save(inventory);

    }

    private Inventory getInventoryEntity(Long productId) {

        return inventoryRepository
                .findByProductId(productId)
                .orElseThrow(() ->
                        new InventoryNotFoundException(
                                "Inventory not found for product "
                                        + productId
                        ));

    }

    private InventoryResponse mapToResponse(
            Inventory inventory) {

        return InventoryResponse.builder()

                                .productId(
                                        inventory.getProductId()
                                )

                                .availableQuantity(
                                        inventory.getAvailableQuantity()
                                )

                                .reservedQuantity(
                                        inventory.getReservedQuantity()
                                )

                                .build();

    }

}