package com.aiplatform.sentinel.controller;

import com.aiplatform.sentinel.common.ApiResponse;
import com.aiplatform.sentinel.common.ApiResponseBuilder;
import com.aiplatform.sentinel.dto.request.CreateIncidentRequest;
import com.aiplatform.sentinel.dto.response.IncidentResponse;
import com.aiplatform.sentinel.service.IncidentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/incidents")
@Tag(name = "Incident API", description = "Operations related to Incident Management")
@RequiredArgsConstructor
public class IncidentController {

        private final IncidentService incidentService;

        @Operation(summary = "Create Incident", description = "Creates a new incident in the system.")
        @PostMapping
        public ResponseEntity<ApiResponse<IncidentResponse>> createIncident(
                        @Valid @RequestBody CreateIncidentRequest request) {

                IncidentResponse response = incidentService.createIncident(request);

                return ResponseEntity.status(HttpStatus.CREATED)
                                .body(ApiResponseBuilder.success(
                                                "Incident created successfully",
                                                response));
        }

        @Operation(summary = "Get All Incidents")
        @GetMapping
        public ResponseEntity<ApiResponse<List<IncidentResponse>>> getAllIncidents() {

                return ResponseEntity.ok(
                                ApiResponseBuilder.success(
                                                "Incidents fetched successfully",
                                                incidentService.getAllIncidents()));
        }

        @Operation(summary = "Get Incident By Id")
        @GetMapping("/{id}")
        public ResponseEntity<ApiResponse<IncidentResponse>> getIncident(
                        @PathVariable UUID id) {

                return ResponseEntity.ok(
                                ApiResponseBuilder.success(
                                                "Incident fetched successfully",
                                                incidentService.getIncident(id)));
        }

        @Operation(summary = "Delete Incident")
        @DeleteMapping("/{id}")
        public ResponseEntity<ApiResponse<Void>> deleteIncident(
                        @PathVariable UUID id) {

                incidentService.deleteIncident(id);

                return ResponseEntity.ok(
                                ApiResponseBuilder.success(
                                                "Incident deleted successfully",
                                                null));
        }
}