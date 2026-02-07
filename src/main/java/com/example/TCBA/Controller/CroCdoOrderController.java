package com.example.TCBA.Controller;

import com.example.TCBA.Request.*;
import com.example.TCBA.Response.LinerDropdownResponse;
import com.example.TCBA.Response.TransportDropdownResponse;
import com.example.TCBA.Response.YardDropdownResponse;
import com.example.TCBA.Service.CroCdoOrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tcba/cro-cdo")
public class CroCdoOrderController {

    private final CroCdoOrderService orderService;

    public CroCdoOrderController(CroCdoOrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/cdo/add")
    public ResponseEntity<String> createOrder(@RequestBody List<CroCdoOrderRequest> request) {
        return orderService.createOrder(request);
    }

    @PostMapping("/cro/add")
    public ResponseEntity<String> createRoOrder(@RequestBody List<CroOrderRequest> request) {
        return orderService.createRoOrder(request);
    }

    @PostMapping("/gate-containers")
    public ResponseEntity<String> fetchGateContainers(
            @RequestBody GateContainerSearchRequest request) {

        String response = orderService.fetchGateContainers(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/pending-containers")
    public ResponseEntity<String> pendingContainers(
            @RequestBody GateContainerSearchRequest request) {

        String response = orderService.pendingContainers(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/approve-reject")
    public ResponseEntity<String> approveContainers(
            @RequestBody ContainerApproveRequest request) {

        String response = orderService.approveContainers(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/Do-Ro-Entries")
    public ResponseEntity<String> fetchDoRoEntries(
            @RequestBody DoRoEntriesSearchRequest request) {

        String response = orderService.fetchDoRoEntries(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/yard/dropdown")
    public ResponseEntity<List<YardDropdownResponse>> getYards() {

        List<YardDropdownResponse> yards = orderService.getYards();

        return ResponseEntity.ok(yards);
    }

    @GetMapping("/liner/dropdown")
    public ResponseEntity<List<LinerDropdownResponse>> getLiner() {

        List<LinerDropdownResponse> liners = orderService.getLiner();

        return ResponseEntity.ok(liners);
    }

    @GetMapping("/transport/dropdown")
    public ResponseEntity<List<TransportDropdownResponse>> getTransport() {

        List<TransportDropdownResponse> transport = orderService.getTransport();

        return ResponseEntity.ok(transport);
    }
}