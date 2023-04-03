package com.aziz.orderservice.service.order;

import com.aziz.orderservice.dto.OrderLineRequest;
import com.aziz.orderservice.dto.OrderRequest;
import com.aziz.orderservice.model.Order;
import com.aziz.orderservice.model.OrderLine;
import com.aziz.orderservice.repository.OrderLineRepository;
import com.aziz.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService{
    private final OrderRepository orderRepository;
//    private final OrderLineRepository orderLineRepository;
    @Override
    public void placeOrder(OrderRequest orderRequest) {
        Order order = mapOrderRequestToOrder(orderRequest);
        order.setOrderNumber(UUID.randomUUID().toString());
//        orderLineRepository.saveAll(order.getOrderLineList());
        orderRepository.save(order);
    }

    private Order mapOrderRequestToOrder(OrderRequest orderRequest){

        return Order
                .builder()
                .orderLineList(mapOrderLineRequestsToOrderLines(orderRequest.getOrderLineList()))
                .build();
    }

    private OrderLine mapOrderLineRequestToOrderLine(OrderLineRequest orderLineRequest){
        return OrderLine
                .builder()
                .quantity(orderLineRequest.getQuantity())
                .price(orderLineRequest.getPrice())
                .skuCode(orderLineRequest.getSkuCode())
                .id(orderLineRequest.getId())
                .build();
    }
    private List<OrderLine> mapOrderLineRequestsToOrderLines(List<OrderLineRequest> orderLineRequests){
        return orderLineRequests
                .stream()
                .map(this::mapOrderLineRequestToOrderLine)
                .toList();
    }
}
