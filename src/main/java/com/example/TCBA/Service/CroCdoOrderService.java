package com.example.TCBA.Service;

import com.example.TCBA.Entity.CroCdoOrder;
import com.example.TCBA.Request.CroCdoOrderRequest;
import com.example.TCBA.Request.DoRoEntriesSearchRequest;
import com.example.TCBA.Request.GateContainerSearchRequest;
import org.springframework.http.ResponseEntity;

public interface CroCdoOrderService {

    ResponseEntity<String> createOrder(CroCdoOrderRequest request);

    String fetchGateContainers(GateContainerSearchRequest request);

    String fetchDoRoEntries(DoRoEntriesSearchRequest request);
}
