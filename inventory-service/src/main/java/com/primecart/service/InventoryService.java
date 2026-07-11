package com.primecart.service;

import com.primecart.dto.request.ReserveStockRequest;
import com.primecart.dto.request.UpdateStockRequest;
import com.primecart.dto.response.InventoryResponse;

public interface InventoryService {

    InventoryResponse getInventory(Long productId);

    InventoryResponse increaseStock(Long productId,
                                    UpdateStockRequest request);

    InventoryResponse decreaseStock(Long productId,
                                    UpdateStockRequest request);

    void reserveStock(ReserveStockRequest request);

    void releaseStock(ReserveStockRequest request);
}