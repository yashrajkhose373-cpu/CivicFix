package com.studysync.controller;

import com.studysync.dto.ApiResponse;
import com.studysync.dto.DashboardResponseDto;
import com.studysync.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    public ResponseEntity<ApiResponse<DashboardResponseDto>> getDashboard() {
        DashboardResponseDto dashboard = dashboardService.getDashboardData();
        return ResponseEntity.ok(ApiResponse.success(dashboard));
    }
}
