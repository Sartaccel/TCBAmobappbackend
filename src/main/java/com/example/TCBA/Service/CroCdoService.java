package com.example.TCBA.Service;

import com.example.TCBA.Entity.ContainerDetail;
import com.example.TCBA.Entity.CroCdoOrder;
import com.example.TCBA.Repository.ContainerDetailRepository;
import com.example.TCBA.Repository.CroCdoRepository;
import com.example.TCBA.Request.ContainerCreateRequest;
import com.example.TCBA.Request.CroCdoCreateRequest;
import com.example.TCBA.Response.ContainerResponse;
import com.example.TCBA.Response.CroCdoCreateResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CroCdoService {

    private final CroCdoRepository orderRepo;
    private final ContainerDetailRepository containerRepo;

    public CroCdoCreateResponse createOrder(CroCdoCreateRequest req) {

        // 1️⃣ Create order
        CroCdoOrder order = new CroCdoOrder();
        order.setOrderType(req.getOrderType());
        order.setDepot(req.getDepot());
        order.setLinerName(req.getLinerName());
        order.setTransporterName(req.getTransporterName());
        order.setSvcType(req.getSvcType());
        order.setCreatedAt(LocalDateTime.now());

        // 2️⃣ 🔹 CRO-specific fields (NEW – does NOT affect CDO)
        if ("CRO".equalsIgnoreCase(req.getOrderType())) {
            order.setTotalContainers(req.getTotalContainers());
            order.setContainer20FtCount(req.getContainer20FtCount());
            order.setContainer40FtCount(req.getContainer40FtCount());
        }

        // 3️⃣ Save order
        orderRepo.save(order);

        List<ContainerResponse> containerResponses = new ArrayList<>();

        // 4️⃣ 🔹 CDO-specific logic (EXISTING – unchanged)
        if ("CDO".equalsIgnoreCase(req.getOrderType())
                && req.getContainers() != null) {

            for (ContainerCreateRequest c : req.getContainers()) {

                ContainerDetail cd = new ContainerDetail();
                cd.setContainerNumber(c.getContainerNumber());
                cd.setContainerSize(c.getContainerSize());
                cd.setCustomerName(c.getCustomerName());
                cd.setSealNo(c.getSealNo());
                cd.setOrder(order);

                containerRepo.save(cd);

                containerResponses.add(
                        ContainerResponse.builder()
                                .id(cd.getId())
                                .containerNumber(cd.getContainerNumber())
                                .containerSize(cd.getContainerSize())
                                .customerName(cd.getCustomerName())
                                .sealNo(cd.getSealNo())
                                .build()
                );
            }
        }

        // 5️⃣ Response (same as before)
        return CroCdoCreateResponse.builder()
                .orderId(order.getId())
                .orderType(order.getOrderType())
                .depot(order.getDepot())
                .linerName(order.getLinerName())
                .transporterName(order.getTransporterName())
                .svcType(order.getSvcType())
                .containers(containerResponses)
                .build();
    }
}
