package com.aziz.orderservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "t_orders")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order {
    //order as a whole.
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String orderNumber;

    //items in the order
    @OneToMany(cascade = CascadeType.ALL)
    private List<OrderLine> orderLineList;


}
