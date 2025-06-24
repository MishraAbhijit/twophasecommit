package com.abhijit.inventoryservice.api;

import com.abhijit.inventoryservice.dto.OrderRequest;
import com.abhijit.inventoryservice.service.InventoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @PostMapping("/prepare")
    public ResponseEntity<String> prepare(@RequestBody OrderRequest request) {
        inventoryService.prepareInventory(request);
        return ResponseEntity.ok("Inventory prepared");
    }

    @PostMapping("/commit")
    public ResponseEntity<String> commit(@RequestBody OrderRequest request) {
        inventoryService.commitInventory(request);
        return ResponseEntity.ok("Inventory committed");
    }

    @PostMapping("/rollback")
    public ResponseEntity<String> rollback(@RequestBody OrderRequest request) {
        inventoryService.rollbackInventory(request);
        return ResponseEntity.ok("Inventory rolled back");
    }

}
