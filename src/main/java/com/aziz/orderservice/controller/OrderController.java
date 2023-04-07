package com.aziz.orderservice.controller;

import com.aziz.orderservice.dto.OrderRequest;
import com.aziz.orderservice.service.order.OrderService;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Slf4j
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    @Retry(name = "inventory", fallbackMethod = "fallback")
    @TimeLimiter(name = "inventory", fallbackMethod = "timeFallback")

    public CompletableFuture<ResponseEntity<String>> placeOrder(@RequestBody OrderRequest orderRequest){
        log.info("placeOrder() called");
        return CompletableFuture.supplyAsync(() -> {
            orderService.placeOrder(orderRequest);
            String response = "Order successfully created!";
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        });

    }

    public CompletableFuture<ResponseEntity<String>> fallback(OrderRequest orderRequest, Exception e){
        return CompletableFuture.completedFuture(
                new ResponseEntity<>("Sorry, your order:  " + orderRequest.getOrderLineList() + " cannot be fulfilled atm, " +
                        "why don't you have a cup of coffee then try out again?", HttpStatus.SERVICE_UNAVAILABLE)
        );
    }


    public CompletableFuture<ResponseEntity<String>>  timeFallback(OrderRequest orderRequest, Exception e){
        return CompletableFuture.completedFuture(
                new ResponseEntity<>("Sorry, the service took long to respond. Why don't you come back a few" +
                        " minutes later??", HttpStatus.REQUEST_TIMEOUT)
        );
    }

}
