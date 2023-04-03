package com.aziz.orderservice.service.order;

import com.aziz.orderservice.dto.OrderRequest;

public interface OrderService {
    void placeOrder(OrderRequest orderRequest);
}
