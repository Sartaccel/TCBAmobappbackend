package com.example.TCBA.Service;

import com.example.TCBA.Entity.CroCdoOrder;
import com.example.TCBA.Repository.CroCdoOrderRepository;
import com.example.TCBA.Request.CroCdoOrderRequest;
import com.example.TCBA.Request.DoRoEntriesSearchRequest;
import com.example.TCBA.Request.GateContainerSearchRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CroCdoOrderServiceImpl implements CroCdoOrderService {

    private final CroCdoOrderRepository repository;
    private final CarryzenApiClient carryzenApiClient;

    public CroCdoOrderServiceImpl(CroCdoOrderRepository repository,
                                  CarryzenApiClient carryzenApiClient) {
        this.repository = repository;
        this.carryzenApiClient = carryzenApiClient;
    }

    @Override
    public String fetchGateContainers(GateContainerSearchRequest request) {
        return carryzenApiClient.fetchGateContainers(request).getBody();
    }


    @Override
    public String fetchDoRoEntries(DoRoEntriesSearchRequest request) {
        return carryzenApiClient.fetchDoRoEntries(request).getBody();
    }


    @Transactional
    @Override
    public ResponseEntity<String> createOrder(CroCdoOrderRequest request) {

        CroCdoOrder order = mapToEntity(request);
        repository.save(order);

        ResponseEntity<String> carryzenResponse =
                carryzenApiClient.sendDoRoEntry(request);

        if (!carryzenResponse.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Carryzen API failed");
        }

        return carryzenResponse;
    }


    private CroCdoOrder mapToEntity(CroCdoOrderRequest req) {
        CroCdoOrder o = new CroCdoOrder();
        o.setLoginCode(req.getLoginCode());
        o.setEntryType(req.getEntryType());
        o.setEntryNumber(req.getEntryNumber());
        o.setEntryDate(req.getEntryDate());
        o.setContainerNo(req.getContainerNo());
        o.setContainerSize(req.getContainerSize());
        o.setSealNo(req.getSealNo());
        o.setMovementType(req.getMovementType());
        o.setYardCode(req.getYardCode());
        o.setChaCode(req.getChaCode());
        o.setLinerCode(req.getLinerCode());
        o.setYardCompanyName(req.getYardCompanyName());
        o.setChaCompanyName(req.getChaCompanyName());
        o.setLineCompanyName(req.getLineCompanyName());
        o.setTransportName(req.getTransportName());
        o.setTransportCode(req.getTransportCode());
        o.setTotalContainers(req.getTotalContainers());
        o.setCount20ft(req.getCount20ft());
        o.setCount40ft(req.getCount40ft());
        return o;
    }
}

