package com.harshpatil.projects.airBnbProject.controller;

import com.harshpatil.projects.airBnbProject.dto.InventoryDto;
import com.harshpatil.projects.airBnbProject.dto.UpdateInventoryRequestDto;
import com.harshpatil.projects.airBnbProject.services.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping("/rooms/{roomId}")
    public ResponseEntity<List<InventoryDto>> getAllInventoryByRoomId(@PathVariable Long roomId)
    {
        return ResponseEntity.ok(inventoryService.getAllInventoryByRoomId(roomId));
    }

    @PatchMapping("/rooms/{roomId}")
    public ResponseEntity<Void> updateInventory(@PathVariable Long roomId ,@RequestBody UpdateInventoryRequestDto updateInventoryRequestDto)
    {
        inventoryService.updateInventory(roomId,updateInventoryRequestDto);
        return ResponseEntity.noContent().build();
    }
}
