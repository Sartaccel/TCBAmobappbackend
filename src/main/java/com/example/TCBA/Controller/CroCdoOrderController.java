package com.example.TCBA.Controller;

import com.example.TCBA.Entity.CroCdoOrder;
import com.example.TCBA.Request.CroCdoOrderRequest;
import com.example.TCBA.Request.DoRoEntriesSearchRequest;
import com.example.TCBA.Request.GateContainerSearchRequest;
import com.example.TCBA.Request.YardUnpaidRequest;
import com.example.TCBA.Response.YardApiResponse;
import com.example.TCBA.Service.CroCdoOrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/tcba/cro-cdo")
public class CroCdoOrderController {

    private final CroCdoOrderService orderService;

    public CroCdoOrderController(CroCdoOrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/add")
    public ResponseEntity<String> createOrder(@RequestBody List<CroCdoOrderRequest> request) {
        return orderService.createOrder(request);
    }

    @PostMapping("/gate-containers")
    public ResponseEntity<String> fetchGateContainers(
            @RequestBody GateContainerSearchRequest request) {

        String response = orderService.fetchGateContainers(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/Do-Ro-Entries")
    public ResponseEntity<String> fetchDoRoEntries(
            @RequestBody DoRoEntriesSearchRequest request) {

        String response = orderService.fetchDoRoEntries(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/unpaid")
    public ResponseEntity<YardApiResponse> fetchUnpaidPayments(
            @RequestBody YardUnpaidRequest request) {

        YardApiResponse response =
                orderService.fetchUnpaidPayments(request);

        return ResponseEntity.ok(response);
    }
}