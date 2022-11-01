package com.microservices.OrderService.Service;


import com.microservices.OrderService.DataTransferObject.InventoryResponse;
import com.microservices.OrderService.DataTransferObject.OrderLineItemsDto;
import com.microservices.OrderService.DataTransferObject.OrderRequest;
import com.microservices.OrderService.Model.Order;
import com.microservices.OrderService.Model.OrderLineItems;
import com.microservices.OrderService.Repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final WebClient.Builder webClientBuilder;

    public void placeOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        List<OrderLineItems> orderLineItems = orderRequest
                .getOrderLineItemsDtoList()
                .stream()
                .map(
                        this::mapToDto
//                        orderLineItemsDto -> mapToDto(orderLineItemsDto)
                )
                .collect(Collectors.toList());
        order.setOrderLineItemList(orderLineItems);

        List<String> skuCodes = order
                .getOrderLineItemList()
                .stream()
                .map(
                        OrderLineItems::getSkuCode
//                        OrderLineItems -> OrderLineItems.getSkuCode()
                )
                .collect(Collectors.toList());

        InventoryResponse[] results = webClientBuilder
                .build()
                .get()
                .uri(
                        "http://inventory-service/api/inventory",
                        uriBuilder -> (
                                uriBuilder
                                        .queryParam("skuCode", skuCodes)
                                        .build()
                        )
                )
                .retrieve()
                .bodyToMono(InventoryResponse[].class)
                .block();

        assert results != null;
        boolean productsInStock = Arrays
                .stream(results)
                .allMatch(
                        InventoryResponse::isInStock
//                        inventoryResponse -> inventoryResponse.isInStock()
                );

        if (productsInStock) {
            orderRepository.save(order);
        } else {
            throw new IllegalArgumentException("Product is not  in stock. Please, try again later");
        }

    }

    private OrderLineItems mapToDto(OrderLineItemsDto orderLineItemsDto) {
        OrderLineItems orderLineItems = new OrderLineItems();
        orderLineItems.setPrice(orderLineItemsDto.getPrice());
        orderLineItems.setQuantity(orderLineItemsDto.getQuantity());
        orderLineItems.setSkuCode(orderLineItemsDto.getSkuCode());
        return orderLineItems;
    }

}
