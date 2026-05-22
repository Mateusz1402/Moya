package com.ms.backend.controller;

import com.ms.backend.dto.*;
import com.ms.backend.service.FuelService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/fuel")
public class FuelController {

    private final FuelService fuelService;

    public FuelController(FuelService fuelService) {
        this.fuelService = fuelService;
    }

    // ─── Tank endpoints ────────────────────────────────────────────────

    @GetMapping("/tanks")
    public ResponseEntity<List<TankDto>> getAllTanks() {
        return ResponseEntity.ok(fuelService.getAllTanks());
    }

    @GetMapping("/tanks/{id}")
    public ResponseEntity<TankDto> getTankById(@PathVariable Long id) {
        return ResponseEntity.ok(fuelService.getTankById(id));
    }

    @PostMapping("/tanks")
    public ResponseEntity<TankDto> createTank(@RequestBody CreateTankRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(fuelService.createTank(request));
    }

    @PutMapping("/tanks/{id}")
    public ResponseEntity<TankDto> updateTank(@PathVariable Long id, @RequestBody CreateTankRequest request) {
        return ResponseEntity.ok(fuelService.updateTank(id, request));
    }

    @DeleteMapping("/tanks/{id}")
    public ResponseEntity<Void> deleteTank(@PathVariable Long id) {
        fuelService.deleteTank(id);
        return ResponseEntity.noContent().build();
    }

    // ─── Pump endpoints ────────────────────────────────────────────────

    @GetMapping("/pumps")
    public ResponseEntity<List<PumpDto>> getAllPumps() {
        return ResponseEntity.ok(fuelService.getAllPumps());
    }

    @GetMapping("/pumps/{id}")
    public ResponseEntity<PumpDto> getPumpById(@PathVariable Long id) {
        return ResponseEntity.ok(fuelService.getPumpById(id));
    }

    @PostMapping("/pumps")
    public ResponseEntity<PumpDto> createPump(@RequestBody CreatePumpRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(fuelService.createPump(request));
    }

    @PatchMapping("/pumps/{id}/status")
    public ResponseEntity<PumpDto> updatePumpStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String status = body.get("status");
        return ResponseEntity.ok(fuelService.updatePumpStatus(id, status));
    }

    @DeleteMapping("/pumps/{id}")
    public ResponseEntity<Void> deletePump(@PathVariable Long id) {
        fuelService.deletePump(id);
        return ResponseEntity.noContent().build();
    }

    // ─── PumpHose endpoints ────────────────────────────────────────────

    @GetMapping("/pumps/{pumpId}/hoses")
    public ResponseEntity<List<PumpHoseDto>> getHosesByPump(@PathVariable Long pumpId) {
        return ResponseEntity.ok(fuelService.getHosesByPump(pumpId));
    }

    @PostMapping("/hoses")
    public ResponseEntity<PumpHoseDto> createPumpHose(@RequestBody CreatePumpHoseRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(fuelService.createPumpHose(request));
    }

    @DeleteMapping("/hoses/{id}")
    public ResponseEntity<Void> deletePumpHose(@PathVariable Long id) {
        fuelService.deletePumpHose(id);
        return ResponseEntity.noContent().build();
    }

    // ─── Dispensing (simulation action) ────────────────────────────────

    @PostMapping("/dispense")
    public ResponseEntity<DispenseFuelResponse> dispenseFuel(@RequestBody DispenseFuelRequest request) {
        return ResponseEntity.ok(fuelService.dispenseFuel(request));
    }
}
