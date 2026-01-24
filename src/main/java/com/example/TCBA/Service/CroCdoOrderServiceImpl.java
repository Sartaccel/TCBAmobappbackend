package com.example.TCBA.Service;

import com.example.TCBA.Entity.CroCdoOrder;
import com.example.TCBA.Repository.BrokerLoginRepository;
import com.example.TCBA.Repository.CroCdoOrderRepository;
import com.example.TCBA.Request.*;
import com.example.TCBA.Response.YardDropdownResponse;
import com.example.TCBA.Response.YardDropdownView;
import com.example.TCBA.Util.CommonUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
@RequiredArgsConstructor
@Service
@Transactional
public class CroCdoOrderServiceImpl implements CroCdoOrderService {

    private final CroCdoOrderRepository repository;
    private final CarryzenApiClient carryzenApiClient;
    private final CommonUtil commonUtil;
    private final BrokerLoginRepository brokerLoginRepository;
    private final EntryNumberService entryNumberService;

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
    public ResponseEntity<String> createOrder(
            List<CroCdoOrderRequest> request) {

        List<CroCdoOrder> dbList = new ArrayList<>();
        List<DoApiRequest> apiList = new ArrayList<>();

        for (CroCdoOrderRequest req : request) {

            // generate values
            CroCdoOrder order = mapToEntity(req);

            // DB list
            dbList.add(order);

            // third-party payload
            apiList.add(mapToDoApi(order));
        }

        // ✅ send ONLY generated data
        ResponseEntity<String> response =
                carryzenApiClient.sendDoEntry(apiList);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Carryzen API failed");
        }

        // ✅ save DB after API success
        repository.saveAll(dbList);

        return response;
    }


    @Transactional
    public ResponseEntity<String> createRoOrder(
            List<CroOrderRequest> request) {

        List<CroCdoOrder> dbList = new ArrayList<>();
        List<RoApiRequest> apiList = new ArrayList<>();

        for (CroOrderRequest req : request) {

            CroCdoOrder order = mapToEntityRo(req);

            dbList.add(order);
            apiList.add(mapToRoApi(order));
        }

        ResponseEntity<String> response =
                carryzenApiClient.sendRoEntry(apiList);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Carryzen RO API failed");
        }

        repository.saveAll(dbList);

        return response;
    }


    public List<YardDropdownResponse> getYards() {

        List<YardDropdownView> list =
                brokerLoginRepository.findByStackHoldersType_Id(3L);

        return list.stream()
                .map(v -> new YardDropdownResponse(
                        v.getStackHolderId(),
                        v.getLegalName()
                ))
                .toList();
    }

    private CroCdoOrder mapToEntity(CroCdoOrderRequest req) {

        CroCdoOrder o = new CroCdoOrder();

        o.setEntryType("IN");
        o.setEntryNumber(entryNumberService.generate("DO"));
        o.setEntryDate(
                LocalDateTime.ofInstant(
                        Instant.now(),
                        ZoneOffset.UTC
                )
        );        o.setLoginCode(req.getLoginCode());
        o.setContainerNo(req.getContainerNo());
        o.setContainerSize(req.getContainerSize());
        o.setSealNo(req.getSealNo());
        o.setMovementType("IMPORT");
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

    private DoApiRequest mapToDoApi(CroCdoOrder o) {

        DoApiRequest api = new DoApiRequest();

        api.setLoginCode(o.getLoginCode());
        api.setEntryType(o.getEntryType());
        api.setEntryNumber(o.getEntryNumber());

        api.setEntryDate(
                o.getEntryDate()
                        .atZone(ZoneOffset.UTC)
                        .truncatedTo(ChronoUnit.SECONDS)
                        .toInstant()
                        .toString()
        );

        api.setContainerNo(o.getContainerNo());
        api.setContainerSize(o.getContainerSize());
        api.setSealNo(o.getSealNo());
        api.setMovementType(o.getMovementType());

        api.setYardCode(o.getYardCode());
        api.setChaCode(o.getChaCode());
        api.setLinerCode(o.getLinerCode());

        api.setChaCompanyName(o.getChaCompanyName());
        api.setLineCompanyName(o.getLineCompanyName());
        api.setYardCompanyName(o.getYardCompanyName());

        api.setTransportName(o.getTransportName());
        api.setTransportCode(o.getTransportCode());

        api.setTotalContainers(o.getTotalContainers());
        api.setCount20ft(o.getCount20ft());
        api.setCount40ft(o.getCount40ft());

        return api;
    }

    private CroCdoOrder mapToEntityRo(CroOrderRequest req) {

        CroCdoOrder o = new CroCdoOrder();

        o.setEntryType("OUT");
        o.setEntryNumber(entryNumberService.generate("RO"));
        o.setEntryDate(LocalDateTime.now());

        o.setLoginCode(req.getLoginCode());

        o.setYardCode(req.getYardCode());
        o.setYardCompanyName(req.getYardCompanyName());

        o.setChaCode(req.getChaCode());
        o.setChaCompanyName(req.getChaCompanyName());

        o.setLinerCode(req.getLinerCode());
        o.setLineCompanyName(req.getLineCompanyName());

        o.setTransportCode(req.getTransportCode());
        o.setTransportName(req.getTransportName());

        o.setTotalContainers(req.getNoOfContainer());
        o.setCount20ft(req.getCount20ft());
        o.setCount40ft(req.getCount40ft());

        return o;
    }

    private RoApiRequest mapToRoApi(CroCdoOrder o) {

        RoApiRequest api = new RoApiRequest();

        api.setLoginCode(o.getLoginCode());

        api.setEntryType(o.getEntryType());
        api.setEntryNumber(o.getEntryNumber());

        api.setEntryDate(
                o.getEntryDate()
                        .atZone(ZoneOffset.UTC)
                        .truncatedTo(ChronoUnit.SECONDS)
                        .toInstant()
                        .toString()
        );

        api.setYardCode(o.getYardCode());
        api.setYardCompanyName(o.getYardCompanyName());

        api.setChaCode(o.getChaCode());
        api.setChaCompanyName(o.getChaCompanyName());

        api.setLinerCode(o.getLinerCode());
        api.setLineCompanyName(o.getLineCompanyName());

        api.setTransportCode(o.getTransportCode());
        api.setTransportName(o.getTransportName());

        api.setNoOfContainer(o.getTotalContainers());
        api.setCount20ft(o.getCount20ft());
        api.setCount40ft(o.getCount40ft());

        return api;
    }


}