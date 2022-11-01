package com.microservices.Inventoryservice.Controller;


import com.microservices.Inventoryservice.DataTransferObject.InventoryResponse;
import com.microservices.Inventoryservice.Service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

//    @GetMapping("/{skuCode}")
//    @ResponseStatus(HttpStatus.OK)
//    public boolean isInStock(@PathVariable("skuCode") String skuCode){
//        return inventoryService.isInStock(skuCode);
//    }


    @GetMapping
    @ResponseStatus(HttpStatus.OK)
//    http://localhost:8082/api/inventory/?skuCode=Iphone 13&skuCode=Iphone 14
    public List<InventoryResponse> isInStock(@RequestParam List<String> skuCode) {
        return inventoryService.isInStock(skuCode);
    }

}
