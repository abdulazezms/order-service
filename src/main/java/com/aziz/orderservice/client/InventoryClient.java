package com.aziz.orderservice.client;
import com.aziz.orderservice.model.InventoryRequest;
import com.aziz.orderservice.model.InventoryResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import java.util.List;

@FeignClient(name = "inventory-service")
public interface InventoryClient {
    @PostMapping("/api/" + "${inventory-service.url.in-stock}")
    InventoryResponse placeOrder(List<InventoryRequest> inventoryRequest);
}
