package com.mo.moyeo.domain.exchange.reservation.controller;

import com.mo.moyeo.domain.auth.security.dto.CustomUserDetails;
import com.mo.moyeo.domain.exchange.reservation.dto.ExchangeReserveDto;
import com.mo.moyeo.domain.exchange.reservation.dto.ExchangeReserveListDto;
import com.mo.moyeo.domain.exchange.reservation.service.ReservedExchangeService;
import com.mo.moyeo.domain.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/exchanges/reservations")
@Tag(name = "ReservedExchangeController", description = "예약 환전 관련 기능 제공")
public class ReservedExchangeController {
    private final ReservedExchangeService reservedExchangeService;

    @PostMapping
    @Operation(summary = "예약 환전 등록", description = "예약 환전을 등록.")
    public ResponseEntity<?> reserveExchange(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody ExchangeReserveDto exchangeReserveDto) {
        User user = customUserDetails.getUser();
        reservedExchangeService.reserveExchange(user, exchangeReserveDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{boxId}")
    @Operation(summary = "박스에 걸린 예약환전 조회", description = "박스에 걸렸던 예약환전 목록 조회")
    public ResponseEntity<List<ExchangeReserveListDto>> getReservations(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestParam Long boxId){
        User user = customUserDetails.getUser();
        return ResponseEntity.ok(reservedExchangeService.getReservations(user, boxId));
    }

    @DeleteMapping("/{reservationId}")
    @Operation(summary = "예약 환전 취소", description = "예약 환전 취소.")
    public ResponseEntity<?> cancelReservation(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestParam Long reservationId
    ){
        User user = customUserDetails.getUser();
        reservedExchangeService.cancelReservation(user, reservationId);
        return ResponseEntity.ok().build();
    }
}
