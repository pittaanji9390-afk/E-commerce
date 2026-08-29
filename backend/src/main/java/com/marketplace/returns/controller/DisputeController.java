package com.marketplace.returns.controller;

import com.marketplace.returns.dto.CreateDisputeRequest;
import com.marketplace.returns.dto.DisputeDto;
import com.marketplace.returns.service.ReturnService;
import com.marketplace.security.UserPrincipal;
import com.marketplace.shared.response.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Dispute Mediation", description = "Endpoints for escalating buyer/seller disagreements to platform arbitration")
@RestController
@RequestMapping("/api/v1/disputes")
@RequiredArgsConstructor
public class DisputeController {

    private final ReturnService returnService;

    @Operation(summary = "Open a mediation dispute for an order or rejected return")
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Result<DisputeDto>> createDispute(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody CreateDisputeRequest request) {
        DisputeDto dispute = returnService.createDispute(principal.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(Result.ok(dispute, "Dispute case opened for arbitration."));
    }
}
