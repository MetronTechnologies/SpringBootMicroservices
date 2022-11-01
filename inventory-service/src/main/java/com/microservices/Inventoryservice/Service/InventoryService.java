package com.microservices.Inventoryservice.Service;


import com.microservices.Inventoryservice.DataTransferObject.InventoryResponse;
import com.microservices.Inventoryservice.Model.Inventory;
import com.microservices.Inventoryservice.Repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Service
public class InventoryService {


    private final InventoryRepository inventoryRepository;

//    @Transactional(readOnly = true)
//    public boolean isInStock(String skuCode){
//        return inventoryRepository.findBySkuCode(skuCode).isPresent();
//    }

    @Transactional(readOnly = true)
    public List<InventoryResponse> isInStock(List<String> skuCode) {
        return inventoryRepository
                .findBySkuCodeIn(skuCode)
                .stream()
                .map(
                        inventory -> InventoryResponse
                                .builder()
                                .skuCode(inventory.getSkuCode())
                                .isInStock(inventory.getQuantity() > 0)
                                .build()
                )
                .collect(Collectors.toList());
    }


}
