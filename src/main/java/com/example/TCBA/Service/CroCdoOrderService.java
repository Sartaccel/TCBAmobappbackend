package com.example.TCBA.Service;

import com.example.TCBA.Entity.CroCdoOrder;
import com.example.TCBA.Request.*;
import com.example.TCBA.Response.LinerDropdownResponse;
import com.example.TCBA.Response.TransportDropdownResponse;
import com.example.TCBA.Response.YardDropdownResponse;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface CroCdoOrderService {

    ResponseEntity<String> createOrder(List<CroCdoOrderRequest> requests);

    String fetchGateContainers(GateContainerSearchRequest request);

    String approveContainers(ContainerApproveRequest request);

    String pendingContainers (GateContainerSearchRequest request);

    String fetchDoRoEntries(DoRoEntriesSearchRequest request);

    ResponseEntity<String> createRoOrder(List<CroOrderRequest> request);

    List<YardDropdownResponse> getYards();

    List<LinerDropdownResponse> getLiner();

    List<TransportDropdownResponse> getTransport();

    Page<CroCdoOrder> getOrders(String stackHolderId, GateContainerSearchRequest request);
}
