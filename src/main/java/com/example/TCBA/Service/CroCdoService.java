package com.example.TCBA.Service;

import com.example.TCBA.Entity.CroCdoOrder;
import com.example.TCBA.Repository.CroCdoRepository;
import com.example.TCBA.Request.CroCdoRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CroCdoService {

    private final CroCdoRepository repository;

    public CroCdoOrder saveOrder(CroCdoRequest request) {

        // 🔒 Validation
        if (request.getOrderType() == null) {
            throw new RuntimeException("Order type is required");
        }

        if ("CRO".equalsIgnoreCase(request.getOrderType())) {
            if (request.getNoOfContainer() == null) {
                throw new RuntimeException("No of container is required for CRO");
            }
        }

        if ("CDO".equalsIgnoreCase(request.getOrderType())) {
            if (request.getContainerNumber() == null) {
                throw new RuntimeException("Container number is required for CDO");
            }
        }

        CroCdoOrder order = new CroCdoOrder();
        order.setOrderType(request.getOrderType());
        order.setDepot(request.getDepot());
        order.setLinerName(request.getLinerName());
        order.setTransporterName(request.getTransporterName());

        // CRO mapping
        if ("CRO".equalsIgnoreCase(request.getOrderType())) {
            order.setNoOfContainer(request.getNoOfContainer());
            order.setContainer20ft(request.getContainer20ft());
            order.setContainer40ft(request.getContainer40ft());
        }

        // CDO mapping
        if ("CDO".equalsIgnoreCase(request.getOrderType())) {
            order.setContainerNumber(request.getContainerNumber());
            order.setContainerSize(request.getContainerSize());
            order.setCustomerName(request.getCustomerName());
            order.setSealNo(request.getSealNo());
            order.setSvcType(request.getSvcType());
        }

        return repository.save(order);
    }
}

