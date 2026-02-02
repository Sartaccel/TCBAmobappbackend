//package com.example.TCBA.Controller;
//
//import com.example.TCBA.Request.ChaDashboardRequest;
//import com.example.TCBA.Service.DashboardService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequestMapping("/dashboard")
//@RequiredArgsConstructor
//public class DashboardController {
//
//    private final DashboardService dashboardService;
//
//    @PostMapping("/cha")
//    public ResponseEntity<?> getDashboard(
//            @RequestBody ChaDashboardRequest request) {
//
//        return ResponseEntity.ok(
//                dashboardService.getChaDashboard(request)
//        );
//    }
//}

package com.example.TCBA.Controller;

import com.example.TCBA.Request.ChaDashboardRequest;
import com.example.TCBA.Service.CarryzenApiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cha")
@RequiredArgsConstructor
public class DashboardController {

    private final CarryzenApiClient carryzenApiClient;

    @PostMapping("/dashboard")
    public ResponseEntity<String> getChaDashboard(@RequestBody ChaDashboardRequest request) {
        return carryzenApiClient.fetchChaDashboard(request);
    }
}
