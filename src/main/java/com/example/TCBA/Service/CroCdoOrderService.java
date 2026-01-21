package com.example.TCBA.Service;

import com.example.TCBA.Entity.CroCdoOrder;
import com.example.TCBA.Request.CroCdoOrderRequest;
import com.example.TCBA.Request.DoRoEntriesSearchRequest;
import com.example.TCBA.Request.GateContainerSearchRequest;
import com.example.TCBA.Request.YardUnpaidRequest;
import com.example.TCBA.Response.YardApiResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface CroCdoOrderService {

    ResponseEntity<String> createOrder(List<CroCdoOrderRequest> requests);

    String fetchGateContainers(GateContainerSearchRequest request);

    String fetchDoRoEntries(DoRoEntriesSearchRequest request);

    YardApiResponse fetchUnpaidPayments(YardUnpaidRequest request);
}
