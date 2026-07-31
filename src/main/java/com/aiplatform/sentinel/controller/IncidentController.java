package com.aiplatform.sentinel.controller;

import com.aiplatform.sentinel.common.ApiResponse;
import com.aiplatform.sentinel.common.ApiResponseBuilder;
import com.aiplatform.sentinel.common.PageResponse;
import com.aiplatform.sentinel.config.PaginationProperties;
import com.aiplatform.sentinel.dto.request.CreateIncidentRequest;
import com.aiplatform.sentinel.dto.request.UpdateIncidentRequest;
import com.aiplatform.sentinel.dto.response.IncidentResponse;
import com.aiplatform.sentinel.enums.Severity;
import com.aiplatform.sentinel.enums.Status;
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
        private final PaginationProperties paginationProperties;

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

        @GetMapping
        public ApiResponse<PageResponse<IncidentResponse>> getIncidents(
                        @RequestParam(required = false) Severity severity,
                        @RequestParam(required = false) Status status,
                        @RequestParam(required = false) String keyword,
                        @RequestParam(required = false) Integer page,
                        @RequestParam(required = false) Integer size,
                        @RequestParam(required = false) String sortBy,
                        @RequestParam(required = false) String sortDir) {

                return ApiResponse.success(
                                "Incidents fetched successfully",
                                incidentService.getIncidents(
                                                severity,
                                                status,
                                                keyword,
                                                page != null ? page : paginationProperties.getDefaultPage(),
                                                size != null ? size : paginationProperties.getDefaultSize(),
                                                sortBy != null ? sortBy : paginationProperties.getDefaultSortBy(),
                                                sortDir != null ? sortDir : paginationProperties.getDefaultSortDir()));
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

        @PatchMapping("/{id}")
        @ResponseStatus(HttpStatus.OK)
        public ApiResponse<IncidentResponse> updateIncident(
                        @PathVariable UUID id,
                        @Valid @RequestBody UpdateIncidentRequest request) {

                IncidentResponse response = incidentService.updateIncident(id, request);

                return ApiResponse.success("Incident updated successfully", response);
        }
}