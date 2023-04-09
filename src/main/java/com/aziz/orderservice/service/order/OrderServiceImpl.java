package com.aziz.orderservice.service.order;

import com.aziz.orderservice.client.InventoryClient;
import com.aziz.orderservice.dto.OrderLineRequest;
import com.aziz.orderservice.dto.OrderRequest;
import com.aziz.orderservice.event.OrderPlacedEvent;
import com.aziz.orderservice.exception.OutOfStockException;
import com.aziz.orderservice.model.InventoryRequest;
import com.aziz.orderservice.model.InventoryResponse;
import com.aziz.orderservice.model.Order;
import com.aziz.orderservice.model.OrderLine;
import com.aziz.orderservice.repository.OrderLineRepository;
import com.aziz.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService{
    private final OrderRepository orderRepository;
    private final InventoryClient inventoryClient;
    @Value("${notification-topic}")
    private String notificationTopic;


    private final KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;

    @Override
    public void placeOrder(OrderRequest orderRequest) {
        Order order = mapOrderRequestToOrder(orderRequest);
        order.setOrderNumber(UUID.randomUUID().toString());
        List<InventoryRequest>  inventoryRequest =
                order.getOrderLineList()
                        .stream()
                        .map(orderLine -> InventoryRequest.builder()
                                .skuCode(orderLine.getSkuCode())
                                .quantity(orderLine.getQuantity())
                                .build())
                        .toList();
        InventoryResponse result = inventoryClient.placeOrder(inventoryRequest);

        if(result == null || !result.getSuccess()){
            throw new OutOfStockException(("Sorry, your order cannot be fulfilled: " +
                    Objects.requireNonNull(result).getMessage()));
        }
        orderRepository.save(order);
        //sending the message to the topic `notificationTopic`.
        //we'll send the order number as the message to the queue, encapsulated
        //in a POJO, so that it can be serialized into JSON.
        log.info("Order was saved");

        kafkaTemplate.send(
                notificationTopic,//sending to the notificationTopic...
                OrderPlacedEvent.builder().orderNumber(order.getOrderNumber()).build()
        );
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
